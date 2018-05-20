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
    LONGABLE("java.lang.long"),
    BOOLEANABLE("java.lang.boolean"),
    FLOATABLE("java.lang.float"),

    INT("int"),
    LONG("long"),
    BOOLEAN("boolean"),
    FLOAT("float");

    private String name;

    TypeName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
