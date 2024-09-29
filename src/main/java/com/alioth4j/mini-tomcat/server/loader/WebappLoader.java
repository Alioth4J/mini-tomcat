package server.loader;

import server.Container;
import server.Loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLStreamHandler;

public class WebappLoader implements Loader {

    ClassLoader classLoader;
    ClassLoader parent;
    String path;
    String docbase;
    Container container;

    public WebappLoader(String docbase) {
        this.docbase = docbase;
    }

    public WebappLoader(String docbase, ClassLoader parent) {
        this.docbase = docbase;
        this.parent = parent;
    }

    @Override
    public synchronized void start() {
        try {
            URL[] urls = new URL[1];
            File classPath = new File(System.getProperty("mini-tomcat.base"));
            String repository = new URL("file", null, classPath.getCanonicalPath() + File.separator).toString();
            if (docbase != null && !docbase.equals("")) {
                repository = repository + docbase + File.separator;
            }
            repository = repository + "WEB-INF" + File.separator + "classes" + File.separator;
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            classLoader = new WebappClassLoader(urls, parent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void addRepository(String repository) {

    }

    @Override
    public String[] findRepositories() {
        return null;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getParent() {
        return parent;
    }

    public void setParent(ClassLoader parent) {
        this.parent = parent;
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
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

}
