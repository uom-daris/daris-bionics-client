package daris.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import arc.mf.client.AuthenticationDetails;
import arc.mf.client.RemoteServer;
import arc.mf.client.ServerClient;
import arc.mf.client.ServerClient.Connection;

public class ConnectionBuilder {

    public static final String PROPERTY_MF_APP = "mf.app";
    public static final String PROPERTY_MF_HOST = "mf.host";
    public static final String PROPERTY_MF_PORT = "mf.port";
    public static final String PROPERTY_MF_TRANSPORT = "mf.transport";
    public static final String PROPERTY_MF_AUTH = "mf.auth";
    public static final String PROPERTY_MF_TOKEN = "mf.token";
    public static final String PROPERTY_MF_SID = "mf.sid";

    public static final String ENV_MF_APP = "MF_APP";
    public static final String ENV_MF_HOST = "MF_HOST";
    public static final String ENV_MF_PORT = "MF_PORT";
    public static final String ENV_MF_TRANSPORT = "MF_TRANSPORT";
    public static final String ENV_MF_AUTH = "MF_AUTH";
    public static final String ENV_MF_TOKEN = "MF_TOKEN";
    public static final String ENV_MF_SID = "MF_SID";

    public static final String ARG_MF_APP = "--mf.app";
    public static final String ARG_MF_HOST = "--mf.host";
    public static final String ARG_MF_PORT = "--mf.port";
    public static final String ARG_MF_TRANSPORT = "--mf.transport";
    public static final String ARG_MF_AUTH = "--mf.auth";
    public static final String ARG_MF_TOKEN = "--mf.token";
    public static final String ARG_MF_SID = "--mf.sid";

    public ConnectionBuilder() {

    }

    public ConnectionBuilder(String propertiesFilePath) throws Throwable {

        loadFromSystemEnvironmentVariables();

        if (propertiesFilePath != null) {
            loadFromPropertiesFile(propertiesFilePath);
        }

        loadFromSystemProperties();

    }

    private String _host;
    private Integer _port;
    private String _transport;

    private String _app;
    private String _domain;
    private String _user;
    private String _password;
    private String _token;
    private String _sid;

    public ConnectionBuilder setServerHost(String serverHost) {
        _host = serverHost;
        return this;
    }

    public ConnectionBuilder setServerPort(int serverPort) throws IllegalArgumentException {
        if (serverPort <= 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Invalid server port: " + serverPort);
        }
        _port = serverPort;
        return this;
    }

    public ConnectionBuilder setServerTransport(String transport) throws IllegalArgumentException {
        if ("https".equalsIgnoreCase(transport)) {
            _transport = "https";
        } else if ("http".equalsIgnoreCase(transport)) {
            _transport = "http";
        } else if (transport != null && transport.toLowerCase().startsWith("tcp")) {
            _transport = "tcp/ip";
        } else {
            throw new IllegalArgumentException(
                    "Invalid server transport: " + transport + ". Expects http, https or tcp/ip");
        }
        return this;
    }

    public ConnectionBuilder setApplication(String app) {
        _app = app;
        return this;
    }

    public ConnectionBuilder setDomain(String domain) {
        _domain = domain;
        return this;
    }

    public ConnectionBuilder setUser(String user) {
        _user = user;
        return this;
    }

    public ConnectionBuilder setPassword(String password) {
        _password = password;
        return this;
    }

    public ConnectionBuilder setToken(String secureIdentityToken) {
        _token = secureIdentityToken;
        return this;
    }

    public ConnectionBuilder setSession(String sid) {
        _sid = sid;
        return this;
    }

    public ConnectionBuilder setAuthenticationDetails(String application, String domain, String user, String password) {
        _app = application;
        _domain = domain;
        _user = user;
        _password = password;
        return this;
    }

    public ConnectionBuilder setAuthenticationDetails(String application, String token) {
        _app = application;
        _token = token;
        return this;
    }

    public ConnectionBuilder setAuthenticationDetails(AuthenticationDetails details) {
        _app = details.application();
        _domain = details.domain();
        _user = details.userName();
        _password = details.userPassword();
        return this;
    }

    public ConnectionBuilder loadFromPropertiesFile(String propertiesFilePath) throws Throwable {
        File propertiesFile = new File(propertiesFilePath);
        if (propertiesFile.exists()) {
            Properties properties = new Properties();
            InputStream in = null;
            try {
                in = new BufferedInputStream(new FileInputStream(propertiesFilePath));
                properties.load(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            if (properties != null) {
                String app = properties.getProperty(PROPERTY_MF_APP);
                if (app != null && !app.trim().isEmpty()) {
                    setApplication(app.trim());
                }
                String host = properties.getProperty(PROPERTY_MF_HOST);
                if (host != null && !host.trim().isEmpty()) {
                    setServerHost(host.trim());
                }
                String port = properties.getProperty(PROPERTY_MF_PORT);
                if (port != null && !port.trim().isEmpty()) {
                    setServerPort(Integer.parseInt(port.trim()));
                }
                String transport = properties.getProperty(PROPERTY_MF_TRANSPORT);
                if (transport != null && !transport.trim().isEmpty()) {
                    setServerTransport(transport.trim());
                }
                String auth = properties.getProperty(PROPERTY_MF_AUTH);
                if (auth != null && !auth.trim().isEmpty()) {
                    setAuth(auth.trim());
                }
                String token = properties.getProperty(PROPERTY_MF_TOKEN);
                if (token != null && !token.trim().isEmpty()) {
                    setToken(token.trim());
                }
                String sid = properties.getProperty(PROPERTY_MF_SID);
                if (sid != null && !sid.trim().isEmpty()) {
                    setSession(sid);
                }
            }
        }
        return this;
    }

    protected ConnectionBuilder setAuth(String auth) throws Throwable {
        if (auth != null) {
            String[] parts = auth.trim().split("\\ *,\\ *");
            if (parts == null || parts.length != 3) {
                throw new Exception("Failed to parse mf.auth: " + auth);
            }
            setDomain(parts[0]);
            setUser(parts[1]);
            setPassword(parts[2]);
        }
        return this;
    }

    public ConnectionBuilder loadFromSystemProperties() throws Throwable {
        String app = System.getProperty(PROPERTY_MF_APP);
        if (app != null && !app.trim().isEmpty()) {
            setApplication(app.trim());
        }
        String host = System.getProperty(PROPERTY_MF_HOST);
        if (host != null && !host.trim().isEmpty()) {
            setServerHost(host.trim());
        }
        String port = System.getProperty(PROPERTY_MF_PORT);
        if (port != null && !port.trim().isEmpty()) {
            setServerPort(Integer.parseInt(port.trim()));
        }
        String transport = System.getProperty(PROPERTY_MF_TRANSPORT);
        if (transport != null && !transport.trim().isEmpty()) {
            setServerTransport(transport.trim());
        }
        String auth = System.getProperty(PROPERTY_MF_AUTH);
        if (auth != null && !auth.trim().isEmpty()) {
            setAuth(auth.trim());
        }
        String token = System.getProperty(PROPERTY_MF_TOKEN);
        if (token != null && !token.trim().isEmpty()) {
            setToken(token.trim());
        }
        String sid = System.getProperty(PROPERTY_MF_SID);
        if (sid != null && !sid.trim().isEmpty()) {
            setSession(sid.trim());
        }
        return this;
    }

    public ConnectionBuilder loadFromSystemEnvironmentVariables() throws Throwable {
        String app = System.getenv(ENV_MF_APP);
        if (app != null && !app.trim().isEmpty()) {
            setApplication(app.trim());
        }
        String host = System.getenv(ENV_MF_HOST);
        if (host != null && !host.trim().isEmpty()) {
            setServerHost(host.trim());
        }
        String port = System.getenv(ENV_MF_PORT);
        if (port != null && !port.trim().isEmpty()) {
            setServerPort(Integer.parseInt(port.trim()));
        }
        String transport = System.getenv(ENV_MF_TRANSPORT);
        if (transport != null && !transport.trim().isEmpty()) {
            setServerTransport(transport.trim());
        }
        String auth = System.getenv(ENV_MF_AUTH);
        if (auth != null && !auth.trim().isEmpty()) {
            setAuth(auth.trim());
        }
        String token = System.getenv(ENV_MF_TOKEN);
        if (token != null && !token.trim().isEmpty()) {
            setToken(token.trim());
        }
        String sid = System.getenv(ENV_MF_SID);
        if (sid != null && !sid.trim().isEmpty()) {
            setSession(sid.trim());
        }
        return this;
    }

    public String[] parseCommandArguments(String[] args) throws Throwable {

        if (args == null || args.length == 0) {
            return args;
        }
        List<String> remaining = new ArrayList<String>();
        for (int i = 0; i < args.length;) {
            if (ARG_MF_APP.equalsIgnoreCase(args[i])) {
                setApplication(args[i + 1]);
                i += 2;
            } else if (ARG_MF_HOST.equalsIgnoreCase(args[i])) {
                setServerHost(args[i + 1]);
                i += 2;
            } else if (ARG_MF_PORT.equalsIgnoreCase(args[i])) {
                setServerPort(Integer.parseInt(args[i + 1]));
                i += 2;
            } else if (ARG_MF_TRANSPORT.equalsIgnoreCase(args[i])) {
                setServerTransport(args[i + 1]);
                i += 2;
            } else if (ARG_MF_AUTH.equalsIgnoreCase(args[i])) {
                setAuth(args[i + 1]);
                i += 2;
            } else if (ARG_MF_TOKEN.equalsIgnoreCase(args[i])) {
                setToken(args[i + 1]);
                i += 2;
            } else if (ARG_MF_SID.equalsIgnoreCase(args[i])) {
                setSession(args[i + 1]);
                i += 2;
            } else {
                remaining.add(args[i]);
                i++;
            }
        }
        return remaining.toArray(new String[remaining.size()]);
    }

    public static void describeSystemEnvironmentVariables(PrintStream out) {
        out.println();
        out.println("System environment variables:");
        out.println(String.format("    %16s    - Mediaflux server host name.", ENV_MF_HOST));
        out.println(String.format("    %16s    - Mediaflux server port number.", ENV_MF_PORT));
        out.println(String.format("    %16s    - Mediaflux server transport protocol, http, https or tcp/ip.",
                ENV_MF_TRANSPORT));
        out.println(String.format("    %16s    - Mediaflux application name.", ENV_MF_APP));
        out.println(
                String.format("    %16s    - Mediaflux user credentials, separated with comma: <domain,user,password>.",
                        ENV_MF_AUTH));
        out.println(String.format("    %16s    - Mediaflux secure identity token.", ENV_MF_TOKEN));
        out.println(String.format("    %16s    - Mediaflux session key.", ENV_MF_SID));
    }

    public static void describeSystemProperties(PrintStream out) {
        out.println();
        out.println("System properties:");
        out.println(String.format("    %16s    - Mediaflux server host name.", PROPERTY_MF_HOST));
        out.println(String.format("    %16s    - Mediaflux server port number.", PROPERTY_MF_PORT));
        out.println(String.format("    %16s    - Mediaflux server transport protocol, http, https or tcp/ip.",
                PROPERTY_MF_TRANSPORT));
        out.println(String.format("    %16s    - Mediaflux application name.", PROPERTY_MF_APP));
        out.println(
                String.format("    %16s    - Mediaflux user credentials, separated with comma: <domain,user,password>.",
                        PROPERTY_MF_AUTH));
        out.println(String.format("    %16s    - Mediaflux secure identity token.", PROPERTY_MF_TOKEN));
        out.println(String.format("    %16s    - Mediaflux session key.", PROPERTY_MF_SID));
    }

    public static void desribePropertiesFile(PrintStream out) {
        out.println();
        out.println("Properties in .properties file:");
        out.println(String.format("    %16s    - Mediaflux server host name.", PROPERTY_MF_HOST));
        out.println(String.format("    %16s    - Mediaflux server port number.", PROPERTY_MF_PORT));
        out.println(String.format("    %16s    - Mediaflux server transport protocol, http, https or tcp/ip.",
                PROPERTY_MF_TRANSPORT));
        out.println(String.format("    %16s    - Mediaflux application name.", PROPERTY_MF_APP));
        out.println(
                String.format("    %16s    - Mediaflux user credentials, separated with comma: <domain,user,password>.",
                        PROPERTY_MF_AUTH));
        out.println(String.format("    %16s    - Mediaflux secure identity token.", PROPERTY_MF_TOKEN));
        out.println(String.format("    %16s    - Mediaflux session key.", PROPERTY_MF_SID));
    }

    public static void desribeCommandArgs(PrintStream out) {
        out.println();
        out.println("    Mediaflux Arguments:");
        out.println(String.format("    %-36s  - Mediaflux server host name.", ARG_MF_HOST + " <host>"));
        out.println(String.format("    %-36s  - Mediaflux server port number.", ARG_MF_PORT + " <port>"));
        out.println(String.format("    %-36s  - Mediaflux server transport protocol, http, https or tcp/ip.",
                ARG_MF_TRANSPORT + " <http|https|tcp/ip>"));

        out.println(String.format("    %-36s  - Mediaflux user credentials, separated with comma.",
                ARG_MF_AUTH + " <domain,user,password>"));
        out.println(String.format("    %-36s  - Mediaflux secure identity token.", ARG_MF_TOKEN + " <token>"));
        out.println(String.format("    %-36s  - Mediaflux session key.", ARG_MF_SID + " <session>"));
    }

    protected void validate() throws Throwable {
        if (_host == null) {
            throw new IllegalArgumentException("Missing Mediaflux server host name.");
        }
        if (_port == null) {
            throw new IllegalArgumentException("Missing Mediaflux server port number.");
        }
        if (_transport == null) {
            throw new IllegalArgumentException("Missing Mediaflux server transport protocol.");
        }
        if ((_domain == null || _user == null || _password == null) && _token == null && _sid == null) {
            throw new IllegalArgumentException("Missing Mediaflux user credentials. Expects " + ARG_MF_AUTH + ", or "
                    + ARG_MF_TOKEN + ", or " + ARG_MF_SID);
        }
    }

    public Connection build() throws Throwable {

        validate();

        RemoteServer server = new RemoteServer(_host, _port, _transport.startsWith("http"), _transport.equals("https"));
        ServerClient.Connection cxn = server.open();
        if (_sid == null) {
            AuthenticationDetails auth = _token != null ? new AuthenticationDetails(_app, _token)
                    : new AuthenticationDetails(_app, _domain, _user, _password);
            cxn.connect(auth);
        } else {
            cxn.reconnect(_sid);
        }

        return cxn;
    }

}
