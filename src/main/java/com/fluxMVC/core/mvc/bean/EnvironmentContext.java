package com.fluxMVC.core.mvc.bean;

import com.fluxMVC.core.annotation.annotationEnum.Exception;
import com.fluxMVC.core.annotation.annotationEnum.ResultData;
import com.fluxMVC.core.annotation.annotationEnum.TypeName;
import com.fluxMVC.core.annotation.beanComponent.Controller;
import com.fluxMVC.core.annotation.dataHandler.PathVariable;
import com.fluxMVC.core.annotation.dataHandler.RequestBody;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.annotation.dataHandler.RequestParam;
import com.fluxMVC.core.annotation.dataHandler.ResponseBody;
import com.fluxMVC.core.annotation.exception.ArgsException;
import com.fluxMVC.core.annotation.exception.PathException;
import com.fluxMVC.core.mvc.dataHandler.GsonMessgeConventer;
import com.fluxMVC.core.mvc.handler.MvcBeanHandler;
import com.fluxMVC.core.util.JavaassistUtil;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/5
 */
public class EnvironmentContext {
    private Class<?> controllerClass;
    private Method actionMethod;
    private Param param;
    private String secondaryPath;
    private Object result;
    private Parameter[] parameters;
    private boolean initialize = true;

    public boolean isInitialize() {
        return initialize;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public EnvironmentContext(Class<?> controllerClass, Method actionMethod, Param param, String secondaryPath) {
        this(controllerClass, actionMethod, param, secondaryPath, null);

    }

    public EnvironmentContext(Class<?> controllerClass, Method actionMethod, Param param, String secondaryPath, Object result) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
        this.param = param;
        this.secondaryPath = secondaryPath;
        this.result = result;
        this.parameters = Optional.ofNullable(actionMethod).map(Executable::getParameters).orElse(null);
        checkNull();
    }


    private void checkNull() {
        if (controllerClass == null || actionMethod == null) {
            initialize = false;
        }
    }

    /**
     * 获取匹配的参数list
     */
    public List<Object> MatchParam() {
        Map<String, Object> map = param.getMap();
        Object obj;
        //javaassist获取运行时方法参数
        String[] parameterNames = JavaassistUtil.getParameterNames(controllerClass, actionMethod.getName());
        //方法参数类型匹配
        packageArgesType(parameters, map, parameterNames);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            requestBodyProcessor(map, parameter);
            if (requestParamProcessor(map, parameterNames, i, parameter)) {
                continue;
            }
            if (!controllerClass.getAnnotation(Controller.class).path().equals("") && parameter.isAnnotationPresent(PathVariable.class)) {
                String value = actionMethod.getAnnotation(RequestMapping.class).value();
                if (value.contains("{") && value.contains("}")) {
                    String paramName = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                    if (isExistParam(parameterNames, paramName)) {
                        //TODO 不支持中间参数
                        String newTemp = secondaryPath.substring(value.indexOf("{"));
                        String type = parameters[i].getType().getTypeName();
                        matchArgsType(map, paramName, newTemp, type, null);
                    } else {
                        throw new ArgsException(Exception.EXPRESSION_EMPTY.getInfo());
                    }
                } else {
                    throw new ArgsException(Exception.EXPRESSION_ERROR.getInfo());
                }
            }
        }
        //方法参数排序后注入
        return sortParameters(parameterNames, map);
    }

    public Object serializeResultByResponseBody() throws IOException {
        Object serialized = "";
        if (actionMethod.isAnnotationPresent(ResponseBody.class)) {
            if (result instanceof String) {
                return result;
            }
            ResponseBody annotation = actionMethod.getAnnotation(ResponseBody.class);
            ResultData rule = annotation.rule();
            if (rule == ResultData.JSON) {
                serialized = MvcBeanHandler.getBean(GsonMessgeConventer.class).write(result);
            } else if (rule == ResultData.XML) {
                //TODO XML序列化
            }
            return serialized;
        } else {
            return result;
        }
    }

    /**
     * 包装参数类型
     * 除非使用java8的javac -parameters参数，否则无法直接获取运行时参数方法名
     * 通过javaassist获取顺序的参数名列表，与参数一一对应
     *
     * @param parameters     参数列表
     * @param map            参数散列
     * @param parameterNames 参数名称列表
     */
    private void packageArgesType(Parameter[] parameters, Map<String, Object> map, String[] parameterNames) {
        for (int i = 0; i < parameters.length; i++) {
            String name = parameterNames[i];
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getKey().equals(name)) {
                    String parameterName = parameters[i].getType().getTypeName();
                    matchArgsType(map, name, String.valueOf(entry.getValue()), parameterName, null);
                }
            }
        }
    }

    /**
     * 根据实际类型转换参数
     *
     * @param map        参数map
     * @param paramName  参数名
     * @param oldVar     需要包装的参数
     * @param customType 自定义参数
     * @param type       参数类型
     */
    private void matchArgsType(Map<String, Object> map, String paramName, String oldVar, String type, Object customType) {
        if (customType != null) {
            map.put(paramName, customType);
            return;
        }
        if (type.toLowerCase().equals(TypeName.INTEGER.getName()) || type.toLowerCase().equals(TypeName.INT.getName())) {
            map.put(paramName, Integer.valueOf(oldVar));
        } else if (type.toLowerCase().equals(TypeName.STRING.getName())) {
            map.put(paramName, oldVar);
        } else if (type.toLowerCase().equals(TypeName.LONGABLE.getName()) || type.toLowerCase().equals(TypeName.LONG.getName())) {
            map.put(paramName, Long.valueOf(oldVar));
        } else if (type.toLowerCase().equals(TypeName.BOOLEANABLE.getName()) || type.toLowerCase().equals(TypeName.BOOLEAN.getName())) {
            map.put(paramName, Boolean.valueOf(oldVar));
        } else if (type.toLowerCase().equals(TypeName.FLOATABLE.getName()) || type.toLowerCase().equals(TypeName.FLOAT.getName())) {
            map.put(paramName, Float.valueOf(oldVar));
        } else {
            map.put(paramName, oldVar);
        }
    }

    /**
     * 校验该参数是否存在
     *
     * @param parameterNames
     * @param substring
     * @return
     */
    private boolean isExistParam(String[] parameterNames, String substring) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(substring)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 方法参数数量匹配
     *
     * @param parameterNames 参数名列表
     * @param map            参数map
     * @return
     */
    private List<Object> sortParameters(String[] parameterNames, Map<String, Object> map) {
        List<Object> sortList = new ArrayList<>(parameterNames.length);
        //填充不足的参数
        for (int i = 0; i < parameterNames.length; i++) {
            sortList.add(null);
        }
        for (int i = 0; i < parameterNames.length; i++) {
            for (Map.Entry entry : map.entrySet()) {
                if (parameterNames[i].equals(entry.getKey())) {
                    sortList.set(i, entry.getValue());
                }
            }
        }
        return sortList;
    }


    private boolean requestParamProcessor(Map<String, Object> map, String[] parameterNames, int i, Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            String value = annotation.value();
            boolean required = annotation.required();
            String defaultValue = annotation.defaultValue();
            if (StringUtils.isEmpty(value)) {
                //不会替换
                Object temp = map.get(value);
                if (StringUtils.isNotEmpty(defaultValue)) {
                    map.put(value, temp == null ? defaultValue : temp);
                }
                if (required && temp == null) {
                    throw new ArgsException(Exception.ARGS_EMPTY.getInfo() + value);
                }
                return true;
            } else {
                //替换变量名
                Object temp = map.get(value);
                if (StringUtils.isNotEmpty(defaultValue)) {
                    temp = temp == null ? defaultValue : temp;
                }
                map.put(parameterNames[i], temp);
                if (required && map.get(parameterNames[i]) == null) {
                    throw new ArgsException(Exception.ARGS_EMPTY.getInfo() + parameterNames[i]);
                }
                map.remove(value);
            }
        }
        return false;
    }

    private void requestBodyProcessor(Map<String, Object> map, Parameter parameter) {
        Object obj;
        if (parameter.isAnnotationPresent(RequestBody.class)) {
            RequestBody annotation = parameter.getAnnotation(RequestBody.class);
            String paramName = annotation.value();
            Class<?> type = parameter.getType();
            if (map.containsKey(paramName)) {
                String data = (String) map.get(paramName);
                obj = MvcBeanHandler.getBean(GsonMessgeConventer.class).read(data, null, type);
                matchArgsType(map, paramName, null, null, obj);
            }
        }
    }

    public void executeService(Object... obj) {
        Object[] objects = obj;
        Object controllerBean = MvcBeanHandler.getBean(controllerClass);
        this.result = ReflectionUtil.invockMethod(controllerBean, actionMethod, objects);
    }
}
