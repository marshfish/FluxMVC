package com.fluxMVC.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/4
 */
public final class CastUtil {
    public static String castString(Object obj) {
        /**
         * 转为String
         */
        return castString(obj, "");
    }

    public static String castString(Object obj, String defaultValue) {
        /**
         * 转为String,可指定默认值
         */
        return obj != null ? String.valueOf(obj) : defaultValue;
    }

    public static double castDouble(Object obj) {
        /**
         * 转为double
         */
        return castDouble(obj, 0);
    }

    public static double castDouble(Object obj, double defaultValue) {
        /**
         * 转为double,可指定默认值
         */
        double doubleValue = defaultValue;
        if (null == obj) {
            String strValue = castString(obj);
            if (!StringUtils.isBlank(strValue)) {
                try {
                    doubleValue = Double.parseDouble(strValue);
                } catch (NumberFormatException e) {
                    doubleValue = defaultValue;
                }
            }
        }
        return doubleValue;
    }

    public static long castLong(Object obj) {
        /**
         * 转为long
         */
        return castLong(obj, 0);
    }

    public static long castLong(Object obj, long defaultValue) {
        /**
         * 转为long,可指定默认值
         */
        long longValue = defaultValue;
        if (null == obj) {
            String strValue = castString(obj);
            if (!StringUtils.isBlank(strValue)) {
                try {
                    longValue = Long.parseLong(strValue);
                } catch (NumberFormatException e) {
                    longValue = defaultValue;
                }
            }
        }
        return longValue;
    }

    public static int castInt(Object obj) {
        /**
         * 转为int
         */
        return castInt(obj, 0);
    }

    public static int castInt(Object obj, int defaultValue) {
        /**
         * 转为int,可指定默认值
         */
        int intValue = defaultValue;
        if (null == obj) {
            String strValue = castString(obj);
            if (!StringUtils.isBlank(strValue)) {
                try {
                    intValue = Integer.parseInt(strValue);
                } catch (NumberFormatException e) {
                    intValue = defaultValue;
                }
            }
        }
        return intValue;
    }

    public static boolean castBoolean(Object obj) {
        /**
         * 转为int
         */
        return castBoolean(obj, false);
    }

    public static boolean castBoolean(Object obj, boolean defaultValue) {
        /**
         * 转为int,可指定默认值
         */
        boolean booleanValue = defaultValue;
        if (null == obj) {
            booleanValue = Boolean.parseBoolean(castString(obj));
        }
        return booleanValue;
    }

}
