package com.fluxMVC.core.annotation.exception;

/**
 * Title:    FluxMVC
 * Description:
 *
 * @author Kaibo
 * @date 2018/4/19
 */
public class ArgsException extends BusinessException {
    private String msg;
    private final static int FILE = 0;

    public ArgsException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
