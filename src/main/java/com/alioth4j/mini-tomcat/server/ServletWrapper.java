package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletWrapper {

    private Servlet instance;
    private String servletClass;
    private ClassLoader loader;
    private String name;
    protected ServletContainer parent = null;

    public ServletWrapper(String servletClass, ServletContainer parent) {
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
        ClassLoader classLoader = getLoader();
        Class classClass = null;
        try {
            if (classLoader != null) {
                classClass = classLoader.loadClass(actualClass);
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

    public void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (instance != null) {
            instance.service(request, response);
        }
    }

    public Servlet getInstance() {
        return instance;
    }

    public void setInstance(Servlet instance) {
        this.instance = instance;
    }

    public String getServletClass() {
        return servletClass;
    }

    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public void setLoader(ClassLoader loader) {
        this.loader = loader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServletContainer getParent() {
        return parent;
    }

    public void setParent(ServletContainer parent) {
        this.parent = parent;
    }

}
