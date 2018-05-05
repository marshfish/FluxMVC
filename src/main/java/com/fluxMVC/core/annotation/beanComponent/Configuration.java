package com.fluxMVC.core.annotation.beanComponent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    String value() default "";
}
