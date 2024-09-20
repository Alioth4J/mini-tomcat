package server.core;

import server.*;
import server.connector.HttpRequestFacade;
import server.connector.HttpResponseFacade;
import server.connector.http.HttpConnector;
import server.connector.http.HttpRequestImpl;
import server.startup.Bootstrap;
import server.valves.AccessLogValve;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardContext extends ContainerBase implements Context {

    HttpConnector connector = null;
    ClassLoader loader = null;

    Map<String, String> servletClsMap = new ConcurrentHashMap<>();
    Map<String, StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
        // ClassLoader 初始化
        try {
            URL[] urls = new URL[1];
            File classPath = new File(Bootstrap.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log("Container created.");
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        super.invoke(request, response);
    }

    public Wrapper getWrapper(String name) {
        StandardWrapper servletWrapper = servletInstanceMap.get(name);
        if (servletWrapper == null) {
            String servletClassName = name;
            servletWrapper = new StandardWrapper(servletClassName, this);
            this.servletClsMap.put(name, servletClassName);
            this.servletInstanceMap.put(name, servletWrapper);
        }
        return servletWrapper;
    }

    public String getInfo() {
        return "MiniTomcat Servlet Context, version 1.0";
    }

    @Override
    public ClassLoader getLoader() {
        return this.loader;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    public String getName() {
        return null;
    }

    public void setName(String name) {
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public void setDisplayName(String displayName) {

    }

    @Override
    public String etDocBase() {
        return "";
    }

    @Override
    public void setDocBase(String docBase) {

    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public StandardContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public void setSessionTimeout(int timeout) {

    }

    @Override
    public String getWrapperClass() {
        return "";
    }

    @Override
    public void setWrapperClass(String wrapperClass) {

    }

    @Override
    public Wrapper createWrapper() {
        return null;
    }

    @Override
    public String findServletMapping(String pattern) {
        return "";
    }

    @Override
    public String[] findServletMapping() {
        return new String[0];
    }

    @Override
    public void reload() {

    }

}
