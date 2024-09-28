package server.core;

import server.*;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

public class StandardWrapper extends ContainerBase implements Wrapper {

    private Servlet instance;
    private String servletClass;
    private Loader loader;
    private String name;
    protected Container parent = null;

    public StandardWrapper(String servletClass, StandardContext parent) {
        super();
        pipeline.setBasic(new StandardWrapperValve());

        this.parent = parent;
        this.servletClass = servletClass;
        try {
            loadServlet();
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public Servlet loadServlet() throws ServletException {
        if (instance != null) {
            return instance;
        }
        Servlet servlet = null;
        String actualClass = servletClass;
        if (actualClass == null) {
            throw new ServletException("Servlet class has not been specified");
        }
        Loader classLoader = getLoader();
        Class classClass = null;
        try {
            if (classLoader != null) {
                classClass = classLoader.getClassLoader().loadClass(actualClass);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            servlet = (Servlet) classClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        servlet.init(null);
        this.instance = servlet;
        return servlet;
    }

    public Servlet getServlet() {
        return this.instance;
    }

    public void invoke(Request request, Response response) throws ServletException, IOException {
        super.invoke(request, response);
    }

    public Servlet getInstance() {
        return instance;
    }

    public void setInstance(Servlet instance) {
        this.instance = instance;
    }

    @Override
    public int getLoadOnStartup() {
        return 0;
    }

    @Override
    public void setLoadOnStartup(int value) {

    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    @Override
    public Servlet allocate() throws ServletException {
        return null;
    }

    @Override
    public void addInitParameter(String name, String value) {

    }

    @Override
    public String findInitParameter(String name) {
        return "";
    }

    @Override
    public String[] findInitParameters() {
        return new String[0];
    }

    @Override
    public void load() {

    }

    @Override
    public void removeInitParameter(String name) {

    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    @Override
    public Loader getLoader() {
        return loader;
    }

    @Override
    public String getInfo() {
        return "MiniTomcat Servlet Wrapper, version 0.1";
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
    }

    @Override
    public void addChild(Container child) {
    }

    @Override
    public Container findChild(String name) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return null;
    }

    @Override
    public void removeChild(Container child) {
    }

}
