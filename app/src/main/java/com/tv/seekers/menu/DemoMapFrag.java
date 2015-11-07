package com.tv.seekers.menu;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;

import java.util.HashSet;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 6/11/15.
 */
public class DemoMapFrag extends Fragment {
    FrameLayout fram_map;
    Button btn_draw_State;
    Boolean Is_MAP_Moveable = false; // to detect map is movable
    double latitude;
    double longitude;
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    HashSet<LatLng> val;

    @Override
    public void onResume() {
        super.onResume();
        initilizeMap();
    }

    @Bind(R.id.nameThisArea_et)
    EditText nameThisArea_et;

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = fragment.getMap();
//            googleMap.setMyLocationEnabled(false);

            try {

//                mapWithZooming(12);


            } catch (Exception e) {
                System.out.println("exception " + e);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_map, container, false);

        ButterKnife.bind(this, view);
        Constant.setFont(getActivity(), nameThisArea_et, 0);
        fram_map = (FrameLayout) view.findViewById(R.id.fram_map);
        btn_draw_State = (Button) view.findViewById(R.id.btn_draw_State);
        val = new HashSet<LatLng>();
        // Button will change Map movable state
        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (Is_MAP_Moveable != true) {
                    Is_MAP_Moveable = true;
                } else {
                    Is_MAP_Moveable = false;
                }
            }
        });

//        Touch Click of Frame Layout and with the help of the do some task
        fram_map.setOnTouchListener(new View.OnTouchListener() {
            @Override

            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();

                int x_co = Math.round(x);
                int y_co = Math.round(y);

//                Projection projection = googleMap.getProjection();
                Point x_y_points = new Point(x_co, y_co);

                LatLng latLng = googleMap.getProjection().fromScreenLocation(x_y_points);
                latitude = latLng.latitude;

                longitude = latLng.longitude;

                int eventaction = event.getAction();
                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        // finger touches the screen

                        val.add(new LatLng(latitude, longitude));

                    case MotionEvent.ACTION_MOVE:
                        // finger moves on the screen
                        val.add(new LatLng(latitude, longitude));

                    case MotionEvent.ACTION_UP:
                        // finger leaves the screen
                        Draw_Map();
                        break;
                }

                if (Is_MAP_Moveable == true) {
                    return true;

                } else {
                    return false;
                }
            }
        });
        return view;
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
        rectOptions.strokeWidth(7);
        rectOptions.fillColor(ContextCompat.getColor(getActivity(), R.color.map_circle_color));
        polygon = googleMap.addPolygon(rectOptions);
    }
}
