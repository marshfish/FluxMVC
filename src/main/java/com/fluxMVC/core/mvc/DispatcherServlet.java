package com.fluxMVC.core.mvc;


import com.fluxMVC.core.annotation.annotationEnum.Exception;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.annotation.exception.ArgsException;
import com.fluxMVC.core.annotation.exception.PathException;
import com.fluxMVC.core.initialize.BeanInitialize;
import com.fluxMVC.core.initialize.Config;
import com.fluxMVC.core.initialize.MappingInitialize;
import com.fluxMVC.core.mvc.bean.DataAndView;
import com.fluxMVC.core.mvc.bean.EnvironmentContext;
import com.fluxMVC.core.mvc.bean.Param;
import com.fluxMVC.core.mvc.bean.ParamProcessor;
import com.fluxMVC.core.util.CodeUtil;
import com.fluxMVC.core.util.StreamUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SingleThreadModel;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
@WebServlet(urlPatterns = "/*", loadOnStartup = 0)
public class DispatcherServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化bean factory
        try {
            BeanInitialize.init();
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
        ServletContext servletContext = config.getServletContext();
        //注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(Config.getAppJspPath() + "*");
        //处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(Config.getAppStaticPath() + "*");
    }

    //TODO 统一异常处理
    //TODO 增加自定义注入IOC  BEAN
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取http 请求动作;获取http 访问路径
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();
        //获取一级URI路径
        String basePath = getBasePath(requestPath);
        if (basePath == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Exception.PATH_NOT_FOUND.getInfo());
        }
        //二级URI路径
        String secondaryPath = requestPath.substring(basePath.length());
        //获取映射controller list
        List<Method> methodMapping = null;
        Class<?> controllerClass = null;
        Method actionMethod = null;
        try {
            methodMapping = controllerAdapter(basePath);
        } catch (PathException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMsg());
        }
        //匹配映射方法，若不存在匹配，controller method置为空
        for (Method method : methodMapping) {
            //二级URI校验，@pathvariable判断
            boolean flag = vaildSecondaryPath(requestMethod, secondaryPath, method);
            if (flag) {
                controllerClass = method.getDeclaringClass();
                actionMethod = method;
                break;
            }
        }
        try {
            Object result = this.doDispatch(controllerClass, actionMethod, getParam(request), secondaryPath);
            this.viewResolve(request, response, result);
        } catch (ArgsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMsg());
        } catch (PathException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMsg());
        }
    }

    private List<Method> controllerAdapter(String basePath) throws PathException {
        List<Class> controllerMapping = MappingInitialize.getControllerMapping(basePath);
        return getMethodsMapping(controllerMapping);
    }

    private String getBasePath(String requestPath) {
        Set<String> mappingSet = MappingInitialize.getControllerMapping();
        Iterator<String> iterator = mappingSet.iterator();
        String basePath = null;
        while (iterator.hasNext()) {
            String next;
            if (requestPath.startsWith((next = iterator.next()))) {
                basePath = next;
            }
        }
        return basePath;
    }

    private List<Method> getMethodsMapping(List<Class> controllerMapping) {
        if (controllerMapping == null || controllerMapping.size() == 0) {
            throw new PathException(Exception.MAPPING_NOT_FOUND.getInfo());
        }
        List<Method> methodMapping = new ArrayList<>();
        //拥有controller mapping
        if (controllerMapping.size() == 1) {
            methodMapping = MappingInitialize.getMethodMapping(controllerMapping.get(0));
        } else {
            //未设置controller mapping
            for (Class cls : controllerMapping) {
                List<Method> singleMethodMapping = MappingInitialize.getMethodMapping(cls);
                methodMapping.addAll(singleMethodMapping);
            }
        }
        return methodMapping;
    }

    private Object doDispatch(Class<?> controllerClass, Method actionMethod, Param param, String secondaryPath) throws IOException {
        if (controllerClass == null || actionMethod == null) {
            throw new PathException(Exception.PATH_NOT_FOUND.getInfo());
        }
        EnvironmentContext context = new ParamProcessor(controllerClass, actionMethod, param, secondaryPath);
        context.paramProcess();
        context.invokeProcess();
        return context.resultProcess();
    }


    private Param getParam(HttpServletRequest request) throws IOException {
        //获取get请求参数，并注册到Param中
        Map<String, Object> paramMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parmaName = parameterNames.nextElement();
            String paramValue = request.getParameter(parmaName);
            paramMap.put(parmaName, paramValue);
        }
        //获取post请求发来的参数
        String body = CodeUtil.decodeURL(StreamUtil.getString(request.getInputStream()));
        if (body.contains("Content-Disposition: form-data")) {
            postHandler(paramMap, body);
            return new Param(paramMap);
        }
        if (StringUtils.isNotEmpty(body)) {
            String[] splitString = StringUtils.split(body, "&");
            if (!ArrayUtils.isEmpty(splitString)) {
                for (String param : splitString) {
                    String[] array = StringUtils.split(param, "=");
                    if (!ArrayUtils.isEmpty(array) && array.length == 2) {
                        String paramName = array[0];
                        String paramValue = array[1];
                        paramMap.put(paramName, paramValue);
                    }
                }
            }
        }
        return new Param(paramMap);
    }

    private void postHandler(Map<String, Object> paramMap, String body) {
        String[] split = body.split(";");
        for (int i = 1; i < split.length; i++) {
            String var = split[i];
            String name = var.substring(7, var.indexOf("\"", var.indexOf("\"") + 1));
            String value = var.substring(8 + name.length(), var.indexOf("----------------------------"));
            paramMap.put(name, value);
        }
    }


    private void viewResolve(HttpServletRequest request, HttpServletResponse response, Object result) throws IOException, ServletException {
        if (result instanceof DataAndView) {
            //返回ModelAndView
            DataAndView dataAndView = (DataAndView) result;
            String view = dataAndView.getView();
            if (StringUtils.isNotEmpty(view)) {
                if (view.startsWith("/")) {
                    response.sendRedirect(request.getContextPath() + view);
                } else {
                    Set<Map.Entry<String, Object>> set = dataAndView.entrySet();
                    for (Map.Entry entry : set) {
                        if (set.size() == 0) {
                            break;
                        }
                        request.setAttribute(entry.getKey().toString(), entry.getValue());
                    }
                    request.getRequestDispatcher(Config.getAppJspPath() + view).forward(request, response);
                }
            }
        } else if (result instanceof String) {
            print((String) result, response.getWriter());
        }
    }


    private void print(String json, PrintWriter writer) {
        writer.write(json);
        writer.flush();
        writer.close();
    }


    public boolean vaildSecondaryPath(String requestMethod, String requestPath, Method method) {
        RequestMapping annotation = method.getAnnotation(RequestMapping.class);
        String value = annotation.value();
        String httpMethod = annotation.method();
        //@pathvariable校验
        String vaildPath = requestPath;
        if (value.contains("{") && value.contains("}")) {
            value = value.substring(0, value.indexOf("{"));
            //若路径包含该URI
            if (requestPath.contains(value)) {
                //若匹配该URI,校验通过
                //TODO 不支持中间参数
                if (requestPath.indexOf(value) == 0 && !requestPath.substring(value.length()).contains("/")) {
                    vaildPath = value;
                } else {
                    return false;
                }
            }
        }
        return value.equals(vaildPath) && httpMethod.equals(requestMethod);
    }
}
