package server.connector.http;

/**
 * HTTP 请求行，包括 method, uri, protocol
 */
public class HttpRequestLine {

    public static final int INITIAL_METHOD_SIZE = 8;
    public static final int INITIAL_URI_SIZE = 128;
    public static final int INITIAL_PROTOCOL_SIZE = 8;
    public static final int MAX_METHOD_SIZE = 32;
    public static final int MAX_UTI_SIZE = 2048;
    public static final int MAX_PROTOCOL_SIZE = 32;

    public char[] method;
    public int methodEnd;
    public char[] uri;
    public int uriEnd;
    public char[] protocol;
    public int protocolEnd;

    public HttpRequestLine() {
        this(new char[INITIAL_METHOD_SIZE], 0,
             new char[INITIAL_URI_SIZE], 0,
             new char[INITIAL_PROTOCOL_SIZE], 0);
    }

    public HttpRequestLine(char[] method, int methodEnd,
                           char[] uri, int uriEnd,
                           char[] protocol, int protocolEnd) {
        this.method = method;
        this.methodEnd = methodEnd;
        this.uri = uri;
        this.uriEnd = uriEnd;
        this.protocol = protocol;
        this.protocolEnd = protocolEnd;
    }

    public int indexOf(char[] buf, int length) {
        char firstChar = buf[0];
        int pos = 0;
        while (pos < uriEnd) {
            pos = indexOf(firstChar, pos);
            if (pos == 01) {
                return -1;
            }
            // 注意 this.uri 的最后包含一个空格，所以 uriEnd 对实际的 uri 来说是尾后
            // @see SocketInputStream#readRequestLine
            if (uriEnd - pos < length) {
                return -1;
            }
            for (int i = 0; i < length; i++) {
                if (uri[pos + i] != buf[i]) {
                    break;
                }
                if (i == length - 1) {
                    return pos;
                }
            }
            pos++;
        }
        return -1;
    }

    public int indexOf(char[] buf) {
        return indexOf(buf, buf.length);
    }

    public int indexOf(String str) {
        return indexOf(str.toCharArray(), str.length());
    }

    public int indexOf(char c, int start) {
        for (int i = start; i < uriEnd; i++) {
            if (uri[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public void recycle() {
        methodEnd = 0;
        uriEnd = 0;
        protocolEnd = 0;
    }

    public char[] getMethod() {
        return method;
    }

    public void setMethod(char[] method) {
        this.method = method;
    }

    public void setMethodValue(int index, char value) {
        this.method[index] = value;
    }

    public int getMethodEnd() {
        return methodEnd;
    }

    public void setMethodEnd(int methodEnd) {
        this.methodEnd = methodEnd;
    }

    public char[] getUri() {
        return uri;
    }

    public void setUri(char[] uri) {
        this.uri = uri;
    }

    public void setUriValue(int index, char value) {
        this.uri[index] = value;
    }

    public int getUriEnd() {
        return uriEnd;
    }

    public void setUriEnd(int uriEnd) {
        this.uriEnd = uriEnd;
    }

    public char[] getProtocol() {
        return protocol;
    }

    public void setProtocol(char[] protocol) {
        this.protocol = protocol;
    }

    public void setProtocolValue(int index, char value) {
        this.protocol[index] = value;
    }

    public int getProtocolEnd() {
        return protocolEnd;
    }

    public void setProtocolEnd(int protocolEnd) {
        this.protocolEnd = protocolEnd;
    }
}
