package com.fluxMVC.core.initialize;

import com.fluxMVC.core.mvc.handler.ClassesHandler;
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
     * ClassesHandler -> BeanContainer -> ControllerMapping -> AopInitialize -> CustomBeanHandler -> IOCInitialize
     */
    private static boolean isInitialized;

    public static void init() throws InvocationTargetException, IllegalAccessException {
        if (!isInitialized) {
            ReflectionUtil.newInstance(ClassesHandler.class).init();
            isInitialized = true;
        }
    }
}
