package server;

import java.io.IOException;
import java.io.InputStream;

public class SocketInputStream extends InputStream {

    private static final byte CR = (byte) '\r';
    private static final byte LF = (byte) '\n';
    private static final byte SP = (byte) ' ';
    private static final byte HT = (byte) '\t';
    private static final byte COLON = (byte) ':';
    private static final int LC_OFFSET = 'A' - 'a';

    protected byte[] buf;
    protected int count;
    protected int pos;

    protected InputStream is;

    public SocketInputStream(InputStream is, int bufferSize) {
        this.is = is;
        buf = new byte[bufferSize];
    }

    public void readRequestLine(HttpRequestLine requestLine) throws IOException{
        // 跳过换行
        int chr = 0;
        do {
            chr = read();
        } while (chr == CR || chr == LF);
        pos--;
        // 读取 method，最后会读入一个空格
        int maxRead = requestLine.getMethod().length;
        int readStart = pos;
        int readCount = 0;
        boolean space = false;
        while (!space) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException();
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.setMethodValue(readCount, (char) buf[pos]);
            readCount++;
            pos++;
        }
        requestLine.setMethodEnd(readCount - 1);
        // 读取 uri，最后会读入一个空格
        maxRead = requestLine.getUri().length;
        readStart = pos;
        readCount = 0;
        space = false;
        while (!space) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException();
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == SP) {
                space = true;
            }
            requestLine.setUriValue(readCount, (char) buf[pos]);
            readCount++;
            pos++;
        }
        requestLine.setUriEnd(readCount - 1);
        // 读取 protocol
        maxRead = requestLine.getProtocol().length;
        readStart = pos;
        readCount = 0;
        space = false;
        boolean eof = false;
        while (!eof) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException();
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == CR) {
                // skip
            } else if (buf[pos] == LF) {
                eof = true;
            } else {
                requestLine.setUriValue(readCount, (char) buf[pos]);
                readCount++;
            }
            pos++;
        }
        requestLine.setProtocolEnd(readCount - 1);
    }

    public void readRequestHeader(HttpRequestHeader requestHeader) throws IOException {
        // 到结尾了或者空行
        int chr = read();
        if (chr == CR || chr == LF) {
            if (chr == CR) {
                read();
            }
            requestHeader.setNameEnd(0);
            requestHeader.setValueEnd(0);
            return;
        } else {
            pos--;
        }
        // 读 name
        int maxRead = requestHeader.getName().length;
        int readStart = pos;
        int readCount = 0;
        boolean colon = false;
        while (!colon) {
            if (pos >= count) {
                int val = read();
                if (val == -1) {
                    throw new IOException();
                }
                pos = 0;
                readStart = 0;
            }
            if (buf[pos] == COLON) {
                colon = true;
            }
            char val = (char) buf[pos];
            if (val >= 'A' && val <= 'Z') {
                val = (char)(val - LC_OFFSET);
            }
            requestHeader.setNameValue(readCount, val);
            readCount++;
            pos++;
        }
        requestHeader.setNameEnd(readCount - 1);
        // 读 value
        maxRead = requestHeader.getValue().length;
        readStart = pos;
        readCount = 0;
        int crPos = -2;
        boolean eof = false;
        boolean validLine = true;
        while (validLine) {
            boolean space = true;
            while (space) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException();
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == SP || buf[pos] == HT) {
                    pos++;
                } else {
                    space = false;
                }
            }
            while (!eof) {
                if (pos >= count) {
                    int val = read();
                    if (val == -1) {
                        throw new IOException();
                    }
                    pos = 0;
                    readStart = 0;
                }
                if (buf[pos] == CR) {

                } else if (buf[pos] == LF) {
                    eof = true;
                } else {
                    int ch = buf[pos] & 0xff;
                    requestHeader.setValueValue(readCount, (char) ch);
                    readCount++;
                }
                pos++;
            }
            int nextChr = read();
            if (nextChr != SP && nextChr != HT) {
                pos--;
                validLine = false;
            } else {
                eof = false;
                requestHeader.setValueValue(readCount, ' ');
                readCount++;
            }
        }
        requestHeader.setValueEnd(readCount);
    }

    @Override
    public int read() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count) {
                return -1;
            }
        }
        return buf[pos++] & 0xff;
    }

    protected void fill() throws IOException {
        pos = 0;
        count = 0;
        int nRead = is.read(buf, 0, buf.length);
        if (nRead > 0) {
            count = nRead;
        }
    }

    @Override
    public int available() throws IOException {
        return (count - pos) + is.available();
    }

    @Override
    public void close() throws IOException {
        if (is == null) {
            return;
        }
        is.close();
        is = null;
        buf = null;
    }

}
