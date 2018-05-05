package com.fluxMVC.core.util;

import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/4
 */
public final class PropertiesUtil implements AutoCloseable {

    private static final Logger logger = Logger.getLogger(PropertiesUtil.class);


    public void close() throws Exception {
    }

    private static Map<String, Properties> map = new HashMap<>();

    public static Properties loadProps(String fileName) {
        if (map.containsKey(fileName)) {
            return map.get(fileName);
        }
        Properties properties = null;
        //线程上下文类加载器
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (null == in) {
                throw new FileNotFoundException(fileName + " file is not found");
            }
            properties = new Properties();
            properties.load(in);
            //缓存properties
            map.put(fileName, properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    //TODO 返回默认值不安全
    public static String getString(Properties properties, String key) {
        /**
         * 获取属性
         */
        return getString(properties, key, "");
    }

    public static String getString(Properties properties, String key, String defaultValue) {
        /**
         * 获取属性，可指定默认值
         */
        String props = properties.getProperty(key);
        if (null == props) {
            return defaultValue;
        }
        return props;
    }

    public static int getInt(Properties properties, String key) {
        /**
         * 获取数值属性
         */
        return getInt(properties, key, 0);
    }

    public static int getInt(Properties properties, String key, int defaultValue) {
        /**
         * 获取数值属性,可指定默认值
         */
        int value = defaultValue;
        if (properties.containsKey(key)) {
            return CastUtil.castInt(properties.getProperty(key));
        }
        return value;
    }

    public static boolean getBoolean(Properties properties, String key) {
        /**
         * 获取boolean属性
         */
        return getBoolean(properties, key, false);
    }

    public static boolean getBoolean(Properties properties, String key, Boolean defaultValue) {
        /**
         * 获取boolean属性,可指定默认值
         */
        boolean value = defaultValue;
        if (properties.containsKey(key)) {
            return CastUtil.castBoolean(properties.getProperty(key));
        }
        return value;
    }
}
