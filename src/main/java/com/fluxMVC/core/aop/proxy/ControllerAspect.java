package com.fluxMVC.core.aop.proxy;

import com.fluxMVC.core.annotation.beanComponent.Controller;
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
 * @Ddate 2018/1/8
 */
@Aspect(value = Controller.class)
public class ControllerAspect extends AspectProxy {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);
    private long begin;

    @Override
    public void before(Class<?> targetClass, Method targetMethod, Object[] methodParams) {
        logger.debug("begin execute dynamic proxy");
        logger.debug(String.format("class: %s", targetClass.getName()));
        logger.debug(String.format("method: %s", targetMethod.getName()));
        begin = System.currentTimeMillis();
    }

    @Override
    public void after(Class<?> targetClass, Method targetMethod, Object[] methodParams) {
        logger.debug("end execute dynamic proxy");
        logger.debug(String.format("time cost: %dms", System.currentTimeMillis() - begin));
    }
}
