package com.fluxMVC.core.aop.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
public class ProxyChain {
    private final Class<?> targetClass;
    private final Object targetObject;
    private final Method targetMethod;
    private final MethodProxy methodProxy;
    private final Object[] methodParams;

    /**
     * 代理链list
     */
    private List<Proxy> proxyList = new ArrayList<>();
    /**
     * 代理链执行次数
     */
    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy, Object[] methidParams, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methidParams;
        this.proxyList = proxyList;
    }

    public Class<?> getTargetClass() { return targetClass;}

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    /**
     * 判断代理链执行次序
     * list元素依次执行
     * @return
     * @throws Throwable
     */
    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            methodResult=proxyList.get(proxyIndex++).doProxy(this);
        }else{
            //最后执行被代理方法
            methodResult=methodProxy.invokeSuper(targetObject,methodParams);
        }
        return methodResult;
    }
}
