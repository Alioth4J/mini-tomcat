package server.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringManager {

    private static Map<String, StringManager> managers = new ConcurrentHashMap<>();

    private StringManager(String packageName) {
    }

    public String getString(String key) {
        if (key == null) {
            String msg = "key is null";
            throw new NullPointerException(msg);
        }
        String str = null;
        str = key;
        return str;
    }

    public String getString(String key, Object[] args) {
        String value = getString(key);
        Object[] nonNullArgs = args;
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                if (nonNullArgs == args) {
                    nonNullArgs = (Object[]) args.clone();
                    nonNullArgs[i] = "null";
                }
            }
        }
        String iString = MessageFormat.format(value, nonNullArgs);
        return iString;
    }

    public String getString(String key, Object arg) {
        Object[] args = new Object[]{arg};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2) {
        Object[] args = new Object[]{arg1, arg2};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2, Object arg3) {
        Object[] args = new Object[]{arg1, arg2, arg3};
        return getString(key, args);
    }

    public String getString(String key, Object arg1, Object arg2, Object arg3, Object arg4) {
        Object[] args = new Object[]{arg1, arg2, arg3, arg4};
        return getString(key, args);
    }

    public static synchronized StringManager getManager(String packageName) {
        StringManager mgr = managers.get(packageName);
        if (mgr == null) {
            mgr = new StringManager(packageName);
            managers.put(packageName, mgr);
        }
        return mgr;
    }

}
