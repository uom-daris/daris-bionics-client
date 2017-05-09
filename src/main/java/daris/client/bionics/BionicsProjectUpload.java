package daris.client.bionics;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

import arc.mf.client.ServerClient.Connection;
import arc.xml.XmlDoc;
import arc.xml.XmlStringWriter;
import daris.client.ClientApplication;
import daris.client.ConnectionBuilder;

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
public class BionicsProjectUpload extends BionicsClientApplication {

    public static final String CMD_NAME = "bionics-project-upload";

    public static void main(String[] args) {
        ClientApplication.run(new BionicsProjectUpload(), args);
    }

    @Override
    public final String commandName() {
        return CMD_NAME;
    }

    @Override
    protected void execute(Connection cxn, String[] args) throws Throwable {

        String projectCid = null;
        File projectDir = null;
        Boolean derived = null;
        for (int i = 0; i < args.length;) {
            if ("--id".equals(args[i])) {
                if (projectCid != null) {
                    throw new IllegalArgumentException("--id argument has already been specified.");
                }
                projectCid = args[i + 1];
                i += 2;
            } else if ("--derived".equals(args[i])) {
                if (derived != null) {
                    throw new IllegalArgumentException("--derived argument has already been specified.");
                }
                derived = true;
                i++;
            } else {
                if (projectDir != null) {
                    throw new IllegalArgumentException(
                            "More than one project directories were specified. Expects only one.");
                }
                projectDir = new File(args[i]);
                if (!projectDir.exists()) {
                    throw new FileNotFoundException(
                            "Input project directory: \"" + projectDir.getCanonicalPath() + "\" does not exist.");
                }
                if (!projectDir.isDirectory()) {
                    throw new IllegalArgumentException(projectDir.getCanonicalPath() + " is not a directory.");
                }
                i++;
            }
        }
        // if (projectCid == null) {
        // throw new IllegalArgumentException("Missing --id argument.");
        // }
        if (projectDir == null) {
            throw new IllegalArgumentException("Missing input project directory.");
        }
        if (derived == null) {
            derived = false;
        }
        uploadProject(cxn, projectCid, projectDir, derived);

    }

    static void uploadProject(Connection cxn, String projectCid, File projectDir, Boolean derived) throws Throwable {

        System.out.println();
        System.out.println("Uploading project from \"" + projectDir.getCanonicalPath() + "\"...");

        if (projectCid == null) {
            projectCid = findProject(cxn, projectDir, true);
            System.out.println("Found project (name=" + projectDir.getName() + ") " + projectCid);
        }
        File[] subjectDirs = projectDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
        });

        if (subjectDirs != null && subjectDirs.length > 0) {
            BionicsSubjectUpload.uploadSubjects(cxn, projectCid, Arrays.asList(subjectDirs), derived);
        } else {
            System.out.println("No subject directory found in \"" + projectDir.getCanonicalPath() + "\"!");
        }
    };

    private static String findProject(Connection cxn, File projectDir, boolean exceptionIfNotFound) throws Throwable {
        String projectName = projectDir.getName();
        XmlStringWriter w = new XmlStringWriter();
        w.add("where", "model='om.pssd.project' and xpath(daris:pssd-object/name)='" + projectName + "'");
        w.add("action", "get-cid");
        XmlDoc.Element re = cxn.execute("asset.query", w.document());
        if (re.elementExists("cid")) {
            if (re.count("cid") > 1) {
                throw new Exception("Multiple projects(name=" + projectName + ") found!");
            }
            return re.value("cid");
        } else {
            if (exceptionIfNotFound) {
                throw new Exception("Could not find project with name: " + projectName);
            }
            return null;
        }
    }

    @Override
    protected void printHelp(PrintStream ps) {
        ps.println();
        ps.println(String.format("Usage: %s [mediaflux-arguments] [--id <project-cid>] <project-dir>", commandName()));
        ps.println();
        ps.println("    Arguments:");
        ps.println(
                "    --id <project-cid>                    - The citeable id of the project. If not specified, it will look for the project with the same name as the project directory.");
        ConnectionBuilder.desribeCommandArgs(ps);
    }

}
