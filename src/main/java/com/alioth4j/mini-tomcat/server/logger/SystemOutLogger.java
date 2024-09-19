package server.logger;

public class SystemOutLogger extends LoggerBase {

    protected static final String info = "com.alioth4j.mini-tomcat.logger.SystemOutLogger/1.0";

    @Override
    public void log(String msg) {
        System.out.println(msg);
    }

}
