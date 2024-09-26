package server.session;

import server.Session;
import server.SessionEvent;
import server.SessionListener;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StandardSession implements HttpSession, Session {

    private String sessionid;
    private long creationTime;
    private boolean valid;

    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private transient List<SessionListener> listeners = new ArrayList<>();

    public void addSessionListener(SessionListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeSessionListener(SessionListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void fireSessionEvent(String type, Object data) {
        if (listeners.size() < 1) {
            return;
        }
        SessionEvent sessionEvent = new SessionEvent(this, type, data);
        SessionListener[] arrayListeners = new SessionListener[0];
        synchronized (listeners) {
            arrayListeners =    listeners.toArray(arrayListeners);
        }
        for (int i = 0; i < arrayListeners.length; i++) {
            arrayListeners[i].sessionEvent(sessionEvent);
        }
    }

    @Override
    public void setId(String sessionId) {
        this.sessionid = sessionId;
        fireSessionEvent(Session.SESSION_CREATE_EVENT, null);
    }

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
