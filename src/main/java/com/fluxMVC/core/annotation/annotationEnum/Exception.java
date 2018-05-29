package com.fluxMVC.core.annotation.annotationEnum;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/5
 */
public enum Exception {
    PATH_NOT_FOUND("#resource not Found"),
    MAPPING_NOT_FOUND("can not found mapping by this path"),
    ARGS_EMPTY("argument is null :"),
    EXPRESSION_EMPTY("@pathvariable expression is not exist"),
    EXPRESSION_ERROR("path error in @pathvariable，please check  param expression is exist");

    private String info;

    Exception(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
