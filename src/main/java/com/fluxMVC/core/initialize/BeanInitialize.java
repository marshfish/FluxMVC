package com.fluxMVC.core.initialize;

import com.fluxMVC.core.mvc.handler.ClassesHandler;
import com.fluxMVC.core.util.ReflectionUtil;

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
     */
    private static boolean isInitialized;

    public static void init() {
        if (!isInitialized) {
            ClassesHandler handler = (ClassesHandler) ReflectionUtil.newInstance(ClassesHandler.class);
            handler.init();
            isInitialized = true;
        }
    }
}
