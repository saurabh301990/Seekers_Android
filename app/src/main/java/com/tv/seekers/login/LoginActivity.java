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
import com.tv.seekers.activities.TermsAndConditions;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.menu.MainActivity;

import com.tv.seekers.utils.NetworkAvailablity;


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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


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

    @Bind(R.id.term_tv)
    TextView term_tv;

    @OnClick(R.id.term_tv)
    public void term_tv(View view) {
        startActivity(new Intent(LoginActivity.this, TermsAndConditions.class));
    }

    private String username = "";
    private String pswrd = "";
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loginscreen);

//        ErrorReporter.getInstance().Init(LoginActivity.this);
        ButterKnife.bind(this);

        main_rl.setOnTouchListener(this);
        login_btn.setOnClickListener(this);
        forgot_pswd_tv.setOnClickListener(this);

        Constant.setFont(LoginActivity.this, forgot_pswd_tv, 0);
        Constant.setFont(LoginActivity.this, login_btn, 0);
        Constant.setFont(LoginActivity.this, email_et, 0);
        Constant.setFont(LoginActivity.this, pswd_et, 0);
        Constant.setFont(LoginActivity.this, term_tv, 0);

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();


    }

    private void callLoginWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;
            JSONObject mJsonObject = new JSONObject();

            @Override
            protected void onPreExecute() {


                Constant.showLoader(LoginActivity.this);

                try {
                    mJsonObject.put("username",username);
                    mJsonObject.put("password",pswrd);
                }catch (Exception e){
                    e.printStackTrace();
                }

               /* builder = new Uri.Builder()
                        .appendQueryParameter("username", username)
                        .appendQueryParameter("password", pswrd);*/
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(LoginActivity.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();
                            System.out.println("query FOr Login : " + query);
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.LOGIN));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);

                            urlConnection.setRequestProperty("Content-Type", "application/json");

                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(30000);
                            urlConnection.connect();


                            //Write
                            OutputStream outputStream = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            writer.write(query);
                            writer.close();
                            outputStream.close();

                            if (urlConnection.getResponseCode()==200){
                                String responseHeader  =  urlConnection.getHeaderField("Set-Cookie");

                                if (responseHeader!=null&&!responseHeader.equalsIgnoreCase("")){
                                    String response[] = responseHeader.split(";");
                                    if (response.length>0){
                                        responseHeader = response[0];
                                        System.out.println("Response Header : " +responseHeader);
                                        editor.putString(Constant.Cookie,responseHeader);
                                        editor.commit();
                                    }


                                }
                            }

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
//                            String message = _jJsonObject.getString("message");

                            if (status == 1) { // Success Login

                                if (_jJsonObject.has("data")) {
                                    JSONObject _jSonSub = _jJsonObject.getJSONObject("data");
                                    String id = _jSonSub.getString("id");
                                    String firstname = _jSonSub.getString("firstName");
                                    String lastname = _jSonSub.getString("lastName");
                                    String profilePic = _jSonSub.getString("profilePic");

                                    editor.putString("id", id);
                                    editor.putString("firstName", firstname);
                                    editor.putString("lastName", lastname);
                                    if (_jJsonObject.has("profilePic")){
                                        JSONObject mJsonObjectPic = _jJsonObject.getJSONObject("profilePic");
                                        String mMedPic = mJsonObjectPic.getString("medium");
                                        editor.putString("profilePic", mMedPic);
                                    }

                                    editor.putBoolean("ISLOGIN", true);
                                    editor.commit();


                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                            } else {
                                Constant.showToast("Incorrect Username or Password.", LoginActivity.this);
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
