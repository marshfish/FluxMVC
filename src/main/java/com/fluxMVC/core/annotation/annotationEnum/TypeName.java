package com.fluxMVC.core.annotation.annotationEnum;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/5
 */
public enum TypeName {
    INTEGER("java.lang.integer"),
    STRING("java.lang.string"),
    LONG("java.lang.long"),
    BOOLEAN("java.lang.boolean"),
    FLOAT("java.lang.float");

    private String name;

    TypeName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
