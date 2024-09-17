package server;

import java.util.HashMap;
import java.util.Map;

public abstract class ContainerBase implements Container {

    protected Container parent = null;
    protected Map<String, Container> children = new HashMap<>();

    protected ClassLoader loader = null;
    protected String name = null;

    public abstract String getInfo();

    public ClassLoader getClassLoader() {
        if (loader != null) {
            return loader;
        }
        if (parent != null) {
            return parent.getLoader();
        }
        return null;
    }

    public synchronized void setLoader(ClassLoader loader) {
        ClassLoader oldLoader = this.loader;
        if (oldLoader == loader) {
            return;
        }
        this.loader = loader;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Container getParent() {
        return parent;
    }

    public void setParent(Container container) {
        Container oldParent = this.parent;
        // ...
        this.parent = parent;
    }

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

    public void removeChild(Container child) {
        synchronized (children) {
            if (children.get(child.getName()) == null) {
                return;
            }
            children.remove(child.getName());
        }
        child.setParent(null);
    }

}
