package server.logger;

import server.Logger;

import javax.servlet.ServletException;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

public abstract class LoggerBase implements Logger {

    protected int debug = 0;
    protected static final String info = "com.alioth4j.mini-tomcat.logger.LoggerBase/1.0";
    protected int verbosity = ERROR;

    public int getDebug() {
        return this.debug = 0;
    }

    public void setDebug(int debug) {
        this.debug = debug;

    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public int getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }

    public void setVerbosityLevel(String verbosity) {
        if ("FATAL".equalsIgnoreCase(verbosity)) {
            this.verbosity = FATAL;
        } else if ("ERROR".equalsIgnoreCase(verbosity)) {
            this.verbosity = ERROR;
        } else if ("WARINING".equalsIgnoreCase(verbosity)) {
            this.verbosity = WARNING;
        } else if ("INFORMATION".equalsIgnoreCase(verbosity)) {
            this.verbosity = INFORMATION;
        } else if ("DEBUG".equalsIgnoreCase(verbosity)) {
            this.verbosity = DEBUG;
        }
    }

    public abstract void log(String msg);

    public void log(Exception exception, String msg) {
        log(msg, exception);
    }

    /**
     * 核心方法
     * 把 Throwable 写入信息中，之后调用 log(String msg)
     * @param msg 异常信息
     * @param throwable 抛出的异常
     */
    public void log(String msg, Throwable throwable) {
        CharArrayWriter buf = new CharArrayWriter();
        PrintWriter writer = new PrintWriter(buf);
        writer.println(msg);
        throwable.printStackTrace(writer);
        Throwable rootCause = null;
        if (throwable instanceof ServletException) {
            rootCause = ((ServletException) throwable).getRootCause();
        }
        if (rootCause != null) {
            writer.println("----- Root Cause -----");
            rootCause.printStackTrace(writer);
        }
        log(buf.toString());
    }

    public void log(String message, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message);
        }
    }

    public void log(String message, Throwable throwable, int verbosity) {
        if (this.verbosity >= verbosity) {
            log(message, throwable);
        }
    }

}
