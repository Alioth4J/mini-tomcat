package server.core;

public final class FilterMap {

    private String filterName = null;
    private String servletName = null;
    private String urlPattern = null;

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String servletName) {
        this.servletName = servletName;
    }

    public String getURLPattern() {
        return urlPattern;
    }

    public void setURLPattern(String urlPattern) {
        this.urlPattern = urlPattern;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FilterMap[");
        sb.append("filterName=");
        sb.append(filterName);
        if (servletName != null) {
            sb.append(",servletName=");
            sb.append(servletName);
        }
        if (urlPattern != null) {
            sb.append(",urlPattern=");
            sb.append(urlPattern);
        }
        sb.append("]");
        return sb.toString();
    }

}
