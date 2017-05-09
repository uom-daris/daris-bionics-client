package daris.client.bionics;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import arc.mf.client.ServerClient.Connection;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import daris.client.ClientApplication;
import daris.client.ConnectionBuilder;
import daris.client.pssd.SubjectUtils;

//@formatter:off
/*
Project: < EthicsOrg _Ethics#> ……………………UOMHESC_1646801
       Subject: <pilot/Expt_initialsOfParticipants>…Pilot_NP or Expt_NP
               Session: <YYYYMMMDD_Location>…2016May16_CfNE
                        Method: <Instrument>……….........EEG_Compumedics or EEG_gUSBamp
                               Data set:<All files>…...............*.hdf5,*.dat
*/
//@formatter:on
/**
 * 
 * @author wliu5
 *
 */
public class BionicsSubjectUpload extends BionicsClientApplication {

    public static final String CMD_NAME = "bionics-subject-upload";

    public static void main(String[] args) {
        ClientApplication.run(new BionicsSubjectUpload(), args);
    }

    @Override
    public final String commandName() {
        return CMD_NAME;
    }

    @Override
    protected void execute(Connection cxn, String[] args) throws Throwable {

        String pid = null;
        Boolean derived = null;
        List<File> subjectDirs = new ArrayList<File>();
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
                File subjectDir = new File(args[i]);
                if (!subjectDir.exists()) {
                    throw new FileNotFoundException("Input subject directory/file does not exist.");
                }
                if (!subjectDir.isDirectory()) {
                    throw new IllegalArgumentException(subjectDir.getCanonicalPath() + " is not a directory.");
                }
                subjectDirs.add(subjectDir);
                i++;
            }
        }
        if (pid == null) {
            throw new IllegalArgumentException("Missing --pid argument.");
        }
        if (subjectDirs.isEmpty()) {
            throw new IllegalArgumentException("Missing input subject directory.");
        }
        if (derived == null) {
            derived = false;
        }
        uploadSubjects(cxn, pid, subjectDirs, derived);
    }

    static List<String> uploadSubjects(Connection cxn, String projectCid, List<File> subjectDirs, Boolean derived)
            throws Throwable {
        List<String> subjectCids = new ArrayList<String>(subjectDirs.size());
        for (File studyDir : subjectDirs) {
            String subjectCid = uploadSubject(cxn, projectCid, studyDir, derived);
            subjectCids.add(subjectCid);
        }
        return subjectCids;
    }

    private static String uploadSubject(Connection cxn, String projectCid, File subjectDir, Boolean derived)
            throws Throwable {

        System.out.println();
        System.out.println("- Uploading subject from \"" + subjectDir.getCanonicalPath() + "\"...");
        String subjectCid = findSubject(cxn, projectCid, subjectDir);
        if (subjectCid == null) {
            subjectCid = SubjectUtils.createSubject(cxn, projectCid, null, subjectDir.getName(), subjectDir.getName());
            System.out.println("- Created subject " + subjectCid);
        } else {
            System.out.println("- Found existing subject " + subjectCid + "(name='" + subjectDir.getName() + "')");
        }
        File[] studyDirs = subjectDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
        if (studyDirs != null && studyDirs.length > 0) {
            BionicsStudyUpload.uploadStudies(cxn, subjectCid, Arrays.asList(studyDirs), derived);
        } else {
            System.out.println("- No study directory found in \"" + subjectDir.getCanonicalPath() + "\"!");
        }
        return subjectCid;
    }

    private static String findSubject(Connection cxn, String projectCid, File subjectDir) throws Throwable {
        String subjectName = subjectDir.getName();
        XmlStringWriter w = new XmlStringWriter();
        w.add("where", "cid in '" + projectCid + "' and model='om.pssd.subject' and xpath(daris:pssd-object/name)='"
                + subjectName + "'");
        w.add("action", "get-cid");
        XmlDoc.Element re = cxn.execute("asset.query", w.document());
        if (re.elementExists("cid")) {
            if (re.count("cid") > 1) {
                throw new Exception("Multiple subjects(name=" + subjectName + ") found in project " + projectCid + "!");
            }
            return re.value("cid");
        } else {
            return null;
        }
    }

    @Override
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println(String.format("Usage: %s [mediaflux-arguments] --pid <project-cid> <subject-dir>", commandName()));
        ps.println();
        ps.println("    Arguments:");
        ps.println("    --pid <project-cid>                   - The citeable id of the parent project.");

        ConnectionBuilder.desribeCommandArgs(ps);
    }

}
