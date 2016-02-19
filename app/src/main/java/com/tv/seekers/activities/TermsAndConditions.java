package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shoeb on 16/12/15.
 */
public class TermsAndConditions extends Activity {

    @Bind(R.id.web_vew_terms)
    WebView web_vew_terms;
    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    private String webviewtxt = "";

    /*Header*/
    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @OnClick(R.id.tgl_menu)
    public void tgl_menu(View view) {
        finish();
    }

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;

    private SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.terms_and_cond);

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);

//        ErrorReporter.getInstance().Init(TermsAndConditions.this);
        ButterKnife.bind(this);
        hdr_title.setText(getResources().getString(R.string.termAndConditionsTExt));
        Constant.setFont(this, hdr_title, 0);
        tgl_menu.setImageResource(R.mipmap.back);
        hdr_fltr.setVisibility(View.GONE);
        if (NetworkAvailablity.checkNetworkStatus(TermsAndConditions.this)) {
            calltermsWS();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), TermsAndConditions.this);
        }
    }

    private void calltermsWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(TermsAndConditions.this);


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(TermsAndConditions.this)) {

                    try {
                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.TERMS));
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of TERMS : " + _responseMain);

                            } else {
                                Log.v("TERMS", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Constant.showToast("Server Error ", TermsAndConditions.this);
                                }
                            });
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
                                Constant.showToast("Server Error ", TermsAndConditions.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", TermsAndConditions.this);
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

                        JSONObject jsonObject = new JSONObject(_responseMain);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {

                            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                            String id = jsonObject1.getString("id");
                            webviewtxt = jsonObject1.getString("text");
                            web_vew_terms.loadDataWithBaseURL("", webviewtxt, mimeType, encoding, "");
                        }else if (status == 0) {
                            Constant.showToast("Server Error    ", TermsAndConditions.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(TermsAndConditions.this);
                        }


                    } catch (Exception e) {

                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {

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
