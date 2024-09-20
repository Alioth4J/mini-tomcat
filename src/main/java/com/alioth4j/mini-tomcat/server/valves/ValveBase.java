package server.valves;

import server.*;

public abstract class ValveBase implements Valve {

    protected Container container = null;
    protected int debug = 0;
    protected static String info = "com.alioth4j.mini-tomcat.server.valves.ValveBase/1.0";

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    public int getDebug() {
        return debug;
    }

}
