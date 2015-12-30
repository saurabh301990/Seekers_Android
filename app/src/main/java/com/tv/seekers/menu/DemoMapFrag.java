package com.tv.seekers.menu;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.tv.seekers.R;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.gpsservice.GPSTracker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 6/11/15.
 */
public class DemoMapFrag extends Fragment {
    FrameLayout fram_map;
    Button btn_draw_State;
    Boolean Is_MAP_Moveable = false; // to detect map is movable
    private double latitude = 0.0;
    private double longitude = 0.0;
    LatLng _latLong;
    GPSTracker gps;
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    HashSet<LatLng> val;
    private ArrayList<HomeBean> mLatLongList = new ArrayList<HomeBean>();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constant.hideKeyBoard(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Bind(R.id.nameThisArea_et)
    EditText nameThisArea_et;


    ImageView _rightIcon;
    TextView _header;

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = fragment.getMap();
//            googleMap.setMyLocationEnabled(false);

            gps = new GPSTracker(getActivity());
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();
                if (!String.valueOf(latitude).equalsIgnoreCase("0.0") &&
                        !String.valueOf(longitude).equalsIgnoreCase("0.0")) {

                    _latLong = new LatLng(latitude, longitude);
                    cameraPosition = new CameraPosition.Builder().target(_latLong)
                            .zoom(13).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    gps.showSettingsAlert();
                }
            } else {
                gps.showSettingsAlert();
            }


        }
    }

   /* private void mapWithZooming(int zoom) {
        _latLong = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder().target(_latLong)
                .zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        *//* if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(_latLong).icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));*//**//*
    }*//*
    }*/

    public void saveData(Activity activity) {
        Constant.showToast("Data Saved", activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_map, container, false);

        ButterKnife.bind(this, view);
        Constant.setFont(getActivity(), nameThisArea_et, 0);
        fram_map = (FrameLayout) view.findViewById(R.id.fram_map);
        btn_draw_State = (Button) view.findViewById(R.id.btn_draw_State);
        _rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        _header = (TextView) getActivity().findViewById(R.id.hdr_title);
        val = new HashSet<LatLng>();
        // Button will change Map movable state
        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!Is_MAP_Moveable) {
                    Is_MAP_Moveable = true;
                } else {
                    Is_MAP_Moveable = false;
                }
            }
        });
        _rightIcon.setVisibility(View.VISIBLE);
        _rightIcon.setImageResource(R.mipmap.save);
        _header.setText("Draw");
        _rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(getActivity());

            }
        });
//        Touch Click of Frame Layout and with the help of the do some task
        fram_map.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {

                if (Is_MAP_Moveable) {
                    float x = event.getX();
                    float y = event.getY();

                    int x_co = Math.round(x);
                    int y_co = Math.round(y);

//                Projection projection = googleMap.getProjection();
                    Point x_y_points = new Point(x_co, y_co);

                    LatLng latLng = googleMap.getProjection().fromScreenLocation(x_y_points);
                    latitude = latLng.latitude;

                    longitude = latLng.longitude;
                    HomeBean bean = new HomeBean();
                    bean.setPost_lat(String.valueOf(latitude));
                    bean.setPost_long(String.valueOf(longitude));
                    bean.setSource_id("3");
                    mLatLongList.add(bean);


                    System.out.println("LatLng : " + latitude + " : " + longitude);

                    LatLng point = new LatLng(latitude, longitude);

                    int eventaction = event.getAction();
                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            // finger touches the screen

//                            System.out.println("ACTION_DOWN");

//                            val.add(new LatLng(latitude, longitude));

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
//                            System.out.println("ACTION_MOVE");
                            val.add(new LatLng(latitude, longitude));

                        case MotionEvent.ACTION_UP:

                            System.out.println("ACTION_UP");

                            // finger leaves the screen

                            Draw_Map();
                            break;
                        default:
                            break;
                    }

                    if (Is_MAP_Moveable) {
                        Log.e("DRAW on MAP : ", "LatLng ArrayList Size : " + mLatLongList.size());
                        return true;

                    } else {
                        return false;
                    }


                } else {
                    return false;
                }


            }
        });

        MainActivity.drawerFragment.setDrawerState(true);

        if (mLatLongList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (int i = 0; i < mLatLongList.size(); i++) {
                HomeBean bean = mLatLongList.get(i);
                LatLng latLng = new LatLng(Double.parseDouble(bean.getPost_lat()), Double.parseDouble(bean.getPost_long()));
                builder.include(latLng);

            }

            LatLngBounds bounds = builder.build();
            bounds.getCenter();
            Log.e("DRAW on MAP :  ", "Centre of LatLngs = " + bounds.getCenter());
        }


        // TODO: 30/12/15 Getting the way points  between 2 latLngs.
        LatLng from = new LatLng(22.719569, 75.857726);
        LatLng to = new LatLng(22.727009, 75.880990);
        LatLng wayPoints = allWayPoints(from, to);
        Log.e("DEMO MAP : ", "WAYPOINTS  :" + wayPoints);
        return view;
    }

    private LatLng allWayPoints(LatLng from, LatLng to) {

        return SphericalUtil.interpolate(from, to, 2);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, fragment).commit();
        }


    }

    Polygon polygon;

    public void Draw_Map() {
        PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(ContextCompat.getColor(getActivity(), R.color.map_circle_color));
        rectOptions.strokeWidth(20);
//        rectOptions.fillColor(Color.BLUE);
        polygon = googleMap.addPolygon(rectOptions);

    }


}
