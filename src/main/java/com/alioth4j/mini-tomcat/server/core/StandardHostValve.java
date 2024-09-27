package server.core;

import server.Request;
import server.Response;
import server.ValveContext;
import server.connector.http.HttpRequestImpl;
import server.valves.ValveBase;

import javax.servlet.ServletException;
import java.io.IOException;

public class StandardHostValve extends ValveBase {

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
        String docbase = ((HttpRequestImpl) request).getDocbase();
        StandardHost host = (StandardHost) getContainer();
        StandardContext servletContext = host.getContext(docbase);
        servletContext.invoke(request, response);
    }

}
