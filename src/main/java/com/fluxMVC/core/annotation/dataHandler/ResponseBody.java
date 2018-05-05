package com.fluxMVC.core.annotation.dataHandler;

import com.fluxMVC.core.annotation.annotationEnum.ResultData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseBody {

    ResultData rule() default ResultData.JSON;

    boolean serializeNull() default false;
}
