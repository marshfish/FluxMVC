package com.fluxMVC.core.util;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.InputStream;
import java.lang.reflect.Modifier;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/18
 */
public final class JavaassistUtil {

    public static String[] getParameterNames(Class clazz, String methodName) {
        ClassPool pool = ClassPool.getDefault();
        CtMethod cm = null;
        LocalVariableAttribute attr = null;
        String[] paramNames = null;
        //手动添加ClassPath，否则web应用中的class不会被装载进classpool
        pool.insertClassPath(new ClassClassPath(clazz));
        try {
            CtClass cc = pool.get(clazz.getName());
            cm = cc.getDeclaredMethod(methodName);
            // 使用javaassist的反射方法获取方法的参数名
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            if (attr == null) {
                throw new RuntimeException("get LocalVariableAttribute is null,please check is class has been loaded");
            }
            paramNames = new String[0];
            paramNames = new String[cm.getParameterTypes().length];
        } catch (NotFoundException e) {
            e.printStackTrace();
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        return paramNames;
    }
}
