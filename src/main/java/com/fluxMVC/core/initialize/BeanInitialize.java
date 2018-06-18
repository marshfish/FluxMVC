package com.fluxMVC.core.initialize;

import com.fluxMVC.core.mvc.handler.ClassesLoader;
import com.fluxMVC.core.util.ReflectionUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public final class BeanInitialize {
    /**
     * 初始化
     * ClassesLoader -> BeanContainer -> CustomBeanLoader -> MappingInitialize -> AopInitialize -> IOCInitialize
     */
    private static boolean isInitialized = false;

    public static void init() throws InvocationTargetException, IllegalAccessException {
        if (!isInitialized) {
            isInitialized = true;
            ReflectionUtil.newInstance(ClassesLoader.class).init();
        }
    }
}
