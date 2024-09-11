package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor {

    public HttpProcessor() {
    }

    public void process(Socket socket) {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Request request = new Request(input);
        request.parse();
        Response response = new Response(output);
        if (request.getUri().startsWith("/servlet/")) {
            // 动态资源
            ServletProcessor servletProcessor = new ServletProcessor();
            servletProcessor.process(request, response);
        } else {
            // 静态资源
            StaticResourceProcessor processor = new StaticResourceProcessor();
            processor.process(request, response);
        }
    }

}
