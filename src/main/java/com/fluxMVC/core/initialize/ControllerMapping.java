package com.fluxMVC.core.initialize;

import com.fluxMVC.core.annotation.beanComponent.Controller;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.mvc.handler.ClassesHandler;
import com.fluxMVC.core.mvc.handler.MvcBeanHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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

    public static List<Method> getMethodMapping(Class cls) {
        return METHOD_MAP.get(cls);
    }

    public static List<Class> getControllerMapping(String pathA) {
        return CONTROLLER_MAP.get(pathA) == null ? Collections.emptyList() : CONTROLLER_MAP.get(pathA);
    }

    public static Set<String> getControllerMapping() {
        return CONTROLLER_MAP.keySet();
    }

    static {
        /**
         * 注册controller与mapping method
         */
        Set<Class<?>> classSet = ClassesHandler.getControllerClassSet();
        if (CollectionUtils.isNotEmpty(classSet)) {

            for (Class<?> cls : classSet) {
                Object bean = MvcBeanHandler.getBean(cls);
                Controller annotation = cls.getAnnotation(Controller.class);
                String requestPathA = annotation.path().toLowerCase();
                //注意path为""时获取controller List
                if (CONTROLLER_MAP.containsKey(requestPathA)) {
                    if (requestPathA.equals("")) {
                        List<Class> classList = CONTROLLER_MAP.get(requestPathA);
                        classList.add(cls);
                    } else {
                        throw new RuntimeException("controller mapping path conflict");
                    }
                } else {
                    List<Class> classList = new ArrayList<>(classSet.size());
                    classList.add(cls);
                    CONTROLLER_MAP.put(requestPathA, classList);
                }
                Method[] methods = bean.getClass().getDeclaredMethods();
                if (!ArrayUtils.isEmpty(methods)) {
                    List<Method> methodList = new ArrayList<>();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
//                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
//                            String requestPathB = requestMapping.value().toLowerCase();
//                            String requestMethod = requestMapping.method().toLowerCase();
//                            NewHandler newHandler = new NewHandler(requestPathB, cls);

//                            Request request = new Request(requestMethod, path, requestPath);
//                            Handler handler = new Handler(cls, method);
                                    /*
                                    初始化action map
                                    url method和controller method映射
                                     */
//                            ACTION_MAP.put(request, handler);
//                                }
//                            }
                            methodList.add(method);
                        }
                    }
                    METHOD_MAP.put(cls, methodList);
                }
            }

        }
    }


}
