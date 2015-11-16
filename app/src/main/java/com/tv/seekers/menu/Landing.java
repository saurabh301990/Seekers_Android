package com.tv.seekers.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.LandingAdapter;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
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
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class Landing extends Fragment {

    LandingAdapter landingAdapter;

    ArrayList<LandingBean> listlanding = new ArrayList<LandingBean>();

    @Bind(R.id.etlandingsearchitem)
    EditText etsearch;

    @Bind(R.id.txtserachselctlo)
    TextView txtserachselectloc;

    @Bind(R.id.landinglist)
    ListView landinglist;

    SharedPreferences sPref;
    private String user_id = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.landing, container, false);
        ButterKnife.bind(this, view);
//        getdata();

        setfont();
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callsavedLocationWS();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }


        return view;
    }

  /*  public void getdata() {
        for (int i = 0; i <= 20; i++) {
            landingBean = new LandingBean();
            landingBean.setLandingplace("Hudson valley");
            listlanding.add(landingBean);
        }
    }*/

    public void setfont() {
        Constant.setFont(getActivity(), txtserachselectloc, 0);
        Constant.setFont(getActivity(), etsearch, 0);
    }


    private void callsavedLocationWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id);

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_LOC));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);


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
                            System.out.println("Response of Location Screen : " + _responseMain);


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

                            if (jsonObject.has("user_locations")) {

                                JSONArray user_locations = jsonObject.getJSONArray("user_locations");
                                if (user_locations.length() > 0) {
                                    for (int i = 0; i < user_locations.length(); i++) {
                                        JSONObject jsonobj = user_locations.getJSONObject(i);
                                        String userlocation = jsonobj.getString("loc_name");
                                        String userlat = jsonobj.getString("loc_lat");
                                        String userlong = jsonobj.getString("loc_long");
                                        String userradius = jsonobj.getString("loc_name");
                                        LandingBean landingBean = new LandingBean();
                                        landingBean.setLandingplace(userlocation);
                                        landingBean.set_long(userlong);
                                        landingBean.setLat(userlat);
                                        landingBean.setRadius(userradius);
                                        listlanding.add(landingBean);
                                    }
                                }


                                landingAdapter = new LandingAdapter(listlanding, getActivity());
                                landinglist.setAdapter(landingAdapter);
                            }

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
