package server.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import server.*;
import server.connector.http.HttpConnector;
import server.logger.FileLogger;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardContext extends ContainerBase implements Context {

    HttpConnector connector = null;
    Loader loader = null;

    Map<String, String> servletClsMap = new ConcurrentHashMap<>();
    Map<String, StandardWrapper> servletInstanceMap = new ConcurrentHashMap<>();

    private Map<String, ApplicationFilterConfig> filterConfigs = new ConcurrentHashMap<>();
    private Map<String, FilterDef> filterDefs = new ConcurrentHashMap<>();
    private FilterMap[] filterMaps = new FilterMap[0];

    private List<ContainerListenerDef> listenerDefs = new ArrayList<>();
    private List<ContainerListener> listeners = new ArrayList<>();

    public StandardContext() {
        super();
        pipeline.setBasic(new StandardContextValve());
        // ClassLoader 初始化
//        try {
//            URL[] urls = new URL[1];
//            File classPath = new File(Bootstrap.WEB_ROOT);
//            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
//            URLStreamHandler streamHandler = null;
//            urls[0] = new URL(null, repository, streamHandler);
//            loader = new URLClassLoader(urls);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
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

    public void addFilterDef(FilterDef filterDef) {
        filterDefs.put(filterDef.getFilterName(), filterDef);
    }

    public void addFilterMap(FilterMap filterMap) {
        String filterName = filterMap.getFilterName();
        String servletName = filterMap.getServletName();
        String urlPattern = filterMap.getURLPattern();
        if (findFilterDef(filterName) == null) {
            throw new IllegalArgumentException("standardContext.filterMap.name: " + filterName);
        }
        if (servletName == null && urlPattern == null) {
            throw new IllegalArgumentException("standardContext.filterMap.either");
        }
        if (servletName != null && urlPattern != null) {
            throw new IllegalArgumentException("standardContext.filterMap.either");
        }
        if (urlPattern != null && !validateURLPattern(urlPattern)) {
            throw new IllegalArgumentException("standardContext.filterMap.pattern: " + urlPattern);
        }
        synchronized (filterMaps) {
            FilterMap[] results = new FilterMap[filterMaps.length + 1];
            System.arraycopy(filterMaps, 0, results, 0, filterMaps.length);
            results[filterMaps.length] = filterMap;
            filterMaps = results;
        }

    }

    public FilterDef findFilterDef(String filterName) {
        return filterDefs.get(filterName);
    }

    public FilterDef[] findFilterDefs() {
        synchronized (filterDefs) {
            return filterDefs.values().toArray(new FilterDef[filterDefs.size()]);
        }
    }

    public FilterMap[] findFilterMaps() {
        return filterMaps;
    }

    public void removeFilterMap(FilterMap filterMap) {
        synchronized (filterMaps) {
            int n = -1;
            for (int i = 0; i < filterMaps.length; i++) {
                if (filterMaps[i] == filterMap) {
                    n = i;
                    break;
                }
            }
            if (n < 0) {
                return;
            }
            FilterMap[] results = new FilterMap[filterMaps.length - 1];
            System.arraycopy(filterMaps, 0, results, 0, n);
            System.arraycopy(filterMaps, n + 1, results, n, filterMaps.length - n - 1);
            filterMaps = results;
        }
    }

    public boolean filterStart() {
        boolean ok = true;
        synchronized (filterConfigs) {
            filterConfigs.clear();
            Iterator<String> names = filterDefs.keySet().iterator();
            while (names.hasNext()) {
                String name = names.next();
                ApplicationFilterConfig filterConfig = null;
                try {
                    filterConfig = new ApplicationFilterConfig(this, filterDefs.get(name));
                } catch (ServletException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                filterConfigs.put(name, filterConfig);
            }
        }
        return ok;
    }

    public FilterConfig findFilterConfig(String name) {
        return filterConfigs.get(name);
    }

    private boolean validateURLPattern(String urlPattern) {
        if (urlPattern == null) {
            return false;
        }
        if (urlPattern.startsWith("*.")) {
            if (urlPattern.indexOf('/') < 0) {
                return true;
            } else {
                return false;
            }
        }
        if (urlPattern.startsWith("/")) {
            return true;
        } else {
            return false;
        }
    }

    public void addListenerDef(ContainerListenerDef listenerDef) {
        synchronized (listenerDefs) {
            this.listenerDefs.add(listenerDef);
        }
    }

    public void addContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void start() {
        fireContainerEvent("Container started", this);

        Logger logger = new FileLogger();
        setLogger(logger);

        // 解析 web.xml
        String file = System.getProperty("mini-tomcat.base") + File.separator + docbase + File.separator + "WEB-INF" + File.separator + "web.xml";
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element root = document.getRootElement();
            // listeners
            List<Element> listeners = root.elements("listener");
            for (Element listener : listeners) {
                Element listenerClass = listener.element("listener-class");
                String listenerClassName = listenerClass.getText();
                ContainerListenerDef listenerDef = new ContainerListenerDef();
                listenerDef.setListenerName(listenerClassName);
                listenerDef.setListenerClass(listenerClassName);
                addListenerDef(listenerDef);
            }
            // filters
            List<Element> filters = root.elements("filter");
            for (Element filter : filters) {
                Element filterName = filter.element("filter-name");
                String filterNameText = filterName.getText();
                Element filterClass = filter.element("filter-class");
                String filterClassText = filterClass.getText();
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName(filterNameText);
                filterDef.setFilterClass(filterClassText);
                addFilterDef(filterDef);
            }
            // filter 映射
            List<Element> filtermaps = root.elements("filter-mapping");
            for (Element filtermap : filtermaps) {
                Element filetername = filtermap.element("filter-name");
                String fileternamestr = filetername.getText();
                Element urlpattern = filtermap.element("url-pattern");
                String urlpatternstr = urlpattern.getText();
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName(fileternamestr);
                filterMap.setURLPattern(urlpatternstr);
                addFilterMap(filterMap);
            }
            filterStart();
            // servlet
            List<Element> servlets = root.elements("servlet");
            for (Element servlet : servlets) {
                Element servletname = servlet.element("servlet-name");
                String servletnamestr = servletname.getText();
                Element servletclass = servlet.element("servlet-class");
                String servletclassstr = servletclass.getText();
                Element loadonstartup = servlet.element("load-on-startup");
                String loadonstartupstr = null;
                if (loadonstartup != null) {
                    loadonstartupstr = loadonstartup.getText();
                }
                this.servletClsMap.put(servletnamestr, servletclassstr);
                if (loadonstartupstr != null) {
                    getWrapper(servletnamestr);
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void fireContainerEvent(String type, Object data) {
        if (listeners.size() < 1) {
            return;
        }
        ContainerEvent event = new ContainerEvent(this, type, data);
        // 为了并发安全，转到一个数组中，再遍历
        ContainerListener[] list = new ContainerListener[0];
        synchronized (listeners) {
            list = listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++) {
            list[i].containerEvent(event);
        }
    }

    public boolean listenerStart() {
        System.out.println("Listener Start.........");
        boolean ok = true;
        synchronized (listeners) {
            listeners.clear();
            Iterator<ContainerListenerDef> defs = listenerDefs.iterator();
            while (defs.hasNext()) {
                ContainerListenerDef def = defs.next();
                try {
                    ContainerListener listener = (ContainerListener) (this.getLoader().getClassLoader().loadClass(def.getListenerClass()).newInstance());
                    addContainerListener(listener);
                } catch (Throwable t) {
                    t.printStackTrace();
                    ok = false;
                }
            }
        }
        return ok;
    }

    public String getInfo() {
        return "MiniTomcat Servlet Context, version 1.0";
    }

    @Override
    public Loader getLoader() {
        return this.loader;
    }

    public void setLoader(Loader loader) {
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
    public String getDocBase() {
        return docbase;
    }

    @Override
    public void setDocBase(String docBase) {
        this.docbase = docBase;
    }

    @Override
    public String getPath() {
        return "";
    }

    @Override
    public void setPath(String path) {

    }

    @Override
    public ServletContext getServletContext() {
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
