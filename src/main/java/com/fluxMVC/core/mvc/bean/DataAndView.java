package com.fluxMVC.core.mvc.bean;

import java.util.HashMap;

/**
 * Title:    FluxMVC
 * Description:
 *
 *
 * @author kaibo
 * @version 1.0
 * @Ddate 2018/1/7
 */
public class DataAndView extends HashMap {
    /**
     * 返回模型数据如json
     */
    private String view;

    public DataAndView addModel(String key, Object value) {
        this.put(key, value);
        return this;
    }
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
