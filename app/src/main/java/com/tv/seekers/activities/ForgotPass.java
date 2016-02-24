package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

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


    SharedPreferences sPref;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_screen);
        ButterKnife.bind(this);
//        ErrorReporter.getInstance().Init(ForgotPass.this);

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
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

    private void callForgotPassword() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(ForgotPass.this);

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(ForgotPass.this)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.FORGOT_PASSWORD) + "?email=" + email);
                            urlConnection = (HttpURLConnection) url.openConnection();

                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of FORGOT_PASSWORD : " + _responseMain);

                            } else {
                                Log.v("FORGOT_PASSWORD", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", ForgotPass.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", ForgotPass.this);
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {
                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

                        JSONObject _jJsonObject = new JSONObject(_responseMain);
                        if (_jJsonObject.has("status")) {
                            int status = _jJsonObject.getInt("status");
                            if (status == 1) {
                                Constant.showToast("Please check your mail box. ", ForgotPass.this);
                                finish();
                            } else if (status == -2) {
                                Constant.showToast("Invalid email!", ForgotPass.this);
                            } else {
                                Constant.showToast("Server Error ", ForgotPass.this);
                            }
//                            String message = _jJsonObject.getString("message");
//                            Constant.showToast(message, ForgotPass.this);
                        } else {
                            Constant.showToast("Server Error ", ForgotPass.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", ForgotPass.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", ForgotPass.this);
                    Constant.hideLoader();
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            _Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
        } else {
            _Task.execute((String[]) null);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.hideKeyBoard(ForgotPass.this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_btn:
                Constant.hideKeyBoard(this);
                email = email_et.getText().toString().trim();
                if (email == null || email.equalsIgnoreCase("")) {
                    Constant.showToast(getResources().getString(R.string.pleaseEnterEmail), ForgotPass.this);
                } else if (!Constant.checkEmail(email)) {
                    Constant.showToast(getResources().getString(R.string.pleaseEnterValidEmail), ForgotPass.this);
                } else {
                    if (NetworkAvailablity.checkNetworkStatus(ForgotPass.this)) {
                        callForgotPassword();
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), ForgotPass.this);
                    }
                }
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

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
}
