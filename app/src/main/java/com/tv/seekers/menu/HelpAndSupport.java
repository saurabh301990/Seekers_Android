package com.tv.seekers.menu;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 5/11/15.
 */
public class HelpAndSupport extends Fragment {

    @Bind(R.id.web_view_helpsupport)
    WebView help_wv;

    final String mimeType = "text/html";
    final String encoding = "UTF-8";
    private String webviewtxt = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.helpandsupport,container,false);

        ButterKnife.bind(this, view);
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callHelpSupportWS();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }
        return view;
    }

    private void callHelpSupportWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {


                            URL url = new URL(WebServiceConstants.getMethodUrl(
                                    WebServiceConstants.HELP));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);


                            urlConnection.setRequestMethod("POST");
                            urlConnection.connect();


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
                            System.out.println("Response of HELP : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", getActivity());
                            }
                        });

                    }


                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", getActivity());
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

                            JSONObject jsonObject1 = jsonObject.getJSONObject("terms");
                            String id = jsonObject1.getString("id");
                            webviewtxt = jsonObject1.getString("help");
                            help_wv.loadDataWithBaseURL("", webviewtxt, mimeType, encoding, "");
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
}
