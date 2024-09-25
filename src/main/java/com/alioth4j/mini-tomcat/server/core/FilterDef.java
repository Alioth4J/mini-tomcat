package server.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FilterDef {

    private String description = null;
    private String displayName = null;
    private String filterClass = null;
    private String filterName = null;
    private String largeIcon = null;
    private String smallIcon = null;
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

    public String getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(String filterClass) {
        this.filterClass = filterClass;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public void setLargeIcon(String largeIcon) {
        this.largeIcon = largeIcon;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public void setSmallIcon(String smallIcon) {
        this.smallIcon = smallIcon;
    }

    public Map<String, String> getParameterMap() {
        return parameters;
    }

    public void addInitParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("FilterDef[");
        sb.append("filterName=");
        sb.append(this.filterName);
        sb.append(",filterClass=");
        sb.append(this.filterClass);
        sb.append("]");
        return sb.toString();
    }

}
