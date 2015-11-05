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
public class ChangePassword extends Activity implements View.OnClickListener {

    @Bind(R.id.user_img_iv)
    ImageView user_img_iv;


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


    @Bind(R.id.cancel_btn)
    Button cancel_btn;


    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pswd);
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();


    }

    private void setOnClick() {
        tgl_menu.setOnClickListener(this);
        user_img_iv.setOnClickListener(this);
        chnge_pswd_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);
    }

    private void setData() {
        hdr_title.setText(getResources().getString(R.string.changePswrdText));
        tgl_menu.setImageResource(R.mipmap.back);
        hdr_fltr.setVisibility(View.GONE);
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
        Constant.setFont(ChangePassword.this, hdr_title, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_img_iv:
                break;
            case R.id.chnge_pswd_btn:
                break;
            case R.id.cancel_btn:
                finish();
                break;
            case R.id.tgl_menu:
                finish();
                break;


            default:
                break;

        }
    }
}
