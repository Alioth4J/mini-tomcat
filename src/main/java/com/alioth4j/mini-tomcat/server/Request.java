package server;

import java.io.IOException;
import java.io.InputStream;

public class Request {

    private InputStream input;
    private String uri;

    public Request(InputStream input) {
        this.input = input;
    }

    public void parse() {
        StringBuilder request = new StringBuilder(2048);
        byte[] buffer = new byte[2048];
        int byteRead;
        try {
            byteRead = this.input.read(buffer);
            for (int j = 0; j < byteRead; j++) {
                request.append((char) buffer[j]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.uri = parseUri(request.toString());
    }

    private String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(' ');
        if (index1 != -1) {
            index2 = requestString.indexOf(' ', index1 + 1);
            if (index2 > index1) {
                return requestString.substring(index1 + 1, index2);
            }
        }
        return null;
    }

    public String getUri() {
        return this.uri;
    }

}
