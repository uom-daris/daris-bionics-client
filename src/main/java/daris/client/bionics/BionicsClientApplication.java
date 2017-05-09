package daris.client.bionics;

import daris.client.ClientApplication;

public abstract class BionicsClientApplication extends ClientApplication {

    public static final String APP_NAME = "daris-bionics-client";
    
    public static final String SETTINGS_FILE_PATH = System.getProperty("user.home")
            + "/.daris/daris-bionics-client.properties";

    protected BionicsClientApplication() {
        super(SETTINGS_FILE_PATH, APP_NAME);
    }

}
