package com.tv.seekers.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.tv.seekers.R;


import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by shoeb on 2/11/15.
 */
public class LoginActivity  extends Activity implements View.OnClickListener{

    @InjectView(R.id.email_et)
    EditText email_et;

    @InjectView(R.id.pwrd_et)
    EditText pswd_et;

    @InjectView(R.id.login_btn)
    Button login_btn;

    @InjectView(R.id.forgot_pswd_tv)
    TextView forgot_pswd_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginscreen);
        ButterKnife.inject(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.login_btn:


                break;
            case R.id.forgot_pswd_tv:


                break;
            default:
                break;
        }

    }
}
