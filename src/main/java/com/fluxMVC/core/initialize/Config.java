package com.fluxMVC.core.initialize;

import com.fluxMVC.core.util.PropertiesUtil;
import com.fluxMVC.core.util.ConfigConstant;

import java.util.Properties;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/4
 */
public final class Config {
    private static final Properties CONFIG_PROPS = PropertiesUtil.loadProps(ConfigConstant.CONFIG_FILE);
    /**
     * 获取jdbc驱动
     * @return
     */
    public static String getJDBCDriver(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_DRIVER);
    }
    /**
     * 获取jdbc url
     */
    public static String getJDBCURL(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.JDBC_URL);
    }
    /**
     * 获取jdbc username
     */
    public static String getJDBCUsername(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.USERNAME);
    }
    /**
     * 获取jdbc password
     */
    public static String getJDBCPassword(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.PASSWORD);
    }
    /**
     * 获取应用基础包名
     */
    public static String getAppBasePackage(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.APP_BASE_PACKAGE);
    }
    /**
     * 获取jsp路径
     */
    public static String getAppJspPath(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.APP_JSP_PATH,"/WEB-INF/view/");
    }
    /**
     * 获取静态资源路径
     */
    public static String getAppStaticPath(){
        return PropertiesUtil.getString(CONFIG_PROPS,ConfigConstant.APP_STATIC_PATH,"/static/");
    }


}
