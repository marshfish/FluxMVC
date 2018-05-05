package com.fluxMVC.core.annotation.dataHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    /**
     * 请求路径
     */
     String  value();
    /**
     * 请求方法
     */
    String method() default "get";
}
