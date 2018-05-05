package com.fluxMVC.core.mvc.dataHandler;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/17
 */
public class GsonMessgeConventer extends AbstractGsonMessageConventer {
    private final Gson gson = new Gson();

    @Override
    public <T> T read(String source, Class<T> clazz, Type type) {
        if (clazz == null) {
            return gson.fromJson(source, type);
        } else {
            return gson.fromJson(source, clazz);
        }
    }

    @Override
    public String write(Object obj) {
        return gson.toJson(obj);
    }


}
