package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Response {

    private OutputStream output;
    private String uri;

    private static final int BUFFER_SIZE = 1024;

    public Response(OutputStream output, String uri) {
        this.output = output;
        this.uri = uri;
    }

    public void sendStaticResource() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream fis = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, uri);
            if (file.exists()) {
                fis = new FileInputStream(file);
                int ch = fis.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, ch);
                    ch = fis.read(bytes, 0, BUFFER_SIZE);
                }
                output.flush();
            } else {
                String errMsg = """
                        HTTP/1.1 404 File Not Found
                        Content-Type: text/html
                        Content-Length: 23
                        
                        <h1>File Not Found</h1>
                        """;
                output.write(errMsg.getBytes());
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

}
