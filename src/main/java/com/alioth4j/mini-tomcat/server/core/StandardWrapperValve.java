package server.core;

import server.Request;
import server.Response;
import server.ValveContext;
import server.connector.http.HttpRequestImpl;
import server.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class StandardWrapperValve extends ValveBase {

    private FilterDef filterDef = null;

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
        // 创建出 FilterChain，调用 doFilter 方法，最后 release
        Servlet servlet = ((StandardWrapper) getContainer()).getServlet();
        ApplicationFilterChain filterChain = createFilterChain(request, servlet);
        if (servlet != null && filterChain != null) {
            filterChain.doFilter((ServletRequest) request, (ServletResponse) response);
        }
        filterChain.release();
    }

    private ApplicationFilterChain createFilterChain(Request request, Servlet servlet) {
        if (servlet == null) {
            return null;
        }
        ApplicationFilterChain filterChain = new ApplicationFilterChain();
        filterChain.setServlet(servlet);
        StandardWrapper wrapper = (StandardWrapper) getContainer();
        StandardContext context = (StandardContext) wrapper.getParent();
        FilterMap[] filterMaps = context.findFilterMaps();
        if (filterMaps == null || filterMaps.length == 0) {
            return filterChain;
        }
        String requestPath = null;
        if (request instanceof HttpServletRequest) {
            String contextPath = "";
            String requestURI = ((HttpRequestImpl)request).getUri();
//            if (requestURI.length() >= contextPath.length()) {
//                requestPath = requestURI.substring(contextPath.length());
//            }
            requestPath = requestURI;
        }
        String servletName = wrapper.getName();

        int n = 0;
        // 匹配 URL
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersURL(filterMaps[i], requestPath)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                continue;
            }
            filterChain.addFilter(filterConfig);
            n++;
        }
        // 匹配 Servlet
        for (int i = 0; i < filterMaps.length; i++) {
            if (!matchFiltersServlet(filterMaps[i], servletName)) {
                continue;
            }
            ApplicationFilterConfig filterConfig = (ApplicationFilterConfig) context.findFilterConfig(filterMaps[i].getFilterName());
            if (filterConfig == null) {
                continue;
            }
            filterChain.addFilter(filterConfig);
            n++;
        }
        return filterChain;
    }

    private boolean matchFiltersURL(FilterMap filterMap, String requestPath) {
        if (requestPath == null) {
            return false;
        }
        String testPath = filterMap.getURLPattern();
        if (testPath == null) {
            return false;
        }
        if (testPath.equals(requestPath)) {
            return true;
        }
        if (requestPath.equals("/*")) {
            return true;
        }
        if (testPath.endsWith("/*")) {
            String comparePath = requestPath;
            while (true) {
                if (testPath.equals(comparePath + "/*")) {
                    return true;
                }
                int slash = comparePath.lastIndexOf('/');
                if (slash < 0) {
                    break;
                }
                comparePath = comparePath.substring(0, slash);
            }
            return false;
        }
        if (testPath.startsWith("*.")) {
            int slash = requestPath.lastIndexOf('/');
            int period = requestPath.lastIndexOf('.');
            if (slash >= 0 && period > slash) {
                return testPath.equals("*." + requestPath.substring(period + 1));
            }
        }
        return false;
    }

    private boolean matchFiltersServlet(FilterMap filterMap, String servletName) {
        if (servletName == null) {
            return false;
        } else {
            return servletName.equals(filterMap.getServletName());
        }
    }

}
