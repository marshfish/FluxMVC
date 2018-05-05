package com.fluxMVC.core.util;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public final class ReflectionUtil {
    private static final Logger logger = Logger.getLogger(ReflectionUtil.class);

    /**
     * 创建实例
     */
    public static Object newInstance(Class<?> cls) {
        Object obj = null;
        try {
            obj = cls.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * 调用方法
     */
    public static Object invockMethod(Object obj, Method method, Object... args) {
        method.setAccessible(true);
        Object result = null;
        try {
            result = method.invoke(obj, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 设置成员变量的值
     */
    public static void setField(Object obj,Field field,Object value){
        field.setAccessible(true);
        try {
            field.set(obj,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
