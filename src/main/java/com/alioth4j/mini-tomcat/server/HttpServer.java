package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() {
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while (true) {
            Socket socket = null;
            InputStream input = null;
            OutputStream output = null;
            try {
                socket = serverSocket.accept();
                input = socket.getInputStream();
                output = socket.getOutputStream();

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

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
