package com.fluxMVC.core.mvc.bean;

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
public class Handler {
    /**
     * congroller
     */
    private Class<?> controllerClass;
    /**
     * action
     */
    private Method actionMethod;
    public Handler(Class<?> controllerClass,Method actionMethod){
        this.actionMethod=actionMethod;
        this.controllerClass=controllerClass;
    }

    public Class<?> getControllerClass() {
        return controllerClass;
    }

    public Method getActionMethod() {
        return actionMethod;
    }

}
