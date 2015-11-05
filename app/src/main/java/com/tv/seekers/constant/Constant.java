package com.tv.seekers.constant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.seekers.R;

import java.util.regex.Pattern;

/**
 * Created by shoeb on 2/11/15.
 */
public class Constant {
    public static Dialog dialog;

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

    //Email Check
    public static boolean checkEmail(String email) {
        String expression = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*"
                + "+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
        Pattern emailPattern = Pattern.compile(expression);
        return emailPattern.matcher(email).matches();
    }


    public static void showToast(String msg , Context context){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }


    public static void setFont(Context context, View view, int type){
        Typeface tf;
        if(type == 0){
            tf = Typeface.createFromAsset(context.getAssets(),"agaramondpro_regular.otf");
        }else{
            tf = Typeface.createFromAsset(context.getAssets(),"agaramondpro_bold.otf");
        }
        if(view instanceof TextView){
            ((TextView)view).setTypeface(tf);
        }else if(view instanceof EditText){
            ((EditText)view).setTypeface(tf);
        }else if(view instanceof Button){
            ((Button)view).setTypeface(tf);
        }
    }

    public static void hideLoader(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public static void showLoader(Context c){
        Dialog builder = new Dialog(c,R.style.mydialogstyle);
        builder.setContentView(R.layout.loader);
        WebView view = (WebView) builder.findViewById(R.id.wb);
        view.setBackgroundColor(Color.TRANSPARENT);
        view.loadUrl("file:///android_asset/loader.gif");
        builder.setCancelable(false);
        builder.show();
    }
}
