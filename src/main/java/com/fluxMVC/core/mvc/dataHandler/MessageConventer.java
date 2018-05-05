package com.fluxMVC.core.mvc.dataHandler;

import java.lang.reflect.Type;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/17
 */
public interface MessageConventer {
    <T> T read(String source, Class<T> clazz, Type type);

    String write(Object obj);
}
