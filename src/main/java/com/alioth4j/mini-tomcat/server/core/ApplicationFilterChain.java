package server.core;

import server.connector.HttpRequestFacade;
import server.connector.HttpResponseFacade;
import server.connector.http.HttpRequestImpl;
import server.connector.http.HttpResponseImpl;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class ApplicationFilterChain implements FilterChain {

    private List<ApplicationFilterConfig> filters = new ArrayList<>();
    private Iterator<ApplicationFilterConfig> iterator = null;

    private Servlet servlet = null;

    public ApplicationFilterChain() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        internalDoFilter(request, response);
    }

    private void internalDoFilter(ServletRequest request, ServletResponse response) {
        if (this.iterator == null) {
            this.iterator = this.filters.iterator();
        }
        if (this.iterator.hasNext()) {
            try {
                ApplicationFilterConfig filterConfig = this.iterator.next();
                Filter filter = filterConfig.getFilter();
                filter.doFilter(request, response, this);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ServletException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            HttpRequestFacade requestFacade = new HttpRequestFacade((HttpRequestImpl) request);
            HttpResponseFacade responseFacade = new HttpResponseFacade((HttpResponseImpl) response);
            servlet.service(requestFacade, responseFacade);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addFilter(ApplicationFilterConfig filterConfig) {
        this.filters.add(filterConfig);
    }

    void release() {
        this.filters.clear();
        this.iterator = null;
        this.servlet = null;
    }

    void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

}
