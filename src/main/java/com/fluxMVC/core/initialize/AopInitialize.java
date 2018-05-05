package com.fluxMVC.core.initialize;

import com.fluxMVC.core.aop.proxy.Aspect;
import com.fluxMVC.core.aop.proxy.AspectProxy;
import com.fluxMVC.core.aop.proxy.Proxy;
import com.fluxMVC.core.aop.transaction.Transaction;
import com.fluxMVC.core.mvc.handler.ClassesHandler;
import com.fluxMVC.core.mvc.handler.MvcBeanHandler;
import com.fluxMVC.core.aop.proxy.ProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/8
 */
public final class AopInitialize {

    private static final Logger logger = LoggerFactory.getLogger(AopInitialize.class);

    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                //覆盖被代理的原始类
                MvcBeanHandler.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            logger.error("aop help fail", e);
            e.printStackTrace();
        }
    }

    /**
     * 获取Aspect的Class Set
     *
     * @param aspect
     * @return class集合
     * @throws Exception
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception {
        Set<Class<?>> targetClassSet = new HashSet<>();
        Class<? extends Annotation> annotation = aspect.value();
        if (!annotation.equals(Aspect.class)) {
            targetClassSet.addAll(ClassesHandler.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 获取(Aspect-Class Set)映射
     * 包括普通切面与事务切面
     *
     * @return
     * @throws Exception
     */
    public static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     * 获取原始类的所有代理切面
     * 将(Aspect-Class Set)映射转为（Class-Aspect Set）映射
     *
     * @param proxyMap
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws IllegalAccessException, InstantiationException {
        Map<Class<?>, List<Proxy>> targetMap = new HashMap<>();
        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            for (Class<?> targetClass : targetClassSet) {
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }

    /**
     * 添加transaction切面
     * 只允许注解service层
     *
     * @param proxyMap
     */
    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) {
        Set<Class<?>> serviceClassSet = new HashSet<>();
        Set<Class<?>> classSet = ClassesHandler.getServiceClassSet();
        for (Class<?> cls : classSet) {
            Method[] methods = cls.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Transaction.class)) {
                    serviceClassSet.add(cls);
                }
            }
        }
        proxyMap.put(Transaction.class, serviceClassSet);
    }

    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Set<Class<?>> proxyClassSet = ClassesHandler.getClassSetBySuper(AspectProxy.class);
        for (Class<?> proxyClass : proxyClassSet) {
            if (proxyClass.isAnnotationPresent(Aspect.class)) {
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
    }


}
