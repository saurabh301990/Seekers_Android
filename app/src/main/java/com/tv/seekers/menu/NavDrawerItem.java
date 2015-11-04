package com.tv.seekers.menu;

/**
 * Created by Ravi on 29/07/15.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private int sideImg;


    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title, int sideImg) {
        this.showNotify = showNotify;
        this.title = title;
        this.sideImg = sideImg;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSideImg() {
        return sideImg;
    }

    public void setSideImg(int sideImg) {
        this.sideImg = sideImg;
    }
}
