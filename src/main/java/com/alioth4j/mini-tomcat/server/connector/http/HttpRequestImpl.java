package server.connector.http;

import server.session.StandardSessionFacade;

import javax.servlet.*;
import javax.servlet.ServletContext;
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

public class HttpRequestImpl implements HttpServletRequest {

    private InputStream input;
    private SocketInputStream sis;
    private String uri;
    private String queryString;
    private boolean parsed = false;
    private InetAddress address;
    private int port;

    protected HttpRequestLine requestLine = new HttpRequestLine();
    protected Map<String, String> headers = new HashMap<>();
    protected Map<String, String[]> parameters = new ConcurrentHashMap<>();
    Cookie[] cookies;
    HttpSession session;
    String sessionid;
    StandardSessionFacade sessionFacade;

    private HttpResponseImpl response;

    public HttpRequestImpl(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void setStream(InputStream input) {
        this.input = input;
        this.sis = new SocketInputStream(this.input, 2048);
    }

    public void setResponse(HttpResponseImpl response) {
        this.response = response;
    }

    public void parse(Socket socket) {
        try {
            parseConnection(socket);
            this.sis.readRequestLine(this.requestLine);
            parseHeaders();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parseRequestLine() {
        int question = requestLine.indexOf("?");
        if (question >= 0) {
            queryString = new String(requestLine.getUri(), question + 1, requestLine.getUriEnd() - question - 1);
            uri = new String(requestLine.getUri(), 0, question);

        } else {
            queryString = null;
            uri = new String(requestLine.getUri(), 0, requestLine.getUriEnd());
        }
        // 从 uri 中得到 sessionid
        String tmp = ";" + DefaultHeaders.JSESSIONID_NAME + "=";
        int semicolon = uri.indexOf(tmp);
        if (semicolon >= 0) {
            sessionid = uri.substring(semicolon + tmp.length());
            uri = uri.substring(0, semicolon);
        }
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
            name = name.toLowerCase();

            if (DefaultHeaders.ACCEPT_LANGUAGE_NAME.equals(name)) {
                headers.put(name, value);
            } else if (DefaultHeaders.CONTENT_LENGTH_NAME.equals(name)) {
                headers.put(name, value);
            } else if (DefaultHeaders.CONTENT_TYPE_NAME.equals(name)) {
                headers.put(name, value);
            } else if (DefaultHeaders.HOST_NAME.equals(name)) {
                headers.put(name, value);
            } else if (DefaultHeaders.CONNECTION_NAME.equals(name)) {
                headers.put(name, value);
                if ("close".equals(value)) {
                    response.setHeader("Connection", "close");
                }
            } else if (DefaultHeaders.TRANSFER_ENCODING_NAME.equals(name)) {
                headers.put(name, value);
            } else if (DefaultHeaders.COOKIE_NAME.equals(name)) {
                headers.put(name, value);
                Cookie[] cookies = parseCookieHeader(value);
                this.cookies = cookies;
                for (int i = 0; i < cookies.length; i++) {
                    if ("jsessionid".equals(cookies[i].getName())) {
                        this.sessionid = cookies[i].getValue();
                    }
                }
            } else {
                headers.put(name, value);
            }
        }
    }

    protected void parseParameters() {
        String encoding = getCharacterEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        String qString = getQueryString();
        if (qString != null) {
            byte[] bytes = null;
            try {
                bytes = qString.getBytes(encoding);
                parseParameters(this.parameters, bytes, encoding);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String contentType = getContentType();
        if (contentType == null) {
            contentType = "";
        }
        int semicolon = contentType.indexOf(";");
        if (semicolon >= 0) {
            contentType = contentType.substring(0, semicolon).trim();
        } else {
            contentType = contentType.trim();
        }
        if ("POST".equals(getMethod()) && getContentLength() > 0 && "application/x-www-form-unlencoded".equals(contentType)) {
            try {
                int max = getContentLength();
                int len = 0;
                byte[] buf = new byte[getContentLength()];
                ServletInputStream is = getInputStream();
                while (len < max) {
                    int next = is.read(buf, len, max - len);
                    if (next < 0) {
                        break;
                    }
                    len += next;
                }
                is.close();
                if (len < max) {
                    throw new RuntimeException("Content length mismatch");
                }
                parseParameters(this.parameters, buf, encoding);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void parseParameters(Map<String, String[]> map, byte[] data, String encoding) throws UnsupportedEncodingException {
        if (parsed) {
            return;
        }
        if (data != null && data.length > 0) {
            int pos = 0;
            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                    case '&':
                        value = new String(data, 0, ox, encoding);
                        if (key != null) {
                            putMapEntry(map, key, value);
                            key = null;
                        }
                        ox = 0;
                        break;
                    case '=':
                        key = new String(data, 0, ox, encoding);
                        ox = 0;
                        break;
                    case '+':
                        data[ox++] = (byte) ' ';
                        break;
                    case '%':
                        data[ox++] = (byte) ((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
                        break;
                    default:
                        data[ox++] = c;
                }
            }
            // 最后一对 key-value
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }
        parsed = true;
    }

    public Cookie[] parseCookieHeader(String header) {
        if (header == null || header.length() < 1) {
            return new Cookie[0];
        }
        List<Cookie> cookieal = new ArrayList<>();
        while (header.length() > 0) {
            int semicolon = header.indexOf(';');
            if (semicolon < 0) {
                semicolon = header.length();
            }
            if (semicolon == 0) {
                break;
            }
            String token = header.substring(0, semicolon);
            if (semicolon < header.length()) {
                header = header.substring(semicolon + 1);
            } else {
                header = "";
            }
            int equals = token.indexOf('=');
            if (equals > 0) {
                String name = token.substring(0, equals);
                String value = token.substring(equals + 1);
                cookieal.add(new Cookie(name, value));
            }
        }
        return cookieal.toArray(new Cookie[cookieal.size()]);
    }

    private void putMapEntry(Map<String, String[]> map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    private byte convertHexDigit(byte b) {
        if (b >= '0' && b <= '9') {
            return (byte) (b - '0');
        } else if (b >= 'a' && b <= 'f') {
            return (byte) (b - 'a' + 10);
        } else if (b >= 'A' && b <= 'F') {
            return (byte) (b - 'A' + 10);
        }
        return 0;
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
        return this.cookies;
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
        return new String(this.requestLine.getMethod(), 0, this.requestLine.getMethodEnd());
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
        return this.queryString;
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
        return this.uri;
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
    public HttpSession getSession() {
        return this.sessionFacade;
    }

    @Override
    public HttpSession getSession(boolean create) {
        if (sessionFacade != null) {
            return sessionFacade;
        }
        if (sessionid != null) {
            session = HttpConnector.getSessions().get(sessionid);
            if (session != null) {
                sessionFacade = new StandardSessionFacade(session);
                return sessionFacade;
            } else {
                session = HttpConnector.createSession();
                sessionFacade = new StandardSessionFacade(session);
                sessionid = session.getId();
                return sessionFacade;
            }
        } else {
            session = HttpConnector.createSession();
            sessionFacade = new StandardSessionFacade(session);
            sessionid = session.getId();
            return sessionFacade;
        }
    }

    public String getSessionId() {
        return this.sessionid;
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
        return headers.get(DefaultHeaders.TRANSFER_ENCODING_NAME);
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return Integer.parseInt(headers.get(DefaultHeaders.CONTENT_LENGTH_NAME));
    }

    @Override
    public long getContentLengthLong() {
        return Long.parseLong(headers.get(DefaultHeaders.CONTENT_LENGTH_NAME));
    }

    @Override
    public String getContentType() {
        return headers.get(DefaultHeaders.CONTENT_TYPE_NAME);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return this.sis;
    }

    @Override
    public String getParameter(String name) {
        parseParameters();
        String[] values = parameters.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        parseParameters();
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        parseParameters();
        String[] values = parameters.get(name);
        return values;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        parseParameters();
        return this.parameters;
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
