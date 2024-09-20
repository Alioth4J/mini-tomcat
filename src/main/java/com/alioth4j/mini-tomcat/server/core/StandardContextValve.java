package server.core;

import server.Request;
import server.Response;
import server.ValveContext;
import server.connector.http.HttpRequestImpl;
import server.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class StandardContextValve extends ValveBase {

    private static final String info = "com.alioth4j.mini-tomcat.server.core.StandardContextValve";

    public String getInfo() {
        return info;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
        StandardWrapper servletWrapper = null;
        String uri = ((HttpRequestImpl) request).getUri();
        String servletName = uri.substring(uri.lastIndexOf("/" + 1));
        String servletClassName = servletName;
        StandardContext context = (StandardContext) getContainer();
        servletWrapper = (StandardWrapper) context.getWrapper(servletName);
        servletWrapper.invoke(request, response);
    }

}
