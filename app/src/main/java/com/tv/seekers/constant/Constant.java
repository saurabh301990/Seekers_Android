package com.tv.seekers.constant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.IntentCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.seekers.R;
import com.tv.seekers.login.LoginActivity;

import java.util.regex.Pattern;

/**
 * Created by shoeb on 2/11/15.
 */
public class Constant {

    public static final String YOUTUBELINK = "https://www.youtube.com/watch?v=";
    public static final String Cookie = "Cookie";
    public static final String notificationStatus = "notificationStatus";
    public static final String YOUTUBE_API_KEY = "AIzaSyBqq3WcNChgrNRMrN96oPpGQy3NFg94b60";
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


    public static void showToast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void setFont(Context context, View view, int type) {
        Typeface tf;
        if (type == 0) {
            tf = Typeface.createFromAsset(context.getAssets(), "agaramondpro_regular.otf");
        } else {
            tf = Typeface.createFromAsset(context.getAssets(), "agaramondpro_bold.otf");
        }
        if (view instanceof TextView) {
            ((TextView) view).setTypeface(tf);
        } else if (view instanceof EditText) {
            ((EditText) view).setTypeface(tf);
        } else if (view instanceof Button) {
            ((Button) view).setTypeface(tf);
        }
    }

    public static void hideLoader() {
        try {
            if (builder != null) {
                builder.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Dialog builder;

    public static void showLoader(Context c) {

        try {
            builder = new Dialog(c, R.style.mydialogstyle);
            builder.setContentView(R.layout.loader);
            ImageView iw = (ImageView) builder.findViewById(R.id.runImg);
            iw.setBackgroundResource(R.drawable.spin_animation);
            AnimationDrawable frameAnimation = (AnimationDrawable) iw.getBackground();
            frameAnimation.start();
            builder.setCancelable(false);
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static SharedPreferences sPref;
    public static SharedPreferences.Editor editor;

    public static void alertForLogin(final Activity context) {

        sPref = context.getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        final Dialog dialog = new Dialog(context, R.style.mydialogstyleSaveLocation);
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog_session_exp);

        dialog.setCancelable(false);
        dialog.show();

        ImageView ok_btn = (ImageView) dialog.findViewById(R.id.ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if this button is clicked, close
                // current activity
                dialog.cancel();
                editor.clear();
                editor.commit();
                Intent i = new Intent(context, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
                context.finish();
            }
        });


    }


}
