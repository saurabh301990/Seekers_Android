package com.tv.seekers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 4/11/15.
 */
public class ForgotPass extends Activity implements View.OnClickListener, View.OnTouchListener {

    @Bind(R.id.info_tv)
    TextView info_tv;

    @Bind(R.id.email_et)
    EditText email_et;

    @Bind(R.id.submit_btn)
    Button submit_btn;

    @Bind(R.id.backToLogin_tv)
    TextView backToLogin_tv;

    @Bind(R.id.main_rl)
    RelativeLayout main_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);
        ButterKnife.bind(this);

        setFont();
        setOnClick();
    }

    private void setOnClick() {
        submit_btn.setOnClickListener(this);
        backToLogin_tv.setOnClickListener(this);
        main_rl.setOnTouchListener(this);
    }

    private void setFont() {
        Constant.setFont(ForgotPass.this, info_tv, 0);
        Constant.setFont(ForgotPass.this, email_et, 0);
        Constant.setFont(ForgotPass.this, submit_btn, 0);
        Constant.setFont(ForgotPass.this, backToLogin_tv, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                Constant.hideKeyBoard(this);
                finish();
                break;
            case R.id.backToLogin_tv:
                finish();
                break;


            default:
                break;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Constant.hideKeyBoard(this);
        return false;
    }
}
