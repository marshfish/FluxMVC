package com.fluxMVC.core.aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public class ProxyManager {
    /**
     * 代理管理器
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final Class<?> targetClass, final List<T> proxyList) {
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            @Override
            public Object intercept(Object targetObject, Method targetMethod, Object[] targetParams, MethodProxy methodProxy) throws Throwable {
                return new ProxyChain<>(targetClass, targetObject, targetMethod, methodProxy, targetParams, proxyList).doProxyChain();
            }
        });
    }
}
