package com.tv.seekers.menu;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.tv.seekers.adapter.MyAreaAdapter;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.bean.MyAreasBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    private SharedPreferences sPref;

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
        callSaveLocationWS();
//        Constant.showToast("Data Saved", activity);
    }

    private boolean screenLeave = false;

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
        areaLatLngJSON = new JSONObject();
        areaLatLngArray = new JSONArray();
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!Is_MAP_Moveable) {
                    btn_draw_State.setText("Free Draw");
                    if (googleMap != null) {
                        googleMap.clear();
                    }
                    if (val.size() > 0) {
                        val.clear();
                    }
                    if (mLatLongList.size() > 0) {
                        mLatLongList.clear();
                    }

                    Is_MAP_Moveable = true;
                } else {
                    Is_MAP_Moveable = false;
                }
            }
        });
        _rightIcon.setVisibility(View.VISIBLE);
        _rightIcon.setImageResource(R.mipmap.save);
        _header.setText("Draw Area");
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
                            screenLeave = false;
//                            System.out.println("ACTION_DOWN");

//                            val.add(new LatLng(latitude, longitude));

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
//                            System.out.println("ACTION_MOVE");
                            val.add(new LatLng(latitude, longitude));
                            screenLeave = false;

                        case MotionEvent.ACTION_UP:

//                            System.out.println("ACTION_UP");
                            if (!screenLeave) {
                                screenLeave = true;
                            } else {
                                System.out.println("ACTION_UP ELSE CAse");
                                Is_MAP_Moveable = false; // to detect map is movable
                                btn_draw_State.setText("Clear");

                            }

                            // finger leaves the screen
//                            Is_MAP_Moveable = false; // to detect map is movable
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


    JSONObject areaLatLngJSON;
    JSONArray areaLatLngArray;


    private void callSaveLocationWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            //            Uri.Builder builder;
            JSONObject mainJsonObject = new JSONObject();
            JSONObject locJsonObject = new JSONObject();


            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                try {


                    for (int i = 0; i < mLatLongList.size(); i++) {
                        try {
                            HomeBean bean = mLatLongList.get(i);
                            areaLatLngJSON = new JSONObject();
                            areaLatLngJSON.put("x", Double.valueOf(bean.getPost_long()));
                            areaLatLngJSON.put("y", Double.valueOf(bean.getPost_lat()));
                            areaLatLngArray.put(areaLatLngJSON);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    locJsonObject.put("x", 0.0);
                    locJsonObject.put("y", 0.0);


                    mainJsonObject.put("loc", locJsonObject);
                    mainJsonObject.put("address", nameThisArea_et.getText().toString().trim());
                    mainJsonObject.put("locName", nameThisArea_et.getText().toString().trim());
                    mainJsonObject.put("userLocationType", "AREA");
                    mainJsonObject.put("locImage", "");
                    mainJsonObject.put("areaLatLng", areaLatLngArray);

                    System.out.println("Request of USER_SAVE_LOCATION : " + mainJsonObject.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

           /*     builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("loc_lat", String.valueOf(_lat))
                        .appendQueryParameter("loc_long", String.valueOf(_long))
                        .appendQueryParameter("loc_radius", "5")
                        .appendQueryParameter("loc_address", _address)
                        .appendQueryParameter("loc_img", finalimgUrl)
                        .appendQueryParameter("loc_name", _address);*/

//


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


//                        String query = builder.build().getEncodedQuery();
                        String query = mainJsonObject.toString();
                        //			String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.
                                getMethodUrl(WebServiceConstants.USER_SAVE_LOCATION));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setReadTimeout(200000);

                        urlConnection.setRequestMethod("POST");
                        urlConnection.setRequestProperty("Content-Type", "application/json");
                        urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
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
                        System.out.println("Response of USER_SAVE_LOCATION : " + _responseMain);


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
                            JSONObject dataJSON = jsonObject.getJSONObject("data");
                            if (dataJSON.has("areaLatLng")) {
                                JSONArray areaLatLngJSONARRAY = dataJSON.getJSONArray("areaLatLng");
                                if (areaLatLngJSONARRAY.length() > 0) {
                                    if (googleMap != null) {
                                        googleMap.clear();
                                    }
                                    // TODO: 17/2/16 Code to plot
                                    for (int i = 0; i < areaLatLngJSONARRAY.length(); i++) {
                                        JSONObject latLngJsonObj = areaLatLngJSONARRAY.getJSONObject(i);
                                        double longitude = latLngJsonObj.getDouble("x");
                                        double lat = latLngJsonObj.getDouble("y");
                                        LatLng ll = new LatLng(lat, longitude);
                                        BitmapDescriptor bitmapMarker = null;
                                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);

                                        googleMap.addMarker(new MarkerOptions().position(ll)

                                                .icon(bitmapMarker));

                                    }
                                }
                            }

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
                        }

                    } catch (Exception e) {

                        e.printStackTrace();

                        Constant.hideLoader();
                        Constant.showToast("Server Error ", getActivity());
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
        rectOptions.fillColor(Color.YELLOW);
        //        rectOptions.fillColor(Color.BLUE);

//        googleMap.addPolyline(new PolylineOptions().addAll(val).width(2.0f).color(Color.RED));
        polygon = googleMap.addPolygon(rectOptions);

    }


}