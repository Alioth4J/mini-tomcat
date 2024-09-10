package test;

import server.Request;
import server.Response;
import server.Servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HelloServlet implements Servlet {

    @Override
    public void service(Request request, Response response) throws IOException {
        String doc = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Test Custom Servlet</title>
                    <meta charset="utf-8">
                </head?
                <body>
                    <h1>Hello Dynamic Resource</h1>
                </body>
                </html>
                """;
        OutputStream output = response.getOutput();
        output.write(doc.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

}
