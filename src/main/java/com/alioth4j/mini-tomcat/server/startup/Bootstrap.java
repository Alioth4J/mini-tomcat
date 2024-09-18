package server.startup;

import server.connector.http.HttpConnector;
import server.core.StandardContext;

import java.io.File;

public class Bootstrap {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        StandardContext context = new StandardContext();
        connector.setContext(context);
        context.setConnector(connector);
        connector.start();
    }

}
