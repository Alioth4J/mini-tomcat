package server.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class WebappClassLoader extends URLClassLoader {

    protected boolean delegate = false;
    private ClassLoader parent = null;
    private ClassLoader system = null;

    public WebappClassLoader() {
        super(new URL[0]);
        this.parent = getParent();
        this.system = getSystemClassLoader();
    }

    public WebappClassLoader(URL[] urls) {
        super(urls);
        this.parent = getParent();
        this.system = getSystemClassLoader();
    }

    public WebappClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.parent = parent;
        this.system = getSystemClassLoader();
    }

    public WebappClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.parent = parent;
        this.system = getSystemClassLoader();
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    public Class findClass(String name) throws ClassNotFoundException {
        Class clazz = super.findClass(name);
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }
        return clazz;
    }

    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, false);
    }

    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = null;
        // 系统类加载器
        clazz = system.loadClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        // 双亲委派
        if (delegate) {
            ClassLoader loader = parent;
            if (loader == null) {
                loader = system;
            }
            clazz = loader.loadClass(name);
            if (clazz != null) {
                if (resolve) {
                    resolveClass(clazz);
                }
                return clazz;
            }
        }
        // 自己加载
        clazz = findClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }
            return clazz;
        }
        // 无法加载
        throw new ClassNotFoundException(name);
    }

    private void log(String message) {
        System.out.println("WebappClassLoader: " + message);
    }

    private void log(String message, Throwable throwable) {
        System.out.println("WebappClassLoader: " + message);
        throwable.printStackTrace(System.out);
    }

}
