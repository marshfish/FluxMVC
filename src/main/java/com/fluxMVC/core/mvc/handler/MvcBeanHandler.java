package com.fluxMVC.core.mvc.handler;

import com.fluxMVC.core.mvc.dataHandler.GsonMessgeConventer;
import com.fluxMVC.core.util.ReflectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public final class MvcBeanHandler {
    /**
     * MVC bean容器
     */
    private static final Map<Class<?>, Object> BEAN_MAP = new HashMap<>();

    static {
        Set<Class<?>> beanClassSet = ClassesHandler.getBeanClassSet();
        addInnerBeanWithoutStatus(beanClassSet);
        for (Class<?> cls : beanClassSet) {
            BEAN_MAP.put(cls, ReflectionUtil.newInstance(cls));
        }

    }

    private static void addInnerBeanWithoutStatus(Set<Class<?>> set) {
        set.add(GsonMessgeConventer.class);
    }

    /**
     * 获取bean Map
     */
    public static Map<Class<?>, Object> getBeanMap() {
        return BEAN_MAP;
    }

    /**
     * 注入bean Map
     */
    public static void setBean(Class<?> cls, Object obj) {
        BEAN_MAP.put(cls, obj);
    }

    /**
     * 获取bean实例
     */
    public static <T> T getBean(Class<T> cls) {
        if (!BEAN_MAP.containsKey(cls)) {
            throw new RuntimeException("handler not found:" + cls.getName());
        }
        return (T) BEAN_MAP.get(cls);
    }

    /**
     * 获取bean定义
     * @param simpleName 非全限定名
     * @return List<String>
     */
    public static List<String> getBeanDefination(boolean simpleName) {
        Set<Class<?>> classSet = BEAN_MAP.keySet();
        List<String> list = new ArrayList<>();
        for (Class<?> cls : classSet) {
            if (simpleName) {
                list.add(cls.getSimpleName());
            } else {
                list.add(cls.getName());
            }
        }
        return list;
    }


}
