package daris.client;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;

import arc.mf.client.ServerClient;

public abstract class ClientApplication implements Closeable {

    private String _propertiesFilePath = null;
    private String _appName = null;

    private ServerClient.Connection _cxn;
    private String[] _args;

    protected ClientApplication(String propertiesFilePath, String appName) {
        _propertiesFilePath = propertiesFilePath;
        _appName = appName;
    }

    public String name() {
        return _appName;
    }

    public void open(String[] args) throws Throwable {
        ConnectionBuilder cb = new ConnectionBuilder(_propertiesFilePath).setApplication(name());
        _args = cb.parseCommandArguments(args);
        _cxn = cb.build();
    }

    public void execute() throws Throwable {
        execute(_cxn, _args);
    }

    protected abstract void execute(ServerClient.Connection cxn, String[] args) throws Throwable;

    @Override
    public void close() throws IOException {
        if (_cxn != null) {
            try {
                _cxn.close();
            } catch (Throwable e) {
                if (e instanceof IOException) {
                    throw (IOException) e;
                } else {
                    throw new IOException(e);
                }
            } finally {
                _cxn.client().discard();
            }
        }
    }

    protected void printHelp(PrintStream ps) {

    }

    public static <T extends ClientApplication> void run(T app, String[] args) {
        try {
            app.open(args);
            app.execute();
        } catch (Throwable e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            if (app != null) {
                app.printHelp(System.out);
            }
        } finally {
            try {
                app.close();
            } catch (Throwable ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

}
