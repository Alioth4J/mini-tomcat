package server.core;

import server.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ContainerBase implements Container, Pipeline {

    protected Container parent = null;
    protected Map<String, Container> children = new HashMap<>();

    protected Loader loader = null;
    protected String name = null;

    protected Logger logger = null;

    protected Pipeline pipeline = new StandardPipeline(this);

    public void invoke(Request request, Response response) throws IOException, ServletException {
        pipeline.invoke(request, response);
    }

    public abstract String getInfo();

    @Override
    public Loader getLoader() {
        if (loader != null) {
            return loader;
        }
        if (parent != null) {
            return parent.getLoader();
        }
        return null;
    }

    @Override
    public synchronized void setLoader(Loader loader) {
        Loader oldLoader = this.loader;
        if (oldLoader == loader) {
            return;
        }
        this.loader = loader;
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
        Container oldParent = this.parent;
        // ...
        this.parent = parent;
    }

    @Override
    public void addChild(Container child) {
        addChildInternal(child);
    }

    private void addChildInternal(Container child) {
        synchronized (children) {
            if (children.get(child.getName()) != null) {
                throw new IllegalArgumentException("addChild: Child name '" + child.getName() + "' is not unique");
            }
            child.setParent(this);
            children.put(child.getName(), child);
        }
    }

    @Override
    public Container findChild(String name) {
        if (name == null) {
            return null;
        }
        synchronized (children) {
            return children.get(name);
        }
    }

    public Container[] getChildren() {
        synchronized (children) {
            Container[] result = new Container[children.size()];
            return children.values().toArray(new Container[0]);
        }
    }

    @Override
    public void removeChild(Container child) {
        synchronized (children) {
            if (children.get(child.getName()) == null) {
                return;
            }
            children.remove(child.getName());
        }
        child.setParent(null);
    }

    @Override
    public Logger getLogger() {
        if (logger != null) {
            return logger;
        }
        if (parent != null) {
            return parent.getLogger();
        }
        return null;
    }

    @Override
    public synchronized void setLogger(Logger logger) {
        Logger oldLogger = this.logger;
        if (oldLogger == logger) {
            return;
        }
        this.logger = logger;
    }

    protected void log(String message) {
        Logger logger = getLogger();
        if (logger != null) {
            logger.log(logName() + ": " + message);
        } else {
            System.out.println(logName() + ": " + message);
        }
    }

    protected String logName() {
        String className = this.getClass().getName();
        int period = className.lastIndexOf(".");
        if (period >= 0) {
            className = className.substring(period + 1);
        }
        return className + "[" + getName() + "]";
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public synchronized void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public Valve[] getValves() {
        return pipeline.getValves();
    }

    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }
}
