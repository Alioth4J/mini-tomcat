package server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpResponse implements HttpServletResponse {

    HttpRequest request;
    OutputStream output;
    PrintWriter writer;

    String contentType = null;
    long contentLength = -1;
    String charset = null;
    String characterEncoding = null;
    String protocol = "HTTP/1.1";

    Map<String, String> headers = new ConcurrentHashMap<>();
    int status = HttpServletResponse.SC_OK;
    String message = getStatusMessage(HttpServletResponse.SC_OK);

    List<Cookie> cookies = new ArrayList<>();

    public HttpResponse(OutputStream output) {
        this.output = output;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    protected String getStatusMessage(int status) {
        switch (status) {
            case SC_OK:
                return "OK";
            case SC_ACCEPTED:
                return "Accepted";
            case SC_BAD_GATEWAY:
                return ("Bad Gateway");
            case SC_BAD_REQUEST:
                return ("Bad Request");
            case SC_CONTINUE:
                return ("Continue");
            case SC_FORBIDDEN:
                return ("Forbidden");
            case SC_INTERNAL_SERVER_ERROR:
                return ("Internal Server Error");
            case SC_METHOD_NOT_ALLOWED:
                return ("Method Not Allowed");
            case SC_NOT_FOUND:
                return ("Not Found");
            case SC_NOT_IMPLEMENTED:
                return ("Not Implemented");
            case SC_REQUEST_URI_TOO_LONG:
                return ("Request URI Too Long");
            case SC_SERVICE_UNAVAILABLE:
                return ("Service Unavailable");
            case SC_UNAUTHORIZED:
                return ("Unauthorized");
            default:
                return "HTTP Response Status " + status;
        }
    }

    public void sendHeaders() throws IOException {
        PrintWriter outputWriter = getWriter();
        outputWriter.print(getProtocol());
        outputWriter.print(" ");
        outputWriter.print(status);
        if (message != null) {
            outputWriter.print(" ");
            outputWriter.print(message);
        }
        outputWriter.print("\r\n");

        if (getContentType() != null) {
            outputWriter.print("Content-Type: " + getContentType() + "\r\n");
        }
        if (getContentLength() >= 0) {
            outputWriter.print("Content-Length: " + getContentLength() + "\r\n");
        }
        Iterator<String> names = headers.keySet().iterator();
        while (names.hasNext()) {
            String name = names.next();
            String value = headers.get(name);
            outputWriter.print(name);
            outputWriter.print(": ");
            outputWriter.print(value);
            outputWriter.print("\r\n");
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            Cookie cookie = new Cookie(DefaultHeaders.JSESSIONID_NAME, session.getId());
            cookie.setMaxAge(-1);
            addCookie(cookie);
        }
        synchronized (cookies) {
            Iterator<Cookie> items = cookies.iterator();
            while (items.hasNext()) {
                Cookie cookie = items.next();
                outputWriter.print(CookieTools.getCookieHeaderName(cookie));
                outputWriter.print(": ");
                StringBuffer sbValue = new StringBuffer();
                CookieTools.getCookieHeaderValue(cookie, sbValue);
                outputWriter.print(sbValue.toString());
                outputWriter.print("\r\n");
            }
        }

        outputWriter.print("\r\n");
        outputWriter.flush();
    }

    public void finishResponse() {
        this.writer.flush();
    }

    public OutputStream getOutput() {
        return output;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public void addCookie(Cookie cookie) {
        synchronized (cookies) {
            cookies.add(cookie);
        }
    }

    @Override
    public boolean containsHeader(String name) {
        return this.headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String url) {
        return "";
    }

    @Override
    public String encodeUrl(String url) {
        return "";
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return "";
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
        if (DefaultHeaders.CONTENT_LENGTH_NAME.equals(name.toLowerCase())) {
            setContentLength(Integer.parseInt(value));
        }
        if (DefaultHeaders.CONTENT_TYPE_NAME.equals(name.toLowerCase())) {
            setContentType(value);
        }
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
        if (DefaultHeaders.CONTENT_LENGTH_NAME.equals(name.toLowerCase())) {
            setContentLength(Integer.parseInt(value));
        }
        if (DefaultHeaders.CONTENT_TYPE_NAME.equals(name.toLowerCase())) {
            setContentType(value);
        }
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.put(name, String.valueOf(value));
        if (DefaultHeaders.CONTENT_LENGTH_NAME.equals(name.toLowerCase())) {
            setContentLength(value);
        }
        if (DefaultHeaders.CONTENT_TYPE_NAME.equals(name.toLowerCase())) {
            setContentType(String.valueOf(value));
        }
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.put(name, String.valueOf(value));
        if (DefaultHeaders.CONTENT_LENGTH_NAME.equals(name.toLowerCase())) {
            setContentLength(value);
        }
        if (DefaultHeaders.CONTENT_TYPE_NAME.equals(name.toLowerCase())) {
            setContentType(String.valueOf(value));
        }
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
        this.message = getStatusMessage(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {

    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getHeader(String name) {
        return this.headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return this.headers.keySet();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }

    @Override
    public String getCharacterEncoding() {
        return this.characterEncoding;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        this.writer = new PrintWriter(new OutputStreamWriter(output, getCharacterEncoding()), true);
        return this.writer;
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {
        this.contentLength = len;
    }

    @Override
    public void setContentLengthLong(long len) {
        this.contentLength = len;
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

}
