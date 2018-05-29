package com.fluxMVC.core.mvc.handler;


import com.fluxMVC.core.annotation.beanComponent.Component;
import com.fluxMVC.core.annotation.beanComponent.Configuration;
import com.fluxMVC.core.annotation.beanComponent.Controller;
import com.fluxMVC.core.util.ClassUtil;
import com.fluxMVC.core.annotation.beanComponent.Repository;
import com.fluxMVC.core.annotation.beanComponent.Service;
import com.fluxMVC.core.initialize.Config;
import com.fluxMVC.core.util.ReflectionUtil;


import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public final class ClassesHandler {
    /**
     * 定义类集合(用于存放所加载的类)
     */
    private static Set<Class<?>> CLASS_SET;


    /**
     * 获取应用包名下的所有类
     */
    public static Set<Class<?>> getClassSet() {
        return CLASS_SET;
    }

    /**
     * 获取应用报名下的所有service类
     */
    public static Set<Class<?>> getServiceClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Service.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下的所有controller类
     */
    public static Set<Class<?>> getControllerClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Controller.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下的所有Repository类
     */
    public static Set<Class<?>> getRepositoryClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Repository.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下的所有Component类
     */
    public static Set<Class<?>> getComponentClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Component.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下的所有Configuration类
     */
    public static Set<Class<?>> getConfigurationClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(Configuration.class)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下的所有bean类,（包括controller，service等等）
     */
    public static Set<Class<?>> getBeanClassSet() {
        Set<Class<?>> classSet = new HashSet<>();
        classSet.addAll(getServiceClassSet());
        classSet.addAll(getControllerClassSet());
        classSet.addAll(getRepositoryClassSet());
        classSet.addAll(getComponentClassSet());
        classSet.addAll(getConfigurationClassSet());
        return classSet;
    }

    /**
     * 获取子类/实现类
     */
    public static Set<Class<?>> getClassSetBySuper(Class<?> superClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            //isAssignableFrom判断是否为其父类/接口
            if (superClass.isAssignableFrom(cls) && !superClass.equals(cls)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }

    /**
     * 获取应用报名下带有某注解的类
     */
    public static Set<Class<?>> getClassSetByAnnotation(Class<? extends Annotation> annotationClass) {
        Set<Class<?>> classSet = new HashSet<>();
        for (Class<?> cls : CLASS_SET) {
            if (cls.isAnnotationPresent(annotationClass)) {
                classSet.add(cls);
            }
        }
        return classSet;
    }


    public void init() {
        String basePackage = Config.getAppBasePackage();
        CLASS_SET = ClassUtil.getClassSet(basePackage);
        MvcBeanHandler handler = (MvcBeanHandler) ReflectionUtil.newInstance(MvcBeanHandler.class);
        handler.init();
    }
}
