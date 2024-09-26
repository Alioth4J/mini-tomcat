package server.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ContainerListenerDef {

    private String description = null;
    private String displayName = null;
    private String listenerClass = null;
    private String listenerName = null;

    private Map<String, String> parameters = new ConcurrentHashMap<>();

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public void setListenerClass(String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public String getListenerName() {
        return listenerName;
    }

    public void setListenerName(String listenerName) {
        this.listenerName = listenerName;
    }

    public Map<String, String> getParameterMap() {
        return this.parameters;
    }

    public void addParameter(String name, String value) {
        parameters.put(name, value);
    }

    @Override
    public String toString() {
        return "ContainerListenerDef[" +
                "description='" + description +
                ", displayName='" + displayName +
                ", listenerClass='" + listenerClass +
                ", listenerName='" + listenerName +
                ", parameters=" + parameters +
                ']';
    }

}
