package com.fluxMVC.core.mvc.bean;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/6
 */
public class Request {
    /**
     * 请求方法
     */
    private String reuquestMethod;
    /**
     * controller mapping路径
     */
    private String requestPath;
    /**
     * URI路径
     */
    private String uriPath;

    public Request(String requestMethod, String requestPath, String uriPath) {
        this.reuquestMethod = requestMethod;
        this.requestPath = requestPath;
        this.uriPath = uriPath;
    }

    public String getReuquestMethod() {
        return reuquestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getUriPath() {
        return uriPath;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
