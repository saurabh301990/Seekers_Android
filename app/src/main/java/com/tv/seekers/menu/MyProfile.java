package com.tv.seekers.menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.tv.seekers.gpsservice.GPSTracker;

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
    Button current_loc_btn;

    @OnClick(R.id.current_loc_btn)
    public void current_loc_btn(View view) {

    }

    @Bind(R.id.change_pswrd_btn)
    Button change_pswrd_btn;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_profile_screen, container, false);

        ButterKnife.bind(this, view);
        setFont();
        return view;
    }

    private void setFont() {
        Constant.setFont(getActivity(), nameInfo_tv, 0);
        Constant.setFont(getActivity(), name_et, 0);
        Constant.setFont(getActivity(), emailInfo_tv, 0);
        Constant.setFont(getActivity(), email_et, 0);
        Constant.setFont(getActivity(), userNameInfo_tv, 0);
        Constant.setFont(getActivity(), username_et, 0);
        Constant.setFont(getActivity(), change_pswrd_btn, 0);
        Constant.setFont(getActivity(), current_loc_btn, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
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
//            googleMap.setMyLocationEnabled(false);

                        try {


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


                        } catch (Exception e) {
                            System.out.println("exception " + e);
                        }

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
}
