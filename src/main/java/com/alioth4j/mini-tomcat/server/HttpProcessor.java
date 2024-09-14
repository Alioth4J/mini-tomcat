package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpProcessor implements Runnable {

    private HttpConnector connector;

    private volatile boolean available = false;
    private Socket socket;

    public HttpProcessor(HttpConnector connector) {
        this.connector = connector;
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            Socket socket = await();
            if (socket == null) {
                continue;
            }
            process(socket);
            connector.recycle(this);
        }
    }

    private synchronized Socket await() {
        while (!available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        available = false;
        notifyAll();
        return this.socket;
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
        // HTTP request
        HttpRequest request = new HttpRequest(input);
        request.parse(socket);
        // handle session
        if (request.getSessionId() == null || request.getSessionId().equals("")) {
            request.getSession(true);
        }
        // HTTP response
        HttpResponse response = new HttpResponse(output);
        response.setRequest(request);
        request.setResponse(response);
        try {
            response.sendHeaders();
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public synchronized void assign(Socket socket) {
        while (available) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.socket = socket;
        available = true;
        notifyAll();
    }

}
