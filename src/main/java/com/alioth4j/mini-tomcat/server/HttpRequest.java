package server;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HttpRequest implements HttpServletRequest {

    private InputStream input;
    private SocketInputStream sis;
    private String uri;
    private InetAddress address;
    private int port;

    protected HttpRequestLine requestLine = new HttpRequestLine();
    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, String> parameters = new ConcurrentHashMap<>();

    public HttpRequest(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void parse(Socket socket) {
        try {
            parseConnection(socket);
            this.sis.readRequestLine(this.requestLine);
            parseHeaders();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.uri = new String(this.requestLine.getUri(), 0, this.requestLine.getUriEnd());
    }

    private void parseHeaders() throws IOException {
        while (true) {
            HttpRequestHeader header = new HttpRequestHeader();
            sis.readRequestHeader(header);
            if (header.getNameEnd() == 0 && header.getValueEnd() == 0) {
                return;
            }
            String name = new String(header.getName(), 0 , header.getNameEnd());
            String value = new String(header.getValue(), 0, header.getValueEnd());

            if (name.equals(DefaultHeaders.ACCEPT_LANGUAGE_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONTENT_LENGTH_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONTENT_TYPE_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.HOST_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.CONNECTION_NAME)) {
                headers.put(name, value);
            } else if (name.equals(DefaultHeaders.TRANSFER_ENCODING_NAME)) {
                headers.put(name, value);
            } else {
                headers.put(name, value);
            }
        }
    }

    public String getUri() {
        return this.uri;
    }

    private void parseConnection(Socket socket) {
        this.address = socket.getInetAddress();
        this.port = socket.getPort();
    }

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return "";
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return "";
    }

    @Override
    public String getPathInfo() {
        return "";
    }

    @Override
    public String getPathTranslated() {
        return "";
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        return "";
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return "";
    }

    @Override
    public String getRequestURI() {
        return "";
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return "";
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return List.of();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return "";
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Map.of();
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public String getScheme() {
        return "";
    }

    @Override
    public String getServerName() {
        return "";
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return "";
    }

    @Override
    public String getRemoteHost() {
        return "";
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return "";
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "";
    }

    @Override
    public String getLocalAddr() {
        return "";
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

}
