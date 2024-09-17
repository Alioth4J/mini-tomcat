package server;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class HttpConnector implements Runnable {

    private int minProcessors = 3;
    private int maxProcessors = 10;
    private int curProcessors = 0;

    private Deque<HttpProcessor> processors = new ArrayDeque<>();

    public static Map<String, HttpSession> sessions = new HashMap<>();

    ServletContext Context = null;

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

    public static Session createSession() {
        Session session = new Session();
        session.setValid(true);
        session.setCreationTime(System.currentTimeMillis());
        String sessionId = generateSessionId();
        session.setId(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    protected static synchronized String generateSessionId() {
        Random random = new Random();
        long seed = System.currentTimeMillis();
        random.setSeed(seed);
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            byte b1 = (byte) ((bytes[i] & 0xf0) >> 4);
            byte b2 = (byte) (bytes[i] & 0x0f);
            if (b1 < 10) {
                result.append((char) ('0' + b1));
            } else {
                result.append((char) ('A' + (b1 - 10)));
            }
            if (b2 < 10) {
                result.append((char) ('0' + b2));
            } else {
                result.append((char) ('A' + b2 - 10));
            }
        }
        return result.toString();
    }

    public static Map<String, HttpSession> getSessions() {
        return sessions;
    }

    public ServletContext getContext() {
        return Context;
    }

    public void setContext(ServletContext Context) {
        this.Context = Context;
    }

}
