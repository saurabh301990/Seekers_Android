package com.tv.seekers.bean;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeywordsBean {
    private String _title = "";;
    private boolean _tglState;
    private String _tglID = "";;

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    private long createdOn ;



    public MyKeywordsBean(String _title, boolean _tglState, String _tglID) {
        super();
        this._title = _title;
        this._tglState = _tglState;
        this._tglID = _tglID;
    }


    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public boolean get_tglState() {
        return _tglState;
    }

    public void set_tglState(boolean _tglState) {
        this._tglState = _tglState;
    }

    public String get_tglID() {
        return _tglID;
    }

    public void set_tglID(String _title) {
        this._tglID = _tglID;
    }

}
