package com.fluxMVC.core.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/5
 */
public final class ClassUtil {
    private static final Logger logger = Logger.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     * 没有指定线程上下文classLoader，默认采用bootstrap classLoader
     */
    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     */
    private static Class<?> loadClass(String className, boolean isInitialized) {
        Class<?> cla = null;
        try {
            cla = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.error("ClassNotFoundException");
            e.printStackTrace();
        }

        return cla;
    }

    /**
     * 获取指定包名下的所有类文件,可读取class或jar
     * Enumeration古代迭代器
     */
    public static Set<Class<?>> getClassSet(String packageName) {
        Set<Class<?>> classes = new HashSet<>();
        try {
            //获取文件系统下的资源文件，url路径类似 file:/C:/Users/Kaibo/OneDrive/workplaceForIDEA/FluxMVC/target/classes/com/smar4j/framework/initialize
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (null != url) {
                    String protocol = url.getProtocol();
                    //判断url协议，file还是jar
                    if ("file".equals(protocol)) {
                        //替换url中的空格
                        String packagePath = url.getPath().replaceAll("%20", "");
                        addClass(classes, packagePath, packageName);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * 读取jar包中的class文件
     *
     * @param classes class文件集合
     * @param url     读取资源路径
     */
    private static void JarHandler(Set<Class<?>> classes, URL url) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
        if (null != jarURLConnection) {
            JarFile jarFile = jarURLConnection.getJarFile();
            if (null != jarFile) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();
                    String name = jarEntry.getName();
                    if (name.endsWith(".class")) {
                        String className = name.substring(0, name.lastIndexOf(".")).replaceAll("/", ".");
                        doAddClass(classes, className);
                    }
                }
            }
        }
    }

    /**
     * 扫描包下的所有class文件
     *
     * @param classes     class文件集合
     * @param packagePath 读取资源路径（文件系统）
     * @param packageName 全限定包名
     */
    private static void addClass(Set<Class<?>> classes, String packagePath, String packageName) {
        File[] files = getFiles(packagePath);
        for (File file : files) {
            String fileName = file.getName();
            //如果不是目录
            if (file.isFile()) {
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                if (StringUtils.isNotEmpty(packageName)) {
                    className = packageName + "." + className;
                    doAddClass(classes, className);
                } else {
                    //若不提供packageName
                    String subPackagePath = fileName;
                    if (StringUtils.isNotEmpty(packagePath)) {
                        subPackagePath = packagePath + "/" + subPackagePath;
                    }
                    String subPackageName = fileName;
                    if (StringUtils.isNotEmpty(packageName)) {
                        subPackageName = packageName + "." + subPackageName;
                    }
                    addClass(classes, subPackagePath, subPackageName);
                }
            } else if (file.isDirectory()) {
                //若为目录则继续递归
                addClass(classes, file.getPath(), packageName + "." + fileName);
            }
        }
    }

    /**
     * 列出子文件并过滤
     *
     * @param packagePath 资源路径
     * @return
     */
    private static File[] getFiles(String packagePath) {
        return new File(packagePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
    }

    /**
     * 加载类并注册到class set中
     *
     * @param classes
     * @param className
     */
    private static void doAddClass(Set<Class<?>> classes, String className) {
        Class<?> cls = loadClass(className, false);
        classes.add(cls);
    }
}
