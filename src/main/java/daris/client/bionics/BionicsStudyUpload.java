package daris.client.bionics;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import arc.mf.client.AuthenticationDetails;
import arc.mf.client.ServerClient;
import arc.mf.client.ServerClient.Connection;
import arc.xml.XmlDoc;
import arc.xml.XmlDocMaker;
import arc.xml.XmlStringWriter;
import daris.client.ClientApplication;
import daris.client.ConnectionBuilder;
import daris.client.pssd.CiteableIdUtils;
import daris.client.pssd.StudyUtils;
import daris.client.util.ConnectionUtils;

// @formatter:off
/*
    Project: < EthicsOrg _Ethics#> ……………………UOMHESC_1646801
            Subject: <pilot/Expt_initialsOfParticipants>…Pilot_NP or Expt_NP
                    Session: <YYYYMMMDD_Location>…2016May16_CfNE
                             Method: <Instrument>……….........EEG_Compumedics or EEG_gUSBamp
                                    Data set:<All files>…...............*.hdf5,*.dat
*/
// @formatter:on

/**
 * 
 * @author wliu5
 *
 */
public class BionicsStudyUpload extends BionicsClientApplication {

    public static final String CMD_NAME = "bionics-study-upload";

    public static void main(String[] args) {
        ClientApplication.run(new BionicsStudyUpload(), args);
    }

    @Override
    public final String commandName() {
        return CMD_NAME;
    }

    @Override
    protected void execute(Connection cxn, String[] args) throws Throwable {

        String pid = null;
        Boolean derived = null;
        List<File> studyDirs = new ArrayList<File>();
        for (int i = 0; i < args.length;) {
            if ("--pid".equals(args[i])) {
                if (pid != null) {
                    throw new IllegalArgumentException("--pid argument has already been specified.");
                }
                pid = args[i + 1];
                i += 2;
            } else if ("--derived".equals(args[i])) {
                if (derived != null) {
                    throw new IllegalArgumentException("--derived argument has already been specified.");
                }
                derived = true;
                i++;
            } else {
                File studyDir = new File(args[i]);
                if (!studyDir.exists()) {
                    throw new FileNotFoundException("Input study/session directory does not exist.");
                }
                if (!studyDir.isDirectory()) {
                    throw new IllegalArgumentException(studyDir.getCanonicalPath() + " is not a directory.");
                }
                studyDirs.add(studyDir);
                i++;
            }
        }
        if (pid == null) {
            throw new IllegalArgumentException("Missing --pid argument.");
        }
        if (studyDirs.isEmpty()) {
            throw new IllegalArgumentException("Missing input study/session directory.");
        }
        if (derived == null) {
            derived = false;
        }
        uploadStudies(cxn, pid, studyDirs, derived);
    }

    static List<String> uploadStudies(ServerClient.Connection cxn, String subjectCid, List<File> studyDirs,
            boolean derived) throws Throwable {
        List<String> studyCids = new ArrayList<String>(studyDirs.size());
        for (File studyDir : studyDirs) {
            String studyCid = uploadStudy(cxn, subjectCid, studyDir, derived);
            studyCids.add(studyCid);
        }
        return studyCids;
    }

    private static String uploadStudy(ServerClient.Connection cxn, String subjectCid, File studyDir, boolean derived)
            throws Throwable {

        System.out.println();
        System.out.println("  - Uploading study from \"" + studyDir.getCanonicalPath() + "\"...");
        String studyCid = findStudy(cxn, subjectCid, studyDir);
        if (studyCid == null) {
            studyCid = createStudy(cxn, subjectCid, studyDir);
            System.out.println("  - Created study " + studyCid);
        } else {
            System.out.println("  - Found existing study " + studyCid + "(name='" + studyDir.getName() + "')");
        }
        File[] datasetDirs = studyDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
        if (datasetDirs != null && datasetDirs.length > 0) {
            BionicsDatasetUpload.uploadDatasets(cxn, studyCid, Arrays.asList(datasetDirs), derived);
        } else {
            System.out.println("  - No dataset directory found in \"" + studyDir.getCanonicalPath() + "\"!");
        }
        return studyCid;
    }

    private static String createStudy(Connection cxn, String subjectCid, File studyDir) throws Throwable {
        AuthenticationDetails authenticationDetails = ConnectionUtils.authenticationDetails(cxn);
        String domain = authenticationDetails == null ? null : authenticationDetails.domain();
        String user = authenticationDetails == null ? null : authenticationDetails.userName();
        Date studyDate = parseStudyDate(studyDir);
        XmlDoc.Element meta = null;
        if (studyDate != null || (domain != null && user != null)) {
            XmlDocMaker dm = new XmlDocMaker("meta");
            dm.push("vicnode.daris:vicnode-study");
            if (domain != null && user != null) {
                dm.push("ingest");
                dm.add("domain", domain);
                dm.add("user", user);
                dm.add("date", new Date());
                dm.pop();
            }
            if (studyDate != null) {
                dm.addDateOnly("sdate", studyDate);
            }
            dm.pop();
            meta = dm.root();
        }
        return StudyUtils.createStudy(cxn, subjectCid, null, studyDir.getName(), studyDir.getName(), meta);
    }

    private static Date parseStudyDate(File studyDir) {
        try {
            String name = studyDir.getName();
            String d = name.split("_")[0].trim();
            if (d.length() == 8/* yyyyMMMd */) {
                return new SimpleDateFormat("yyyyMMMd").parse(d);
            } else {
                return new SimpleDateFormat("yyyyMMMdd").parse(d);
            }
        } catch (Throwable e) {
            return null;
        }
    }

    private static String findStudy(ServerClient.Connection cxn, String pid, File studyDir) throws Throwable {
        String studyName = studyDir.getName();
        XmlStringWriter w = new XmlStringWriter();
        w.add("where", "cid starts with '" + pid + "' and model='om.pssd.study' and xpath(daris:pssd-object/name)='"
                + studyName + "'");
        w.add("action", "get-cid");
        XmlDoc.Element re = cxn.execute("asset.query", w.document());
        if (re.elementExists("cid")) {
            if (re.count("cid") > 1) {
                throw new Exception("Multiple studies(name=" + studyName + ") found in "
                        + CiteableIdUtils.getTypeFromCID(pid) + " " + pid + "!");
            }
            return re.value("cid");
        } else {
            return null;
        }
    }

    @Override
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println(String.format("Usage: %s [mediaflux-arguments] --pid <parent-cid> <study-dir>", commandName()));
        ps.println();
        ps.println("    Arguments:");
        ps.println("    --pid <parent-cid>                    - The citeable id of the parent subject/ex-method.");

        ConnectionBuilder.desribeCommandArgs(ps);
    }

}
