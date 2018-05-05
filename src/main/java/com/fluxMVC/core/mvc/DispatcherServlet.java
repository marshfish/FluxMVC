package com.fluxMVC.core.mvc;


import com.fluxMVC.core.annotation.annotationEnum.Exception;
import com.fluxMVC.core.annotation.dataHandler.RequestMapping;
import com.fluxMVC.core.annotation.exception.ArgsException;
import com.fluxMVC.core.annotation.exception.PathException;
import com.fluxMVC.core.initialize.BeanInitialize;
import com.fluxMVC.core.initialize.Config;
import com.fluxMVC.core.initialize.ControllerMapping;
import com.fluxMVC.core.mvc.bean.DataAndView;
import com.fluxMVC.core.mvc.bean.EnvironmentContext;
import com.fluxMVC.core.mvc.bean.Param;
import com.fluxMVC.core.util.CodeUtil;
import com.fluxMVC.core.util.StreamUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
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
    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化bean factory
        BeanInitialize.init();
        ServletContext servletContext = config.getServletContext();
        //注册处理jsp的servlet
        ServletRegistration jspServlet = servletContext.getServletRegistration("jsp");
        jspServlet.addMapping(Config.getAppJspPath() + "*");
        //处理静态资源的默认servlet
        ServletRegistration defaultServlet = servletContext.getServletRegistration("default");
        defaultServlet.addMapping(Config.getAppStaticPath() + "*");
    }

    //TODO 设计模式重构
    //TODO 增加自定义注入IOC  BEAN
    //TODO bootstrap替代静态块
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取http 请求动作;获取http 访问路径
        String requestMethod = request.getMethod().toLowerCase();
        String requestPath = request.getPathInfo();
        String basePath = getBasePath(requestPath);
        if (basePath.equals(Exception.PATH_NOT_FOUND.getInfo())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, Exception.PATH_NOT_FOUND.getInfo());
        }
        String secondaryPath = requestPath.substring(basePath.length());
        List<Class> controllerMapping = ControllerMapping.getControllerMapping(basePath);
        List<Method> methodMapping = getMethodsMapping(controllerMapping);
        Class<?> controllerClass = null;
        Method actionMethod = null;
        for (Method method : methodMapping) {
            //TODO 这个路经验证不要整合进去
            boolean flag = vaildSecondaryPath(requestMethod, secondaryPath, method);
            if (flag) {
                controllerClass = method.getDeclaringClass();
                actionMethod = method;
                break;
            }
        }
        try {
            EnvironmentContext context = new EnvironmentContext(controllerClass, actionMethod, getParam(request), secondaryPath);
            Object result = doDispatch(context);
            resultWapper(request, response, result);
//            doDispatch(request, response, controllerClass, actionMethod, param, secondaryPath);
        } catch (ArgsException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMsg());
        } catch (PathException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMsg());
        }
    }

    private String getBasePath(String requestPath) {
        Set<String> mappingSet = ControllerMapping.getControllerMapping();
        Iterator<String> iterator = mappingSet.iterator();
        String basePath = Exception.PATH_NOT_FOUND.getInfo();
        while (iterator.hasNext()) {
            String next;
            if (requestPath.startsWith((next = iterator.next()))) {
                basePath = next;
            }
        }
        return basePath;
    }

    private List<Method> getMethodsMapping(List<Class> controllerMapping) {
        List<Method> methodMapping = Collections.emptyList();
        //拥有controller mapping
        if (controllerMapping.size() == 1) {
            methodMapping = ControllerMapping.getMethodMapping(controllerMapping.get(0));
        } else if (controllerMapping.size() > 1) {
            //未设置controller mapping
            for (Class cls : controllerMapping) {
                List<Method> singleMethodMapping = ControllerMapping.getMethodMapping(cls);
                methodMapping.addAll(singleMethodMapping);
            }
        }
        return methodMapping;
    }

    private Object doDispatch(EnvironmentContext context) throws IOException {
        if (!context.isInitialize()) {
            throw new PathException(Exception.PATH_NOT_FOUND.getInfo());
        }
        if (!ArrayUtils.isEmpty(context.getParameters())) {
            context.executeService(context.MatchParam().toArray());
        } else {
            context.executeService();
        }
        return context.serializeResultByResponseBody();
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


    public void resultWapper(HttpServletRequest request, HttpServletResponse response, Object result) throws IOException, ServletException {
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

    public static void main(String[] args) {
        System.out.println(11);
    }
}