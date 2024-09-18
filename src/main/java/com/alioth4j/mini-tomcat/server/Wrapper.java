package server;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

public interface Wrapper {

    public int getLoadOnStartup();
    public void setLoadOnStartup(int value);
    public String getServletClass();
    public void setServletClass(String servletClass);
    public Servlet allocate() throws ServletException;
    public void addInitParameter(String name, String value);
    public String findInitParameter(String name);
    public String[] findInitParameters();
    public void load();
    public void removeInitParameter(String name);

}
