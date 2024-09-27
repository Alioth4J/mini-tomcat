package server.startup;

import server.Logger;
import server.connector.http.HttpConnector;
import server.core.ContainerListenerDef;
import server.core.FilterDef;
import server.core.FilterMap;
import server.core.StandardContext;
import server.logger.FileLogger;

import java.io.File;

public class Bootstrap {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    private static int debug = 0;

    public static void main(String[] args) {
        if (debug >= 1) {
            log("..... startup .....");
        }

        System.setProperty("mini-tomcat.base", WEB_ROOT);

        HttpConnector connector = new HttpConnector();
        StandardContext container = new StandardContext();
        connector.setContainer(container);
        container.setConnector(connector);

        Logger logger = new FileLogger();
        container.setLogger(logger);

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("TestFilter");
        filterDef.setFilterClass("test.TestFilter");
        container.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("TestFilter");
        filterMap.setURLPattern("/*");
        container.addFilterMap(filterMap);

        ContainerListenerDef listenerDef = new ContainerListenerDef();
        listenerDef.setListenerName("TestListener");
        listenerDef.setListenerClass("test.TestListener");
        container.addListenerDef(listenerDef);
        container.listenerStart();

        container.filterStart();
        connector.start();
    }

    private static void log(String message) {
        System.out.print("Bootstrap: ");
        System.out.println(message);
    }

    private static void log(String message, Throwable exception) {
        log(message);
        exception.printStackTrace(System.out);
    }

}
