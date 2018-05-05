package com.fluxMVC.core.aop.proxy;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public interface Proxy {
    /**
     * 链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
