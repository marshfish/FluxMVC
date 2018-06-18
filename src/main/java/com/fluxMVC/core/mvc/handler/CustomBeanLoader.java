package com.fluxMVC.core.mvc.handler;

import com.fluxMVC.core.annotation.dataHandler.Bean;
import com.fluxMVC.core.initialize.MappingInitialize;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.Set;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/30
 */
public class CustomBeanLoader {
    public void init() throws InvocationTargetException, IllegalAccessException {
        Set<Class<?>> configurationClassSet = ClassesLoader.getConfigurationClassSet();
        for (Class<?> cls : configurationClassSet) {
            Method[] declaredMethods = cls.getDeclaredMethods();
            Object bean = ReflectionUtil.newInstance(cls);
            //后实例化存在参数依赖的bean
            doSort(declaredMethods);
            for (Method method : declaredMethods) {
                if (method.isAnnotationPresent(Bean.class)) {
                    Parameter[] parameters = method.getParameters();
                    if (ArrayUtils.isEmpty(parameters)) {
                        Object result = method.invoke(bean);
                        BeanContainer.setBean(result.getClass(), result);
                    } else {
                        LinkedList<Object> objects = new LinkedList<>();
                        for (Parameter parameter : parameters) {
                            objects.add(BeanContainer.getBean(parameter.getType()));
                        }
                        Object result = method.invoke(bean, objects.toArray());
                        BeanContainer.setBean(result.getClass(), result);
                    }
                }
            }
        }
        ReflectionUtil.newInstance(MappingInitialize.class).init();
    }

    private void doSort(Method[] declaredMethods) {
        for (int i = 0; i < declaredMethods.length - 1; i++) {
            for (int j = 0; j < declaredMethods.length - 1 - i; j++) {
                if (declaredMethods[j].getParameters().length > 0) {
                    Method method = declaredMethods[j];
                    declaredMethods[j] = declaredMethods[j + 1];
                    declaredMethods[j + 1] = method;
                }
            }
        }
    }
}
