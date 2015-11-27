package com.tv.seekers.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tv.seekers.R;
import com.tv.seekers.activities.ForgotPass;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.menu.MainActivity;

import com.tv.seekers.utils.NetworkAvailablity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by shoeb on 2/11/15.
 */
public class LoginActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

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

    private String username = "";
    private String pswrd = "";
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginscreen);
        ButterKnife.bind(this);

        main_rl.setOnTouchListener(this);
        login_btn.setOnClickListener(this);
        forgot_pswd_tv.setOnClickListener(this);

        Constant.setFont(LoginActivity.this, forgot_pswd_tv, 0);
        Constant.setFont(LoginActivity.this, login_btn, 0);
        Constant.setFont(LoginActivity.this, email_et, 0);
        Constant.setFont(LoginActivity.this, pswd_et, 0);

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();


    }

    private void callLoginWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(LoginActivity.this);

                builder = new Uri.Builder()
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", pswrd);
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.LOGIN));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);


                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(5000);
                            urlConnection.connect();

                            //Write
                            OutputStream outputStream = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            writer.write(query);
                            writer.close();
                            outputStream.close();

                            //Read
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                            String line = null;
                            StringBuilder sb = new StringBuilder();

                            while ((line = bufferedReader.readLine()) != null) {
                                //System.out.println("Uploading............");
                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();
                            System.out.println("Response of LOGIN : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", LoginActivity.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", LoginActivity.this);
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
                            String message = _jJsonObject.getString("message");

                            if (status == 1) { // Success Login

                                if (_jJsonObject.has("user_details")) {
                                    JSONObject _jSonSub = _jJsonObject.getJSONObject("user_details");
                                    String id = _jSonSub.getString("id");
                                    String firstname = _jSonSub.getString("firstname");
                                    String lastname = _jSonSub.getString("lastname");
                                    String image = _jSonSub.getString("image");

                                    editor.putString("id", id);
                                    editor.putString("firstname", firstname);
                                    editor.putString("lastname", lastname);
                                    editor.putString("image", image);
                                    editor.putBoolean("ISLOGIN", true);
                                    editor.commit();


                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                            } else {
                                Constant.showToast(message, LoginActivity.this);
                            }
                        } else {
                            Constant.showToast("Server Error ", LoginActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", LoginActivity.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", LoginActivity.this);
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
        Constant.hideKeyBoard(LoginActivity.this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_btn:
                Constant.hideKeyBoard(LoginActivity.this);
                if (validData()) {

                    if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) {
                        callLoginWS();
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), LoginActivity.this);
                    }


                }


                break;
            case R.id.forgot_pswd_tv:
                startActivity(new Intent(LoginActivity.this, ForgotPass.class));


                break;
            default:
                break;
        }

    }

    private boolean validData() {

        username = email_et.getText().toString().trim();
        pswrd = pswd_et.getText().toString().trim();
        boolean isValid = false;
        if (username == null || username.equalsIgnoreCase("")) {
            isValid = false;
            Constant.showToast(getResources().getString(R.string.enterUsernameText), LoginActivity.this);
        }/* else if (!Constant.checkEmail(email)){
            isValid = false;
        }*/ else if (pswrd == null || pswrd.equalsIgnoreCase("")) {
            isValid = false;
            Constant.showToast(getResources().getString(R.string.enterPswrdText), LoginActivity.this);
        } else if (pswrd.length() < 8 || pswrd.length() > 32) {
            Constant.showToast(getResources().getString(R.string.pswrdMustContainsText), LoginActivity.this);
            isValid = false;
        } else {
            isValid = true;
        }

        return isValid;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Constant.hideKeyBoard(LoginActivity.this);
        return false;
    }


}
