package com.fluxMVC.core.annotation.exception;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/20
 */
public class PathException extends RuntimeException {
    private String msg;
    private final static int FILE = 0;

    public PathException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
