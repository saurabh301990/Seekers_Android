package com.tv.seekers.bean;

import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by shoeb on 21/11/15.
 */
public class MyAreasBean {


    private String loc_name = "";
    private String loc_add = "";
    private String img_url = "";
    private String _lat = "";
    private String _long= "";
    private String type= "";

    public ArrayList<LatLng> getAreaLatLng() {
        return areaLatLng;
    }

    public void setAreaLatLng(ArrayList<LatLng> areaLatLng) {
        this.areaLatLng = areaLatLng;
    }

    private ArrayList<LatLng> areaLatLng = new ArrayList<LatLng>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    private boolean isSelected = false;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id= "";

    public String get_lat() {
        return _lat;
    }

    public void set_lat(String _lat) {
        this._lat = _lat;
    }

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
    }

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public String getLoc_add() {
        return loc_add;
    }

    public void setLoc_add(String loc_add) {
        this.loc_add = loc_add;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

}
