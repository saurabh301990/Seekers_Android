package com.tv.seekers.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.menu.MainActivity;


import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by shoeb on 2/11/15.
 */
public class LoginActivity  extends Activity implements View.OnClickListener, View.OnTouchListener{

    @Bind(R.id.email_et)
    EditText email_et;

    @Bind(R.id.pwrd_et)
    EditText pswd_et;

    @Bind(R.id.login_btn)
    Button login_btn;

    @Bind(R.id.forgot_pswd_tv)
    TextView forgot_pswd_tv;


    @Bind(R.id.main_rl)
    RelativeLayout main_rl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginscreen);
        ButterKnife.bind(this);

        main_rl.setOnTouchListener(this);
        login_btn.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login_btn:

                if (validData()){
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }


                break;
            case R.id.forgot_pswd_tv:


                break;
            default:
                break;
        }

    }

    private boolean validData() {

        String email = email_et.getText().toString().trim();
        String pswrd = pswd_et.getText().toString().trim();


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Constant.hideKeyBoard(LoginActivity.this);
        return false;
    }
}
