package server;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.EventObject;
import java.util.logging.Filter;

public final class InstanceEvent extends EventObject {

    public static final String BEFORE_INIT_EVENT = "beforeInit";
    public static final String AFTER_INIT_EVENT = "afterInit";
    public static final String BEFORE_SERVICE_EVENT = "beforeService";
    public static final String AFTER_SERVICE_EVENT = "afterService";
    public static final String BEFORE_DESTROY_EVENT = "beforeDestroy";
    public static final String AFTER_DESTROY_EVENT = "afterDestroy";
    public static final String BEFORE_DISPATCH_EVENT = "beforeDispatch";
    public static final String AFTER_DISPATCH_EVENT = "afterDispatch";
    public static final String BEFORE_FILTER_EVENT = "beforeFilter";
    public static final String AFTER_FILTER_EVENT = "afterFilter";

    private Throwable exception = null;
    private Filter filter = null;
    private ServletRequest request = null;
    private ServletResponse response = null;
    private Servlet servlet = null;
    private String type = null;
    private Wrapper wrapper = null;

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type,
                         ServletRequest request, ServletResponse response) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = null;
        this.servlet = servlet;
        this.type = type;
        this.request = request;
        this.response = response;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.servlet = null;
        this.type = type;

    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type,
                         Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.servlet = null;
        this.type = type;
        this.exception = exception;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type,
                         ServletRequest request, ServletResponse response) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.servlet = null;
        this.type = type;
        this.request = request;
        this.response = response;
    }

    public InstanceEvent(Wrapper wrapper, Filter filter, String type,
                         ServletRequest request, ServletResponse response,
                         Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = filter;
        this.servlet = null;
        this.type = type;
        this.request = request;
        this.response = response;
        this.exception = exception;
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = null;
        this.servlet = servlet;
        this.type = type;
    }

    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type,
                         Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = null;
        this.servlet = servlet;
        this.type = type;
        this.exception = exception;
    }


    public InstanceEvent(Wrapper wrapper, Servlet servlet, String type,
                         ServletRequest request, ServletResponse response, Throwable exception) {
        super(wrapper);
        this.wrapper = wrapper;
        this.filter = null;
        this.servlet = servlet;
        this.type = type;
        this.request = request;
        this.response = response;
        this.exception = exception;
    }


    public Throwable getException() {
        return exception;
    }

    public Filter getFilter() {
        return filter;
    }

    public ServletRequest getRequest() {
        return request;
    }

    public ServletResponse getResponse() {
        return response;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public String getType() {
        return type;
    }

    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public String toString() {
        return "InstanceEvent[" +
                "exception=" + exception +
                ", filter=" + filter +
                ", request=" + request +
                ", response=" + response +
                ", servlet=" + servlet +
                ", type='" + type +
                ", wrapper=" + wrapper +
                ']';
    }

}
