package com.fluxMVC.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public final class JsonUtil {
    private static final Logger logger = Logger.getLogger(JsonUtil.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * pojo转json
     */
    public static <T> String toJson(T obj) {
        String json;
        try {
            json = OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("convert po to json fail", e);
            throw new RuntimeException();
        }
        return json;
    }

    /**
     * json转pojo
     */
    public static <T> T fromJson(String json, Class<T> type) {
        T po;
        try {
            po = OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            logger.error("convert json to po fail", e);
            throw new RuntimeException();
        }
        return po;
    }
}
