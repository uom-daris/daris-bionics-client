package daris.client.util;

import arc.mf.client.AuthenticationDetails;
import arc.mf.client.ConnectionDetails;
import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;

public class ConnectionUtils {

    public static AuthenticationDetails authenticationDetails(ServerClient.Connection cxn) {
        try {
            return ((RemoteServer.Connection) cxn).authenticationDetails();
        } catch (Throwable e) {
            return null;
        }
    }

    public static ConnectionDetails connectionDetails(ServerClient.Connection cxn) {
        try {
            return ((RemoteServer) cxn.client()).connectionDetails();
        } catch (Throwable e) {
            return null;
        }
    }
}
