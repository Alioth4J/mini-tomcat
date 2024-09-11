package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;

public class HttpConnector implements Runnable {

    private int minProcessors = 3;
    private int maxProcessors = 10;
    private int curProcessors = 0;

    private Deque<HttpProcessor> processors = new ArrayDeque<>();

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        // 创建 ServerSocket
        ServerSocket serverSocket = null;
        int port = 8080;
        try {
            serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // 初始化 Processors 池
        for (int i = 0; i < minProcessors; i++) {
            HttpProcessor processor = new HttpProcessor(this);
            processor.start();
            processors.push(processor);
            curProcessors++;
        }
        // 开始监听请求
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                HttpProcessor processor = getProcessor();
                if (processor == null) {
                    socket.close();
                    continue;
                }
                processor.assign(socket);
//                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private HttpProcessor getProcessor() {
        synchronized (processors) {
            if (processors.size() > 0) {
                return processors.pop();
            } else if (curProcessors < maxProcessors) {
                return newProcessor();
            } else {
                return null;
            }
        }
    }

    private HttpProcessor newProcessor() {
        HttpProcessor processor = new HttpProcessor(this);
        processor.start();
        // 可重入锁
        synchronized (processors) {
            processors.push(processor);
            curProcessors++;
            return processors.pop();
        }
    }

    public void recycle(HttpProcessor processor) {
        processors.push(processor);
    }

}
