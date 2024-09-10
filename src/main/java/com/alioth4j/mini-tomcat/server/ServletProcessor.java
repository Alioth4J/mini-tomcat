package server;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
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
    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf('/') + 1);
        OutputStream output = response.getOutput();
        URLClassLoader loader = null;
        Servlet servlet = null;
        try {
            URL[] urls = new URL[1];
            File classPath = new File(HttpServer.WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            URLStreamHandler streamHandler = null;
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
            Class<?> servletClass = loader.loadClass(servletName);
            servlet = (Servlet) servletClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            try {
                output.write(servletNotFoundMessage.getBytes(StandardCharsets.UTF_8));
                output.flush();
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            try {
                loader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            // response head
            String responseHead = composeResponseHead();
            output.write(responseHead.getBytes(StandardCharsets.UTF_8));
            // response body
            servlet.service(request, response);
        } catch (IOException e) {
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
