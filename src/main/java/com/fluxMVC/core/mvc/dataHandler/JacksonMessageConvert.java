package com.fluxMVC.core.mvc.dataHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/30
 */
public class JacksonMessageConvert extends AbstractGsonMessageConventer {
    //TODO BEAN CONTAINER
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger logger= LoggerFactory.getLogger(JacksonMessageConvert.class);

    @Override
    public <T> T read(String source, Class<T> clazz, Type type) {
        T po;
        try {
            po = OBJECT_MAPPER.readValue(source, clazz);
        } catch (Exception e) {
            logger.error("convert json to po fail", e);
            throw new RuntimeException();
        }
        return po;
    }

    @Override
    public String write(Object obj) {
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("convert po to json fail", e);
            throw new RuntimeException();
        }
        return json;
    }
}
