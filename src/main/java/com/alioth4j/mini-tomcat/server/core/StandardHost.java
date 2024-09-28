package server.core;

import server.*;
import server.connector.http.HttpConnector;
import server.logger.FileLogger;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardHost extends ContainerBase {

    HttpConnector connector = null;

    Map<String, StandardContext> contextMap = new ConcurrentHashMap<>();

    private List<ContainerListenerDef> listenerDefs = new ArrayList<>();
    private List<ContainerListener> listeners = new ArrayList<>();

    public StandardHost() {
        super();
        pipeline.setBasic(new StandardHostValve());
        log("Host created.");
    }

    public void start() {
        fireContainerEvent("Host Started", this);
        FileLogger logger = new FileLogger();
        setLogger(logger);
        ContainerListenerDef listenerDef = new ContainerListenerDef();
        listenerDef.setListenerName("TestListener");
        listenerDef.setListenerClass("test.TestListener");
        addListenerDef(listenerDef);
        listenerStart();
    }

    /**
     * 通过 docbase 获得 StandardContext
     * @param name
     * @return
     */
    public StandardContext getContext(String name) {
        StandardContext context = contextMap.get(name);
        if (context == null) {
            context = new StandardContext();
            context.setDocBase(name);
            context.setConnector(connector);
            Loader loader = new WebappLoader(name, this.loader.getClassLoader());
            context.setLoader(loader);
            loader.start();
            contextMap.put(name, context);
        }
        return context;
    }

    public String getInfo() {
        return "Mini-tomcat host, version 1.0";
    }

    public HttpConnector getConnector() {
        return connector;
    }

    public void setConnector(HttpConnector connector) {
        this.connector = connector;
    }

    public void invoke(Request request, Response response) throws ServletException, IOException {
        System.out.println("StandardHost invoke()");
        super.invoke(request, response);
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

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }


}
