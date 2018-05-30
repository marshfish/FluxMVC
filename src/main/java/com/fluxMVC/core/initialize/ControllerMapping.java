package com.fluxMVC.core.initialize;

import com.fluxMVC.core.annotation.beanComponent.Controller;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.annotation.exception.PathException;
import com.fluxMVC.core.mvc.handler.ClassesHandler;
import com.fluxMVC.core.util.JavaassistUtil;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public final class ControllerMapping {
    public static final Map<Class, List<Method>> METHOD_MAP = new HashMap<>();
    public static final Map<String, List<Class>> CONTROLLER_MAP = new HashMap<>();
    public static final Map<Class, Map<String, String[]>> PARAMTETER_CACHE = new HashMap<>();


    public static List<Method> getMethodMapping(Class cls) {
        return METHOD_MAP.get(cls);
    }

    public static List<Class> getControllerMapping(String pathA) {
        return CONTROLLER_MAP.get(pathA);
    }

    public static Set<String> getControllerMapping() {
        return CONTROLLER_MAP.keySet();
    }

    public void init() throws InvocationTargetException, IllegalAccessException {
        Set<Class<?>> classSet = ClassesHandler.getControllerClassSet();
        if (CollectionUtils.isNotEmpty(classSet)) {
            for (Class<?> cls : classSet) {
                Controller annotation = cls.getAnnotation(Controller.class);
                String requestPathA = annotation.path().toLowerCase();
                //注意path为""时获取controller List
                if (CONTROLLER_MAP.containsKey(requestPathA)) {
                    if (requestPathA.equals("")) {
                        List<Class> classList = CONTROLLER_MAP.get(requestPathA);
                        classList.add(cls);
                    } else {
                        throw new PathException("controller mapping path conflict");
                    }
                } else {
                    List<Class> classList = new ArrayList<>(classSet.size());
                    classList.add(cls);
                    CONTROLLER_MAP.put(requestPathA, classList);
                }
                Method[] methods = cls.getDeclaredMethods();
                if (!ArrayUtils.isEmpty(methods)) {
                    List<Method> methodList = new ArrayList<>();
                    Map<String, String[]> mappingCache = new HashMap<>();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            methodList.add(method);
                            mappingCache.put(method.getName(), JavaassistUtil.getParameterNames(cls, method.getName()));
                        }
                    }
                    METHOD_MAP.put(cls, methodList);
                    PARAMTETER_CACHE.put(cls, mappingCache);
                }
            }
        }
        ReflectionUtil.newInstance(AopInitialize.class).init();
    }
}
