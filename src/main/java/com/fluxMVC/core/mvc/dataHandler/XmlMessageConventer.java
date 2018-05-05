package com.fluxMVC.core.mvc.dataHandler;

import java.lang.reflect.Type;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/17
 */
public class XmlMessageConventer implements MessageConventer {

    @Override
    public <T> T read(String source, Class<T> clazz, Type type) {
        return null;
    }

    @Override
    public String write(Object obj) {
        return null;
    }


}
