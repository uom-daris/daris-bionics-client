package daris.client.bionics;

import daris.client.ClientApplication;

public abstract class BionicsClientApplication extends ClientApplication {

    public static final String SETTINGS_FILE_PATH = System.getProperty("user.home")
            + "/.daris/daris-bionics-client.properties";

    protected BionicsClientApplication(String appName) {
        super(SETTINGS_FILE_PATH, appName);
    }

}
