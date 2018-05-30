package com.fluxMVC.core.mvc.dataHandler;

import com.fluxMVC.core.mvc.handler.BeanContainer;
import com.google.gson.Gson;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/17
 */
public abstract class AbstractGsonMessageConventer implements MessageConventer {

    protected Gson getGson() {
        Gson bean = BeanContainer.getBean(Gson.class);
        return bean == null ? new Gson() : bean;
    }
    public static void main(String[] args) {
        Class<AbstractGsonMessageConventer> dispatcherServletClass = AbstractGsonMessageConventer.class;
        Method[] methods = dispatcherServletClass.getMethods();
        Method method = methods[11];
        Parameter[] parameters = method.getParameters();
        method.getName();
    }
}
