package server.core;

import server.Context;
import server.Loader;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

final class ApplicationFilterConfig implements FilterConfig {

    private Context context = null;
    private Filter filter = null;
    private FilterDef filterDef = null;

    public ApplicationFilterConfig(Context context, FilterDef filterDef) throws ServletException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        super();
        this.context = context;
        setFilterDef(filterDef);
    }

    public String getFilterName() {
        return filterDef.getFilterName();
    }

    public String getInitParameter(String name) {
        Map<String, String> map = filterDef.getParameterMap();
        if (map == null) {
            return null;
        } else {
            return map.get(name);
        }
    }

    public Enumeration<String> getInitParameterNames() {
        Map<String, String> map = filterDef.getParameterMap();
        if (map == null) {
            return Collections.enumeration(new ArrayList<String>());
        } else {
            return Collections.enumeration(map.keySet());
        }
    }

    public ServletContext getServletContext() {
        return this.context.getServletContext();
    }

    Filter getFilter() throws InstantiationException, IllegalAccessException, ServletException, ClassNotFoundException, ClassCastException {
        if (this.filter != null) {
            return this.filter;
        }
        Loader classLoader = context.getLoader();
        String filterClass = filterDef.getFilterClass();
        Class<?> clazz = classLoader.getClassLoader().loadClass(filterClass);
        this.filter = (Filter) clazz.newInstance();
        this.filter.init(this);
        return this.filter;
    }

    FilterDef getFilterDef() {
        return this.filterDef;
    }

    void release() {
        if (this.filter != null) {
            filter.destroy();
        }
        this.filter = null;
    }

    void setFilterDef(FilterDef filterDef) throws ServletException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.filterDef = filterDef;
        // 设置 Filter
        if (filterDef == null) {
            if (this.filter != null) {
                this.filter.destroy();
            }
            this.filter = null;
        } else {
            this.filter = getFilter();
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("ApplicationFilterConfig[");
        sb.append("name=");
        sb.append(filterDef.getFilterName());
        sb.append(",filterClass=");
        sb.append(filterDef.getFilterClass());
        sb.append("]");
        return sb.toString();
    }

}
