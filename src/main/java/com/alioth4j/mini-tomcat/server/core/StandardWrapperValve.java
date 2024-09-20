package server.core;

import server.Request;
import server.Response;
import server.ValveContext;
import server.connector.HttpRequestFacade;
import server.connector.HttpResponseFacade;
import server.connector.http.HttpRequestImpl;
import server.connector.http.HttpResponseImpl;
import server.valves.ValveBase;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

public class StandardWrapperValve extends ValveBase {

    @Override
    public void invoke(Request request, Response response, ValveContext context) throws IOException, ServletException {
        HttpRequestFacade requestFacade = new HttpRequestFacade((HttpRequestImpl) request);
        HttpResponseFacade responseFacade = new HttpResponseFacade((HttpResponseImpl) response);
        Servlet instance = ((StandardWrapper) getContainer()).getServlet();
        if (instance != null) {
            instance.service(requestFacade, responseFacade);
        }
    }

}
