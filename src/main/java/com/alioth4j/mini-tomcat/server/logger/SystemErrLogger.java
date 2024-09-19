package server.logger;

public class SystemErrLogger extends LoggerBase {

    protected static final String info = "com.alioth4j.mini-tomcat.logger.SystemErrLogger/1.0";

    @Override
    public void log(String msg) {
        System.err.println(msg);
    }

}
