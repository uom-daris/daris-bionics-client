package daris.client.bionics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import arc.mf.client.ServerClient.Connection;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import daris.client.ClientApplication;
import daris.client.ConnectionBuilder;
import daris.client.pssd.ArchiveType;
import daris.client.pssd.CiteableIdUtils;
import daris.client.pssd.DatasetUtils;
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
public class BionicsDatasetUpload extends BionicsClientApplication {

    public static final String APP_NAME = "bionics-dataset-upload";

    public static void main(String[] args) {
        ClientApplication.run(new BionicsDatasetUpload(), args);
    }

    BionicsDatasetUpload() {
        super(APP_NAME);
    }

    @Override
    protected void execute(Connection cxn, String[] args) throws Throwable {
        String pid = null;
        Boolean derived = null;
        List<File> datasetDirs = new ArrayList<File>();
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
                File input = new File(args[i]);
                if (!input.exists()) {
                    throw new FileNotFoundException("Input dataset directory/file does not exist.");
                }
                datasetDirs.add(input);
                i++;
            }
        }
        if (pid == null) {
            throw new IllegalArgumentException("Missing --pid argument.");
        }
        if (datasetDirs.isEmpty()) {
            throw new IllegalArgumentException("Missing input dataset directory.");
        }
        if (derived == null) {
            derived = false;
        }
        uploadDatasets(cxn, pid, datasetDirs, derived);
    }

    static List<String> uploadDatasets(Connection cxn, String pid, List<File> datasetDirs, boolean derived)
            throws Throwable {
        List<String> datasetCids = new ArrayList<String>(datasetDirs.size());
        for (File datasetDir : datasetDirs) {
            String datasetCid = uploadDataset(cxn, pid, datasetDir, derived);
            datasetCids.add(datasetCid);
        }
        return datasetCids;
    }

    private static String uploadDataset(Connection cxn, String pid, File f, boolean derived) throws Throwable {
        String canonicalPath = f.getCanonicalPath();
        System.out.println();
        System.out.println("    - Uploading dataset from \"" + canonicalPath + "\"...");
        String datasetCid = findDataset(cxn, pid, f);
        if (datasetCid == null) {
            datasetCid = DatasetUtils.uploadDataset(cxn, pid, null, null, derived, f.getName(), f.getName(), f, false,
                    ArchiveType.AAR, null, null);
            System.out.println("    - Created dataset " + datasetCid + ".");
        } else {
            System.out.println("    - Dataset " + datasetCid + " orginated from \"" + canonicalPath
                    + "\"already exists. Skipped.");
        }
        return datasetCid;
    }

    private static String findDataset(Connection cxn, String pid, File f) throws Throwable {
        StringBuilder query = new StringBuilder("(cid starts with '" + pid + "' or cid='" + pid + "')");
        query.append(" and model='om.pssd.dataset'");
        query.append(" and xpath(daris:pssd-object/name)='" + f.getName() + "'");
        query.append(" and xpath(mf-note/note)='source: ");
        query.append(f.getCanonicalPath()).append("'");
        XmlStringWriter w = new XmlStringWriter();
        w.add("where", query);
        w.add("action", "get-cid");
        XmlDoc.Element re = cxn.execute("asset.query", w.document());
        if (re.elementExists("cid")) {
            if (re.count("cid") > 1) {
                throw new Exception("Multiple datasets(name='" + f.getName() + "', source='" + f.getCanonicalPath()
                        + "') found in " + CiteableIdUtils.getTypeFromCID(pid) + " " + pid + "!");
            }
            return re.value("cid");
        } else {
            return null;
        }
    }

    @Override
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println(String.format("Usage: %s [mediaflux-arguments] --pid <study-cid> <dataset-dir>", name()));
        ConnectionBuilder.desribeCommandArgs(ps);
    }

}
