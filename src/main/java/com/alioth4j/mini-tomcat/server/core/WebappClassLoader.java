package server.core;

import server.Container;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class WebappClassLoader {

    ClassLoader classLoader;
    String path;
    String docbase;
    Container container;

    public WebappClassLoader() {
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDocbase() {
        return docbase;
    }

    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public synchronized void start() {
        System.out.println("Starting WebappClassLoader");
        // 创建 ClassLoader
        try {
            URL[] urls = new URL[1];
            File classPath = new File(System.getProperty("mini-tomcat.base"));
            String repository = new URL("file", null, classPath.getCanonicalPath() + File.separator).toString();
            if (docbase != null && docbase.length() > 0) {
                repository = repository + docbase + File.separator;
            }
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            classLoader = new URLClassLoader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
    }

}
