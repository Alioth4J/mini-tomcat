package server.core;

import server.*;

import javax.servlet.ServletException;
import java.io.IOException;

public class StandardPipeline implements Pipeline {

    protected Valve basic = null;
    protected Valve[] valves = new Valve[0];

    protected Container container = null;

    protected int debug = 0;
    protected String info = "com.alioth4j.mini-tomcat.server.core.StandardPipeline/1.0";

    public StandardPipeline() {
        this(null);
    }

    public StandardPipeline(Container container) {
        setContainer(container);
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return this.basic;
    }

    @Override
    public void setBasic(Valve valve) {
        if (valve == null) {
            return;
        }
        Valve oldBasic = this.basic;
        if (oldBasic == valve) {
            return;
        }
        valve.setContainer(container);
        this.basic = valve;
    }

    @Override
    public void addValve(Valve valve) {
        synchronized (valves) {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            valve.setContainer(container);
            results[valves.length] = valve;
            valves = results;
        }
    }

    @Override
    public Valve[] getValves() {
        if (basic == null) {
            return valves;
        }
        synchronized (valves) {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0 ,valves.length);
            results[valves.length] = basic;
            return results;
        }
    }

    @Override
    public void removeValve(Valve valve) {
        synchronized (valves) {
            int j = -1;
            for (int i = 0; i < valves.length; i++) {
                if (valve == valves[i]) {
                    j = i;
                    break;
                }
            }
            if (j < 0) {
                return;
            }
            valve.setContainer(null);
            Valve[] results = new Valve[valves.length - 1];
            int n = 0;
            for (int i = 0; i < valves.length; i++) {
                if (i == j) {
                    continue;
                }
                results[n++] = valves[i];
            }
            valves = results;
        }
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        // 调用 context 中的 invoke，发起责任链调用
        new StandardPipelineValveContext().invokeNext(request, response);
    }

    protected class StandardPipelineValveContext implements ValveContext {

        protected int stage = 0;

        @Override
        public String getInfo() {
            return info;
        }

        /**
         * 由于 Valve 中是倒序调用，所以最先调用 basic
         * @param request
         * @param response
         * @throws IOException
         * @throws ServletException
         */
        @Override
        public void invokeNext(Request request, Response response) throws IOException, ServletException {
            int subscript = stage;
            stage++;
            if (subscript < valves.length) {
                valves[subscript].invoke(request, response, this);
            } else if (subscript == valves.length && basic != null) {
                basic.invoke(request, response, this);
            } else {
                throw new ServletException("StandardPipeline.noValve");
            }
        }
    }

}
