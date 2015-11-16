package com.tv.seekers.bean;

/**
 * Created by dheerendra on 4/11/15.
 */
public class LandingBean  {

    String landingplace;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    String lat;
    String _long;
    String radius;

    public String getLandingplace() {
        return landingplace;
    }

    public void setLandingplace(String landingplace) {
        this.landingplace = landingplace;
    }
}
