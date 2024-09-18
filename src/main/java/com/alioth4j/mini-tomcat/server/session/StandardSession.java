package server.session;

import server.Session;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StandardSession implements HttpSession, Session {

    private String sessionid;
    private long creationTime;
    private boolean valid;
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return "";
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int interval) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Object getValue(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(this.attributes.keySet());
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public void putValue(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public void removeValue(String name) {

    }

    @Override
    public void invalidate() {
        this.valid = false;
    }

    @Override
    public boolean isNew() {
        return false;
    }

    @Override
    public void setCreationTime(long time) {
        this.creationTime = time;
    }

    @Override
    public void setId(String id) {
        this.sessionid = id;
    }

    @Override
    public String getInfo() {
        return "";
    }

    @Override
    public void setNew(boolean isNew) {

    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public void setValid(boolean isValid) {
        this.valid = isValid;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void access() {

    }

    @Override
    public void expire() {

    }

    @Override
    public void recycle() {

    }

}
