package com.fluxMVC.core.aop.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public abstract class AspectProxy implements Proxy {
    /**
     * 代理接口实现
     */
    private static final Logger logger = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result;

        Class<?> targetClass = proxyChain.getTargetClass();
        Method targetMethod = proxyChain.getTargetMethod();
        Object[] methodParams = proxyChain.getMethodParams();

        begin();
        try {
            if (intercept(targetClass, targetMethod, methodParams)) {
                before(targetClass, targetMethod, methodParams);
                result = proxyChain.doProxyChain();
                after(targetClass, targetMethod, methodParams);
            } else {
                //回调，实现代理链
                result = proxyChain.doProxyChain();
            }
        } catch (Exception e) {
            logger.error("proxy failure", e);
            error(targetClass, targetMethod, methodParams, e);
            throw e;
        } finally {
            end();
        }
        return result;
    }

    private void begin() {

    }

    private boolean intercept(Class<?> targetClass, Method targetMethod, Object[] methodParams) {
        return true;
    }

    public void before(Class<?> targetClass, Method targetMethod, Object[] methodParams) {

    }

    public void after(Class<?> targetClass, Method targetMethod, Object[] methodParams) {

    }

    private void end() {

    }

    private void error(Class<?> cls, Method method, Object[] params, Object resuslt) {

    }
}
