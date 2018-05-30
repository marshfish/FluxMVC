package com.fluxMVC.core.annotation.exception;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/5/30
 */
public class ServerInnerException extends RuntimeException {

    private String msg;
    private final static int FILE = 0;

    public ServerInnerException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
