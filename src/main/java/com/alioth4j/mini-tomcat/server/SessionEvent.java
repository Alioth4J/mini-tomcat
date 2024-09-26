package server;

import java.util.EventObject;

public final class SessionEvent extends EventObject {

    private Object data = null;
    private Session session = null;
    private String type = null;

    public SessionEvent(Session session, String type, Object data) {
        super(session);
        this.session = session;
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return this.data;
    }

    public Session getSession() {
        return session;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SessionEvent[" +
                ", session=" + session +
                ", type='" + type +
                ']';
    }

}
