package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tv.seekers.R;
import com.tv.seekers.login.LoginActivity;

/**
 * Created by shoeb on 2/11/15.
 */
public class SplashScreen extends Activity {

    protected int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
