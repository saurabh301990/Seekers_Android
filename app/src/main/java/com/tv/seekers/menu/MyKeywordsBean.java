package com.tv.seekers.menu;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeywordsBean {
    private String _title;
    private boolean _tglState;


    public MyKeywordsBean(String _title, boolean _tglState) {
        super();
        this._title = _title;
        this._tglState = _tglState;
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


}
