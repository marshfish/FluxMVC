package com.fluxMVC.core.aop.proxy;

import java.lang.annotation.*;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * bean类型，如controller，service
     * @return
     */
    Class<? extends Annotation> value();
}
