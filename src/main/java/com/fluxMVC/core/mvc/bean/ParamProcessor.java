package com.fluxMVC.core.mvc.bean;

import com.fluxMVC.core.annotation.annotationEnum.Exception;
import com.fluxMVC.core.annotation.annotationEnum.TypeName;
import com.fluxMVC.core.annotation.beanComponent.Controller;
import com.fluxMVC.core.annotation.dataHandler.PathVariable;
import com.fluxMVC.core.annotation.dataHandler.RequestBody;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.annotation.dataHandler.RequestParam;
import com.fluxMVC.core.annotation.exception.ArgsException;
import com.fluxMVC.core.mvc.dataHandler.GsonMessgeConventer;
import com.fluxMVC.core.mvc.handler.BeanContainer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/28
 */
public class ParamProcessor extends EnvironmentContext {
    public ParamProcessor(Class<?> controllerClass, Method actionMethod, Param param, String secondaryPath) {
        super(controllerClass, actionMethod, param, secondaryPath);
    }

    @Override
    public void doProcess() {
        packageArgsType(parameters, parameterNames);
        for (int i = 0; i < parameters.length; i++) {
            requestParamProcessor(parameterNames[i], parameters[i]);
            requestBodyProcessor(parameters[i]);
            pathVariableProcessor(parameterNames, parameters[i]);
        }
        paramList = sortParameters().toArray();
    }


    /**
     * 包装参数类型
     * 除非使用java8的javac -parameters参数，否则无法直接获取运行时参数方法名
     * 通过javaassist获取顺序的参数名列表，与参数一一对应
     *
     * @param parameters     参数列表
     * @param parameterNames 参数名称列表
     */
    private void packageArgsType(Parameter[] parameters, String[] parameterNames) {
        Map<String, Object> map = param.getMap();
        for (int i = 0; i < parameters.length; i++) {
            String name = parameterNames[i];
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getKey().equals(name)) {
                    String parameterName = parameters[i].getType().getTypeName();
                    matchArgsType(map, name, String.valueOf(entry.getValue()), parameterName);
                }
            }
        }
    }

    /**
     * 根据实际类型转换参数
     *
     * @param map       参数map
     * @param paramName 参数名
     * @param oldVar    需要包装的参数
     * @param type      参数类型
     */
    protected void matchArgsType(Map<String, Object> map, String paramName, String oldVar, String type) {
        String lowerCase = type.toLowerCase();
        if (lowerCase.equals(TypeName.INTEGER.getName()) || lowerCase.equals(TypeName.INT.getName())) {
            map.put(paramName, Integer.valueOf(oldVar));
        } else if (lowerCase.equals(TypeName.LONGABLE.getName()) || lowerCase.equals(TypeName.LONG.getName())) {
            map.put(paramName, Long.valueOf(oldVar));
        } else if (lowerCase.equals(TypeName.BOOLEANABLE.getName()) || lowerCase.equals(TypeName.BOOLEAN.getName())) {
            map.put(paramName, Boolean.valueOf(oldVar));
        } else if (lowerCase.equals(TypeName.FLOATABLE.getName()) || lowerCase.equals(TypeName.FLOAT.getName())) {
            map.put(paramName, Float.valueOf(oldVar));
        } else {
            map.put(paramName, oldVar);
        }
    }

    /**
     * 方法参数数量匹配
     */
    private List<Object> sortParameters() {
        Map<String, Object> map = param.getMap();
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

    private void requestParamProcessor(String parameterName, Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestParam.class)) {
            RequestParam annotation = parameter.getAnnotation(RequestParam.class);
            String value = annotation.value();
            boolean required = annotation.required();
            String defaultValue = annotation.defaultValue();
            Map<String, Object> map = param.getMap();
            if (StringUtils.isEmpty(value)) {
                //不会替换
                Object temp = map.get(value);
                if (StringUtils.isNotEmpty(defaultValue)) {
                    map.put(value, temp == null ? defaultValue : temp);
                }
                if (required && temp == null) {
                    throw new ArgsException(Exception.ARGS_EMPTY.getInfo() + value);
                }
            } else {
                //替换变量名
                Object temp = map.get(value);
                if (StringUtils.isNotEmpty(defaultValue)) {
                    temp = temp == null ? defaultValue : temp;
                }
                map.put(parameterName, temp);
                if (required && map.get(parameterName) == null) {
                    throw new ArgsException(Exception.ARGS_EMPTY.getInfo() + parameterName);
                }
                map.remove(value);
            }
        }
    }

    private void requestBodyProcessor(Parameter parameter) {
        if (parameter.isAnnotationPresent(RequestBody.class)) {
            Object obj;
            Map<String, Object> map = param.getMap();
            RequestBody annotation = parameter.getAnnotation(RequestBody.class);
            String paramName = annotation.value();
            Class<?> type = parameter.getType();
            if (map.containsKey(paramName)) {
                String data = (String) map.get(paramName);
                obj = BeanContainer.getBean(GsonMessgeConventer.class).read(data, null, type);
                if (obj != null) {
                    map.put(paramName, obj);
                }
            }
        }
    }

    private void pathVariableProcessor(String[] parameterNames, Parameter parameter) {
        if (!controllerClass.getAnnotation(Controller.class).path().equals("") && parameter.isAnnotationPresent(PathVariable.class)) {
            Map<String, Object> map = param.getMap();
            String value = actionMethod.getAnnotation(RequestMapping.class).value();
            if (value.contains("{") && value.contains("}")) {
                String paramName = value.substring(value.indexOf("{") + 1, value.indexOf("}"));
                if (isExistParam(parameterNames, paramName)) {
                    //TODO 不支持中间参数
                    String newTemp = secondaryPath.substring(value.indexOf("{"));
                    String type = parameter.getType().getTypeName();
                    matchArgsType(map, paramName, newTemp, type);
                } else {
                    throw new ArgsException(Exception.EXPRESSION_EMPTY.getInfo());
                }
            } else {
                throw new ArgsException(Exception.EXPRESSION_ERROR.getInfo());
            }
        }
    }

    private boolean isExistParam(String[] parameterNames, String substring) {
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals(substring)) {
                return true;
            }
        }
        return false;
    }

}
