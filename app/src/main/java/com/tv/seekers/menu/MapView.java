package com.tv.seekers.menu;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tv.seekers.R;
import com.tv.seekers.gpsservice.GPSTracker;

import butterknife.Bind;
import butterknife.ButterKnife;

import butterknife.OnClick;

/**
 * Created by shoeb on 3/11/15.
 */
public class MapView extends Fragment{

    //map Related
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    Marker marker;
    private double latitude = 0.0;
    private double longitude= 0.0;
    LatLng _latLong;
    GPSTracker gps;
    Circle mapCircle;

    @Bind(R.id.two_miles_btn)
    Button two_miles_btn;

    @OnClick(R.id.two_miles_btn)
    public void two_miles_btn(View view) {
        activeMilesBtn(2);
    }

    @Bind(R.id.five_miles_btn)
    Button five_miles_btn;

    @OnClick(R.id.five_miles_btn)
    public void five_miles_btn(View view) {
        if (view.getId() == R.id.five_miles_btn) {
            activeMilesBtn(5);

        }
    }

    @Bind(R.id.ten_miles_btn)
    Button ten_miles_btn;

    @OnClick(R.id.ten_miles_btn)
    public void ten_miles_btn(View view) {
        if (view.getId() == R.id.ten_miles_btn) {

            activeMilesBtn(10);
        }
    }

    @Bind(R.id.twenty_miles_btn)
    Button twenty_miles_btn;

    @OnClick(R.id.twenty_miles_btn)
    public void twenty_miles_btn(View view) {
        if (view.getId() == R.id.twenty_miles_btn) {
            activeMilesBtn(20);

        }
    }

    @Bind(R.id.map_btn)
    Button map_btn;

    @OnClick(R.id.map_btn)
    public void map_btn(View view) {
        if (view.getId() == R.id.map_btn) {
            map_layout.setVisibility(View.GONE);
            map_layout.setVisibility(View.VISIBLE);
            map_btn.setBackgroundColor(Color.WHITE);
            list_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));

        }
    }

    @Bind(R.id.list_btn)
    Button list_btn;

    @OnClick(R.id.list_btn)
    public void list_btn(View view) {
        if (view.getId() == R.id.list_btn) {
            map_layout.setVisibility(View.VISIBLE);
            map_layout.setVisibility(View.GONE);
            list_btn.setBackgroundColor(Color.WHITE);
            map_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));

        }
    }


    @Bind(R.id.map_layout)
    LinearLayout map_layout;



    @Bind(R.id.list_layout)
    RelativeLayout list_layout;



    @Override public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_view_screen,container,false);
        ButterKnife.bind(this, view);
        return view;
    }



    private void init() {
        gps = new GPSTracker(getActivity());


        if (gps.canGetLocation()){
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            if (!String.valueOf(latitude).equalsIgnoreCase("0.0")&&
                    !String.valueOf(longitude).equalsIgnoreCase("0.0")){

                try {
                    // Loading map
                    initilizeMap();

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

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapView, fragment).commit();
        }


    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = fragment.getMap();
//            googleMap.setMyLocationEnabled(false);

            try {

                mapWithZooming(12);
                addCircleToMap(2);


            } catch (Exception e) {
                System.out.println("exception " + e);
            }

        }
    }

    private void mapWithZooming(int zoom) {
        _latLong = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder().target(_latLong)
                .zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(_latLong).icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }


    private void addCircleToMap(int miles) {

        // circle settings
        double radiusM = miles * 1609.34;// your radius in meters
        int radiusInt = (int)radiusM;


        CircleOptions circleOptions = new CircleOptions().center(_latLong) // set center
                .radius(radiusInt) // set radius in meters
                .strokeColor(0x10000000).strokeWidth(5).
                        fillColor(0x55fe9f9f);
        // Fill color of the circle
        // 0x represents, this is an hexadecimal code
        // 55 represents percentage of transparency. For 100% transparency, specify 00.
        // For 0% transparency ( ie, opaque ) , specify ff
        // The remaining 6 characters(00ff00) specify the fill color

        if(mapCircle!=null){
            mapCircle.remove();
        }
        mapCircle=  googleMap.addCircle(circleOptions);

      /*  googleMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(_latLong, radiusInt * 2, radiusInt * 2).
                transparency(0.6f));*/
    }




    private void activeMilesBtn(int  activeBtnId) {

        switch (activeBtnId){
            case 2:
                two_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                addCircleToMap(2);
                mapWithZooming(12);
                break;
            case 5:
                five_miles_btn.setBackgroundColor(Color.WHITE);
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                addCircleToMap(5);
                mapWithZooming(11);
                break;
            case 10:
                ten_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                addCircleToMap(10);
                mapWithZooming(10);
                break;

            case 20:
                twenty_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                addCircleToMap(20);
                mapWithZooming(9);
                break;
            default:
                break;

        }

    }
}
