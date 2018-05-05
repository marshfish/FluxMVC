package com.fluxMVC.core.aop.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * Title:    FluxMVC
 * Description:
 *
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
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList) {
        return (T) Enhancer.create(targetClass,(MethodInterceptor) (targetObject, targetMethod, targetParmas, methodProxy) -> new ProxyChain(targetClass, targetObject, targetMethod, methodProxy, targetParmas, proxyList).doProxyChain());
    }
}
