package com.fluxMVC.core.mvc.bean;

import com.fluxMVC.core.annotation.annotationEnum.ResultData;
import com.fluxMVC.core.annotation.dataHandler.ResponseBody;
import com.fluxMVC.core.initialize.ControllerMapping;
import com.fluxMVC.core.mvc.dataHandler.GsonMessgeConventer;
import com.fluxMVC.core.mvc.handler.BeanContainer;
import com.fluxMVC.core.util.JavaassistUtil;
import com.fluxMVC.core.util.ReflectionUtil;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Optional;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/5
 */
public abstract class EnvironmentContext {
    protected Class<?> controllerClass;
    protected Method actionMethod;
    protected Param param;
    protected String secondaryPath;
    protected String[] parameterNames;
    protected Parameter[] parameters;
    protected Object[] paramList;
    private Object result;
    private boolean initialize = true;

    public boolean isInitialize() {
        return initialize;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public EnvironmentContext(Class<?> controllerClass, Method actionMethod, Param param, String secondaryPath) {
        this.controllerClass = controllerClass;
        this.actionMethod = actionMethod;
        this.param = param;
        this.secondaryPath = secondaryPath;
        this.parameters = Optional.ofNullable(actionMethod).map(Executable::getParameters).orElse(null);
        this.parameterNames = getParametersByCache();
    }

    private String[] getParametersByCache() {
        Map<String, String[]> flag;
        if ((flag = ControllerMapping.PARAMTETER_CACHE.get(controllerClass)) != null) {
            return flag.get(actionMethod.getName());
        }
        return JavaassistUtil.getParameterNames(controllerClass, actionMethod.getName());
    }


    public abstract void doProcess();

    /**
     * 获取匹配的参数list
     */
    public void paramProcess() {
        doProcess();
    }

    public Object resultProcess() throws IOException {
        Object serialized = "";
        if (actionMethod.isAnnotationPresent(ResponseBody.class)) {
            if (result instanceof String) {
                return result;
            }
            ResponseBody annotation = actionMethod.getAnnotation(ResponseBody.class);
            ResultData rule = annotation.rule();
            if (rule == ResultData.JSON) {
                serialized = BeanContainer.getBean(GsonMessgeConventer.class).write(result);
            } else if (rule == ResultData.XML) {
                //TODO XML序列化
            }
            return serialized;
        } else {
            return result;
        }
    }

    public void invokeProcess() {
        if (!ArrayUtils.isEmpty(parameters)) {
            invoke(paramList);
        } else {
            invoke();
        }
    }

    private void invoke(Object... obj) {
        Object controllerBean = BeanContainer.getBean(controllerClass);
        this.result = ReflectionUtil.invockMethod(controllerBean, actionMethod, obj);
    }
}
