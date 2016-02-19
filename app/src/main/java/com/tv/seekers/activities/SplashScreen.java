package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.tv.seekers.R;
import com.tv.seekers.login.LoginActivity;
import com.tv.seekers.menu.MainActivity;

/**
 * Created by shoeb on 2/11/15.
 */
public class SplashScreen extends Activity {

    protected int SPLASH_TIME_OUT = 3000;
    private SharedPreferences sPref;
    private boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

//        ErrorReporter.getInstance().Init(SplashScreen.this);
        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);

        isLogin = sPref.getBoolean("ISLOGIN", false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (isLogin) {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }


            }
        }, SPLASH_TIME_OUT);
    }
}
