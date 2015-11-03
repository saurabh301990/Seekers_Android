package com.tv.seekers.constant;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by shoeb on 2/11/15.
 */
public class Constant {


    ///////// key board hide
    public static void hideKeyBoard(Activity context) {
        View focusedView = context.getCurrentFocus();
        //	 Toast.makeText(context,"not hide", 1).show();
        if (focusedView != null) {
            //       Toast.makeText(context,"hide", 1).show();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getWindow().getCurrentFocus().getWindowToken(), 0);
        }
    }


}
