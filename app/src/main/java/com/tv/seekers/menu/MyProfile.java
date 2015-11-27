package com.tv.seekers.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tv.seekers.R;
import com.tv.seekers.activities.ChangePassword;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
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
public class MyProfile extends Fragment {

    @Bind(R.id.user_img_iv)
    ImageView user_img_iv;

    @OnClick(R.id.user_img_iv)
    public void img_click(View view) {

    }

    @Bind(R.id.nameInfo_tv)
    TextView nameInfo_tv;

    @Bind(R.id.name_et)
    EditText name_et;

    @Bind(R.id.emailInfo_tv)
    TextView emailInfo_tv;

    @Bind(R.id.email_et)
    EditText email_et;

    @Bind(R.id.userNameInfo_tv)
    TextView userNameInfo_tv;

    @Bind(R.id.username_et)
    EditText username_et;

    @Bind(R.id.current_loc_btn)
    ImageView current_loc_btn;

    @OnClick(R.id.current_loc_btn)
    public void current_loc_btn(View view) {

    }

    @Bind(R.id.main_layout)
    LinearLayout main_layout;
    @Bind(R.id.change_pswrd_btn)
    ImageView change_pswrd_btn;

    @OnClick(R.id.change_pswrd_btn)
    public void change_pswrd_btn(View view) {
        startActivity(new Intent(getActivity(), ChangePassword.class));
    }

    @Bind(R.id.map_view)
    RelativeLayout map_view;

    //map Related
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    Marker marker;
    private double latitude = 0.0;
    private double longitude = 0.0;
    LatLng _latLong;
    GPSTracker gps;

    private String user_id = "";
    private SharedPreferences sPref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_screen, container, false);

        ButterKnife.bind(this, view);
        setFont();


        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetMyProfile();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Constant.hideKeyBoard(getActivity());
                return false;
            }
        });
        return view;
    }

    private void setFont() {
        Constant.setFont(getActivity(), nameInfo_tv, 0);
        Constant.setFont(getActivity(), name_et, 0);
        Constant.setFont(getActivity(), emailInfo_tv, 0);
        Constant.setFont(getActivity(), email_et, 0);
        Constant.setFont(getActivity(), userNameInfo_tv, 0);
        Constant.setFont(getActivity(), username_et, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    private void callGetMyProfile() {
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
                        String query = builder.build().getEncodedQuery();
                        //String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_PROFILE));
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
                        System.out.println("Response of My Profile : " + _responseMain);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
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
                        JSONObject _JsonObject = new JSONObject(_responseMain);
                        int status = _JsonObject.getInt("status");
                        if (status == 1) {
                            JSONObject user_details = _JsonObject.getJSONObject("user_details");
                            String id = user_details.getString("id");
                            String username = user_details.getString("username");
                            String firstname = user_details.getString("firstname");
                            String lastname = user_details.getString("lastname");
                            String fullname = user_details.getString("fullname");
                            String email = user_details.getString("email");
                            String password = user_details.getString("password");
                            String role_type = user_details.getString("role_type");
                            String gender = user_details.getString("gender");
                            String mobile_no = user_details.getString("mobile_no");
                            String lat = user_details.getString("lat");
                            String _long = user_details.getString("long");
                            String address = user_details.getString("address");
                            String image = user_details.getString("image");
                            image = WebServiceConstants.IMAGE_URL + image;

                            email_et.setText(email);
                            name_et.setText(fullname);
                            username_et.setText(username);

                            if (_long != null && !_long.equalsIgnoreCase("")
                                    && lat != null && !lat.equalsIgnoreCase("")) {
                                latitude = Double.parseDouble(lat);
                                longitude = Double.parseDouble(_long);
                                showMap();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", getActivity());
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", getActivity());
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_view);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_view, fragment).commit();
        }
    }

    private void initMap() {
        gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (!String.valueOf(latitude).equalsIgnoreCase("0.0") &&
                    !String.valueOf(longitude).equalsIgnoreCase("0.0")) {

                try {
                    // Loading map
                    if (googleMap == null) {
                        googleMap = fragment.getMap();
                        //googleMap.setMyLocationEnabled(false);
                        showMap();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                gps.showSettingsAlert();
            }
        } else {
            gps.showSettingsAlert();
        }
    }

    private void showMap() {
        _latLong = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder().target(_latLong)
                .zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(_latLong).icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }
}
