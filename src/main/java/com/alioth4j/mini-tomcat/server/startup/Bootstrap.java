package server.startup;

import server.Loader;
import server.connector.http.HttpConnector;
import server.core.*;

import java.io.File;

public class Bootstrap {

    public static final String MINITOMCAT_HOME = System.getProperty("user.dir");
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webapps";
    public static final int PORT = 8080;
    private static int debug = 0;

    public static void main(String[] args) {
        // 记录日志
        if (debug >= 1) {
            log("..... startup .....");
        }
        // 设置系统属性
        System.setProperty("mini-tomcat.home", MINITOMCAT_HOME);
        System.setProperty("mini-tomcat.base", WEB_ROOT);
        // 创建
        HttpConnector connector = new HttpConnector();
        StandardHost container = new StandardHost();
        Loader loader = new CommonLoader();
        container.setLoader(loader);
        connector.setContainer(container); // 互相持有
        container.setConnector(connector); // 互相持有
        // 启动
        loader.start();
        container.start();
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
