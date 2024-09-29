package server.loader;

import server.Container;
import server.Loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

public class CommonLoader implements Loader {

    ClassLoader classLoader;
    ClassLoader parent;
    String path;
    String docbase;
    Container container;

    public CommonLoader() {
    }

    public CommonLoader(ClassLoader parent) {
        this.parent = parent;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getDocbase() {
        return docbase;
    }

    @Override
    public void setDocbase(String docbase) {
        this.docbase = docbase;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String getInfo() {
        return "CommonLoader";
    }

    @Override
    public void addRepository(String repository) {
    }

    @Override
    public String[] findRepositories() {
        return null;
    }

    @Override
    public synchronized void start() {
        try {
            URL[] urls = new URL[1];
            File classPath = new File(System.getProperty("mini-tomcat.home"));
            String repository = new URL("file", null, classPath.getCanonicalPath() + File.separator).toString();
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            classLoader = new CommonClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
    }

}
