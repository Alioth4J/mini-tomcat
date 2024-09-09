package server;

import java.io.OutputStream;

public class Response {

    private OutputStream output;

    private static final int BUFFER_SIZE = 1024;

    public Response(OutputStream output) {
        this.output = output;
    }

    public OutputStream getOutput() {
        return this.output;
    }

}
