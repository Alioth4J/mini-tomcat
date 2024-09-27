package server.startup;

import server.connector.http.HttpConnector;
import server.core.*;

import java.io.File;

public class Bootstrap {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    private static int debug = 0;

    public static void main(String[] args) {
        // 记录日志
        if (debug >= 1) {
            log("..... startup .....");
        }
        // 设置系统属性
        System.setProperty("mini-tomcat.base", WEB_ROOT);
        // 创建 Connector 和 Container
        HttpConnector connector = new HttpConnector();
        StandardHost container = new StandardHost();
        WebappClassLoader loader = new WebappClassLoader();
        container.setLoader(loader);
        // 互相持有
        connector.setContainer(container);
        container.setConnector(connector);
        // 启动
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
