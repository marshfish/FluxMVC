package com.fluxMVC.core.initialize;

import com.fluxMVC.core.annotation.dataHandler.Inject;
import com.fluxMVC.core.annotation.exception.ServerInnerException;
import com.fluxMVC.core.mvc.handler.BeanContainer;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public void init() throws InvocationTargetException, IllegalAccessException {
        Map<Class<?>, Object> beanMap = BeanContainer.getBeanMap();
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
                        Object injectBean = beanMap.get(type);
                        if (null != injectBean) {
                            ReflectionUtil.setField(value, field, injectBean);
                        } else {
                            throw new ServerInnerException("handler{" + type.getName() + "} has not been initializing");
                        }
                    }
                }
            }
        }
    }
}
