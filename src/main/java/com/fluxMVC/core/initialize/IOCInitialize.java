package com.fluxMVC.core.initialize;

import com.fluxMVC.core.annotation.dataHandler.Inject;
import com.fluxMVC.core.mvc.handler.MvcBeanHandler;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public final class IOCInitialize {

    public void init() {
        Map<Class<?>, Object> beanMap = MvcBeanHandler.getBeanMap();
        if (MapUtils.isNotEmpty(beanMap)) {
            for (Map.Entry<Class<?>, Object> entry : beanMap.entrySet()) {
                Class<?> key = entry.getKey();
                Object value = entry.getValue();
                Field[] fields = key.getDeclaredFields();
                for (Field field : fields) {
                    //实例化注入bean
                    if (field.isAnnotationPresent(Inject.class)) {
                        Class<?> type = field.getType();
                        //注意要保证加载顺序
                        Object fieldInstanceObject = beanMap.get(type);
                        if (null != fieldInstanceObject) {
                            ReflectionUtil.setField(value, field, fieldInstanceObject);
                        } else {
                            throw new RuntimeException("handler{" + type.getName() + "} has not been initializing");
                        }
                    }
                }
            }
        }
        ControllerMapping handler = (ControllerMapping) ReflectionUtil.newInstance(ControllerMapping.class);
        handler.init();
    }
}
