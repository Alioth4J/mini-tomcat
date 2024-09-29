package server.startup;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import server.Loader;
import server.connector.http.HttpConnector;
import server.core.*;
import server.loader.CommonLoader;

import java.io.File;

public class Bootstrap {

    public static String MINITOMCAT_HOME = System.getProperty("user.dir");
    public static String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webapps";
    public static int PORT = 8080;
    private static int debug = 0;

    public static void main(String[] args) {
        // 记录日志
        if (debug >= 1) {
            log("..... startup .....");
        }
        // 读取 server.xml
        String file = MINITOMCAT_HOME + File.separator + "conf" + File.separator + "server.xml";
        SAXReader reader = new SAXReader();
        Document document;
        try {
            document = reader.read(file);
            Element root = document.getRootElement();
            Element connectorElement = root.element("Connector");
            Attribute portAttribute = connectorElement.attribute("port");
            PORT = Integer.parseInt(portAttribute.getText());
            Element hostElement = root.element("Host");
            Attribute appbaseAttribute = hostElement.attribute("appBase");
            WEB_ROOT = WEB_ROOT + File.separator + appbaseAttribute.getText();
        } catch (DocumentException e) {
            e.printStackTrace();
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
