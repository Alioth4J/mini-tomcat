package server;

import org.apache.commons.lang3.text.StrSubstitutor;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ServletProcessor {

    private final String okMessage = """
            HTTP/1.1 ${statusCode} ${statusName}
            Content-Type: ${contentType}
            Server: mini-tomcat
            Date: ${zonedDateTime}
            
            """;

    private final String servletNotFoundMessage = """
            HTTP/1.1 404 Not Found
            Content-Type: text/html
            Content-Length: 30
            
            <h1>Servlet Not Found</h1>
            """;

    // TODO FIX UNKNOWN BUG(S)
    public void process(HttpRequest request, HttpResponse response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf('/') + 1);
        PrintWriter writer = null;
        try {
            response.setCharacterEncoding("UTF-8");
            writer = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Servlet servlet = null;
        try {
            Class<?> servletClass = HttpConnector.getLoader().loadClass(servletName);
            servlet = (Servlet) servletClass.newInstance();
            HttpRequestFacade requestFacade = new HttpRequestFacade(request);
            HttpResponseFacade responseFacade = new HttpResponseFacade(response);
            servlet.service(requestFacade, responseFacade);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            writer.println(servletNotFoundMessage);
            return;
        } catch (IOException | InstantiationException | IllegalAccessException | ServletException e) {
            e.printStackTrace();
        }
    }

    private String composeResponseHead() {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("statusCode", "200");
        valuesMap.put("statusName", "OK");
        valuesMap.put("contentType", "text/html;charset=utf-8");
        valuesMap.put("zonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor substitutor = new StrSubstitutor();
        String responseHead = substitutor.replace(this.okMessage);
        return responseHead;
    }

}
