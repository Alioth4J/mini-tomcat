package server;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class StaticResourceProcessor {

    private static final int BUFFER_SIZE = 1024;

    private final String okMessage = """
            HTTP/1.1 ${statusCode} ${statusName}
            Content-Type: ${contentType}
            Content-Length: ${contentLength}
            Server: mini-tomcat
            Date: ${zonedDateTime}
            
            """;

    private final String fileNotFoundMessage = """
            HTTP/1.1 404 Not Found
            Content-Type: text/html
            Content-Length: 23
            
            <h1>File Not Found</h1>
            """;

    public void process(Request request, Response response) throws IOException {
        OutputStream output = response.getOutput();
        File file = new File(HttpServer.WEB_ROOT, request.getUri());
        if (file.exists()) {
            // response head
            String head = composeResponseHead(file);
            output.write(head.getBytes(StandardCharsets.UTF_8));
            // response body
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = fis.read(buffer, 0, BUFFER_SIZE);
                while (bytesRead != -1) {
                    output.write(buffer, 0, bytesRead);
                    bytesRead = fis.read(buffer, 0, BUFFER_SIZE);
                }
                output.flush();
            }
        } else {
            output.write(fileNotFoundMessage.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }

    private String composeResponseHead(File file) {
        Map<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("statusCode", "200");
        valuesMap.put("statusName", "OK");
        valuesMap.put("contentType", "text/html;charset=utf-8");
        valuesMap.put("contentLength", file.length());
        valuesMap.put("zonedDateTime", DateTimeFormatter.ISO_ZONED_DATE_TIME.format(ZonedDateTime.now()));
        StrSubstitutor substitutor = new StrSubstitutor(valuesMap);
        String responseHead = substitutor.replace(this.okMessage);
        return responseHead;
    }

}
