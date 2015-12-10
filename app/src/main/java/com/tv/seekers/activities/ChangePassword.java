package com.tv.seekers.activities;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tv.seekers.R;
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


    /*Header*/
    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;

    @Bind(R.id.main_layout)
    LinearLayout main_layout;


    private String currentPass = "";
    private String newPass = "";
    private String confirmPass = "";
    private String user_id = "";
    private SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_pswd);
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");


    }

    private void setOnClick() {
        tgl_menu.setOnClickListener(this);
        user_img_iv.setOnClickListener(this);
        chnge_pswd_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Constant.hideKeyBoard(ChangePassword.this);
                return false;
            }
        });
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
                if (validData()) {
                    if (NetworkAvailablity.checkNetworkStatus(ChangePassword.this)) {
                        callChangePass();
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), ChangePassword.this);
                    }
                }
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


    private void callChangePass() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(ChangePassword.this);

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("new_password", newPass)
                        .appendQueryParameter("cur_password", currentPass);
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(ChangePassword.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.CHANGE_PASSWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setReadTimeout(5000);

                            urlConnection.setRequestMethod("POST");
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
                            System.out.println("Response of ChangePassword : " + _responseMain);


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
                                Constant.showToast("Server Error ", ChangePassword.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", ChangePassword.this);
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {
                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try{
                        JSONObject _JsonObject = new JSONObject(_responseMain);
                        int status = _JsonObject.getInt("status");
                        String message = _JsonObject.getString("message");
                        Constant.showToast(message, ChangePassword.this);
                        if (status==1){
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", ChangePassword.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", ChangePassword.this);
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

    private boolean validData() {
        boolean isValid = false;

        currentPass = current_pass_et.getText().toString().trim();
        newPass = new_pass_et.getText().toString().trim();
        confirmPass = confirm_pass_et.getText().toString().trim();

        if (currentPass == null || currentPass.equalsIgnoreCase("")) {

            Constant.showToast(getResources().getString(R.string.enterCurrentPass), ChangePassword.this);
            isValid = false;
        } else if (newPass == null || newPass.equalsIgnoreCase("")) {
            Constant.showToast(getResources().getString(R.string.enterNewPass), ChangePassword.this);
            isValid = false;
        } else if (confirmPass == null || confirmPass.equalsIgnoreCase("")) {
            Constant.showToast(getResources().getString(R.string.enterConfirmPass), ChangePassword.this);
            isValid = false;
        } else if (!newPass.equals(confirmPass)) {
            Constant.showToast(getResources().getString(R.string.passwordNotMatch), ChangePassword.this);
            isValid = false;
        } else {
            isValid = true;
        }

        return isValid;
    }
}
