package server.logger;

import server.util.StringManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

public class FileLogger extends LoggerBase {

    private String date = "";
    private String directory = "logs";
    protected static final String info = "com.alioth4j.mini-tomcat.logger.FileLogger/1.0";
    private String prefix = "mini-tomcat";
    private String suffix = ".log";
    private boolean started = false;
    private boolean timestamp = true;
    private StringManager sm = StringManager.getManager(Constants.PACKAGE);
    private PrintWriter writer = null;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        String oldDirectory = this.directory;
        this.directory = directory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isTimestamp() {
        return timestamp;
    }

    public void setTimestamp(boolean timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void log(String msg) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        String tsString = ts.toString().substring(0, 19);
        String tsDate = tsString.substring(0, 10);
        if (!date.equals(tsDate)) {
            synchronized (this) {
                if (!date.equals(tsDate)) {
                    close();
                    date = tsDate;
                    open();
                }
            }
        }
        if (writer != null) {
            if (timestamp) {
                writer.println(tsString + " " + msg);
            } else {
                writer.println(msg);
            }
        }
    }

    private void close() {
        if (writer == null) {
            return;
        }
        writer.flush();
        writer.close();
        writer = null;
        date = "";
    }

    private void open() {
        File dir = new File(directory);
        if (!dir.isAbsolute()) {
            dir = new File(System.getProperty("catalina.base"), directory);
        }
        dir.mkdirs();
        try {
            String pathname = dir.getAbsolutePath() + File.separator + prefix + date + suffix;
            writer = new PrintWriter(new FileWriter(pathname, true), true);
        } catch (IOException e) {
            writer = null;
            e.printStackTrace();
        }
    }

}
