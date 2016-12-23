package com.qlhui.thoughtlight.utils;

/**
 * Created by Administrator on 2016/4/1.
 */
public class Bean {
    private String title;
    private int imgId;
    private int ggvalue;
    public Bean() {
    }

    public Bean(String title, int imgId,int ggvalue) {
        this.title = title;
        this.imgId = imgId;
        this.imgId = ggvalue;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
    
    public int getggvalue() {
        return ggvalue;
    }

    public void setggvalue(int ggvalue) {
        this.ggvalue = ggvalue;
    }
}
