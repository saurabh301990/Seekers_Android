package com.tv.seekers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shoeb on 4/11/15.
 */
public class ChangePassword extends Activity {

    @Bind(R.id.user_img_iv)
    ImageView user_img_iv;

    @OnClick(R.id.user_img_iv)
    public void user_img_iv(View view) {

    }

    @Bind(R.id.current_pass_info_tv)
    TextView current_pass_info_tv;

    @Bind(R.id.current_pass_et)
    EditText current_pass_et;

    @Bind(R.id.new_pass_info_tv)
    TextView new_pass_info_tv;

    @Bind(R.id.new_pass_et)
    EditText new_pass_et;

    @Bind(R.id.confirm_pass_info_tv)
    TextView confirm_pass_info_tv;

    @Bind(R.id.confirm_pass_et)
    EditText confirm_pass_et;

    @Bind(R.id.chnge_pswd_btn)
    Button chnge_pswd_btn;

    @OnClick(R.id.chnge_pswd_btn)
    public void chnge_pswd_btn(View view) {

    }


    @Bind(R.id.cancel_btn)
    Button cancel_btn;

    @OnClick(R.id.cancel_btn)
    public void cancel_btn(View view) {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pswd);
        ButterKnife.bind(this);
        setFont();


    }

    private void setFont() {
        Constant.setFont(ChangePassword.this, current_pass_info_tv, 0);
        Constant.setFont(ChangePassword.this, current_pass_et, 0);
        Constant.setFont(ChangePassword.this, new_pass_info_tv, 0);
        Constant.setFont(ChangePassword.this, new_pass_et, 0);
        Constant.setFont(ChangePassword.this, confirm_pass_info_tv, 0);
        Constant.setFont(ChangePassword.this, confirm_pass_et, 0);
        Constant.setFont(ChangePassword.this, chnge_pswd_btn, 0);
        Constant.setFont(ChangePassword.this, cancel_btn, 0);
    }
}
