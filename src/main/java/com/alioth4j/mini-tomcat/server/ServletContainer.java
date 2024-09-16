package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServletContainer {

    HttpConnector connector = null;
    ClassLoader loader = null;

    Map<String, String> servletClsMap = new ConcurrentHashMap<>();
    Map<String, Servlet> servletInstanceMap = new ConcurrentHashMap<>();

    public ServletContainer() {
        // ClassLoader 初始化
        try {
            URL[] urls = new URL[1];
            File classPath = new File(HttpServer.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getInfo() {
        return null;
    }

    public ClassLoader getClassLoader() {
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

    public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        Servlet servlet = null;
        ClassLoader loader = getClassLoader();
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        String servletClassName = servletName;
        servlet = servletInstanceMap.get(servletClassName);
        if (servlet == null) {
            try {
                Class<?> servletClass = loader.loadClass(servletClassName);
                servlet = (Servlet) servletClass.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            servletClsMap.put(servletName, servletClassName);
            servletInstanceMap.put(servletName, servlet);

            servlet.init(null);
        }
        HttpRequestFacade requestFacade = new HttpRequestFacade(request);
        HttpResponseFacade responseFacade = new HttpResponseFacade(response);
        servlet.service(requestFacade, responseFacade);
    }

}
