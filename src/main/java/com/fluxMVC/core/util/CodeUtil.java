package com.fluxMVC.core.util;

import org.apache.log4j.Logger;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public final class CodeUtil {
    private static final Logger logger = Logger.getLogger(CodeUtil.class);

    /**
     * 编码URL
     */
    public static String encodeURL(String source) {
        String target;
        try {
            target = URLEncoder.encode(source, "utf-8");
        } catch (Exception e) {
            logger.error("encode url fail", e);
            throw new RuntimeException();
        }
        return target;
    }

    /**
     * URL解码
     */
    public static String decodeURL(String source) {
        String target;
        try {
            target = URLDecoder.decode(source, "utf-8");
        } catch (Exception e) {
            logger.error("decode url fail,", e);
            throw new RuntimeException();
        }
        return target;
    }
}
