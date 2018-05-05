package com.fluxMVC.core.initialize;

import com.fluxMVC.core.mvc.handler.ClassesHandler;
import com.fluxMVC.core.mvc.handler.MvcBeanHandler;
import com.fluxMVC.core.util.ReflectionUtil;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public final class BeanInitialize {
    /**
     * 加载helper
     */
    public static void init() {
        Class<?>[] classes = {
                ClassesHandler.class, MvcBeanHandler.class, IOCInitialize.class, ControllerMapping.class, AopInitialize.class
        };
        for (Class<?> aClass : classes) {
            ReflectionUtil.newInstance(aClass);
        }
    }
}
