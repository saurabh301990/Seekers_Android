package com.tv.seekers.menu;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.google.android.gms.common.api.Status;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.tv.seekers.R;
import com.tv.seekers.activities.FilterActivity;
import com.tv.seekers.activities.PostDetailsTextImg;
import com.tv.seekers.adapter.CustomWindAdapter;
import com.tv.seekers.adapter.HomeListAdapter;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.date.DateTime;
import com.tv.seekers.gpsservice.GPSTracker;
import com.tv.seekers.utils.NetworkAvailablity;
import com.tv.seekers.utils.XListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

import butterknife.OnClick;

/**
 * Created by shoeb on 3/11/15.
 */
public class MapView extends Fragment
        implements XListView.IXListViewListener,
        GoogleMap.OnInfoWindowClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //map Related
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    Marker marker;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private ArrayList<HomeBean> _mainList = new ArrayList<HomeBean>();
    private ArrayList<HomeBean> arrayTemplist = new ArrayList<HomeBean>();


    LatLng _latLong;
    GPSTracker gps;
    Circle mapCircle;

    @Bind(R.id.two_miles_btn)
    Button two_miles_btn;

    @OnClick(R.id.two_miles_btn)
    public void two_miles_btn(View view) {
        if (arrayTemplist.size() > 0) {
            arrayTemplist.clear();
        }

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            _page_number = 1;
            activeMilesBtn(2);
            _radiusForWS = "2";
            Constant.showLoader(getActivity());
            callGetAllPostsWS(_radiusForWS);
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }
    }

    @Bind(R.id.five_miles_btn)
    Button five_miles_btn;

    @OnClick(R.id.five_miles_btn)
    public void five_miles_btn(View view) {
        if (view.getId() == R.id.five_miles_btn) {

            if (arrayTemplist.size() > 0) {
                arrayTemplist.clear();
            }
            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _page_number = 1;
                activeMilesBtn(5);
                _radiusForWS = "5";
                Constant.showLoader(getActivity());
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }

        }
    }

    @Bind(R.id.ten_miles_btn)
    Button ten_miles_btn;
    private String _radiusForWS = "2";

    @OnClick(R.id.ten_miles_btn)
    public void ten_miles_btn(View view) {
        if (view.getId() == R.id.ten_miles_btn) {
            if (arrayTemplist.size() > 0) {
                arrayTemplist.clear();
            }

            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _page_number = 1;
                activeMilesBtn(10);
                _radiusForWS = "10";
                Constant.showLoader(getActivity());
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }
        }
    }

    @Bind(R.id.twenty_miles_btn)
    Button twenty_miles_btn;

    @OnClick(R.id.twenty_miles_btn)
    public void twenty_miles_btn(View view) {
        if (view.getId() == R.id.twenty_miles_btn) {
            if (arrayTemplist.size() > 0) {
                arrayTemplist.clear();
            }
            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _page_number = 1;
                activeMilesBtn(20);
                _radiusForWS = "20";
                Constant.showLoader(getActivity());
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }


        }
    }

    @Bind(R.id.map_btn)
    Button map_btn;

    @Bind(R.id.search_et)
    EditText search_et;

    @Bind(R.id.et_layout)
    RelativeLayout et_layout;

    @Bind(R.id.no_post_layout)
    RelativeLayout no_post_layout;

    @Bind(R.id.sorry_tv)
    TextView sorry_tv;

    @Bind(R.id.no_post_tv)
    TextView no_post_tv;


    @OnClick(R.id.map_btn)
    public void map_btn(View view) {
        if (view.getId() == R.id.map_btn) {

            map_view.setVisibility(View.VISIBLE);
            map_btn.setBackgroundColor(Color.WHITE);
            list_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
            header.setText("Map");
            list_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
            map_btn.setTextColor(Color.BLACK);
            list_layout.setVisibility(View.GONE);
            no_post_layout.setVisibility(View.GONE);
            headerRL.setVisibility(View.GONE);


            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _isList = false;
                Constant.showLoader(getActivity());
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }
        }
    }

    @Bind(R.id.list_btn)
    Button list_btn;


    @OnClick(R.id.list_btn)
    public void list_btn(View view) {
        if (view.getId() == R.id.list_btn) {

            list_layout.setVisibility(View.VISIBLE);
            headerRL.setVisibility(View.VISIBLE);
            map_view.setVisibility(View.GONE);
            no_post_layout.setVisibility(View.GONE);
            list_btn.setBackgroundColor(Color.WHITE);
            map_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
            header.setText("List");
            map_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
            list_btn.setTextColor(Color.BLACK);

            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _isList = true;
                Constant.showLoader(getActivity());
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }

        }
    }


    @Bind(R.id.map_layout)
    LinearLayout map_layout;


    @Bind(R.id.miles_tabs)
    LinearLayout miles_tabs;

    @Bind(R.id.refresh_iv)
    ImageView refresh_iv;

    @Bind(R.id.mapView)
    RelativeLayout map_view;


    @Bind(R.id.list_layout)
    RelativeLayout list_layout;

    //Load More
    @Bind(R.id.listView_home)
    XListView listView_home;


    HomeListAdapter adapterList;

    private boolean _isList = false;
    private int recordCount = 0;
    private long lastPostTime = 0;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        Constant.hideKeyBoard(getActivity());
        System.out.println("onDestroyView : Called");
        editor.putString("LATITUDE", "");
        editor.putString("LONGITUDE", "");
        editor.putString("userLocationType", "");
        editor.putString("LOCATIONID", "");

        editor.commit();
        if (arrayPoints.size() > 0) {
            arrayPoints.clear();
        }
    }

    private TextView header;
    private ImageView rightIcon = null;

    @Bind(R.id.header)
    RelativeLayout headerRL = null;


    SharedPreferences sPref;
    SharedPreferences.Editor editor = null;
    private String user_id = "";
    private String mlatitude = "";
    private String mlongitude = "";
    private String userLocationType = "";
    private String locationId = "";
    private int _page_number = 1;
    public static ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();
    private static final LatLng DAVV = new LatLng(22.7188222, 75.8708128);
    private static final LatLng TI = new LatLng(22.7188222, 75.8708128);
    private static final LatLng MILAN = new LatLng(22.7188222, 75.8708128);

    public static ArrayList<LatLng> sortLocations(ArrayList<LatLng> locations, final double myLatitude, final double myLongitude) {
        Comparator comp = new Comparator<LatLng>() {
            @Override
            public int compare(LatLng o, LatLng o2) {
                float[] result1 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o.latitude, o.longitude, result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(myLatitude, myLongitude, o2.latitude, o2.longitude, result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        };


        Collections.sort(locations, comp);
        return locations;
    }

    public void drawPolygonOnMap() {
        System.out.println("PolyGon List Size : " + arrayPoints.size());
        if (arrayPoints.size() >= 3) {

            if (googleMap != null) {
                googleMap.clear();
            }

         /*   BitmapDescriptor bitmapMarker = null;
            for (int k = 0 ; k>arrayPoints.size();k++){
                bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                LatLng ll = arrayPoints.get(k);
                googleMap.addMarker(new MarkerOptions().position(ll)

                        .icon(bitmapMarker));
            }*/
/*
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < arrayPoints.size(); z++) {
                LatLng point = arrayPoints.get(z);
                options.add(point);
            }
            googleMap.addPolyline(options);*/
/*

            ArrayList<LatLng> finalArrayList = new ArrayList<LatLng>();
            finalArrayList = sortLocations(arrayPoints, arrayPoints.get(0).latitude, arrayPoints.get(0).longitude);
*/

           /* googleMap.addPolyline((new PolylineOptions())
                    .addAll(arrayPoints).width(5).color(Color.BLACK)
                    .geodesic(true));*/

            PolygonOptions polygonOptions = new PolygonOptions();
            polygonOptions.addAll(arrayPoints);
            polygonOptions.strokeColor(ContextCompat.getColor(getActivity(), R.color.colorForDrawArea));
            polygonOptions.strokeWidth(1);
            polygonOptions.fillColor(ContextCompat.getColor(getActivity(), R.color.colorForDrawArea));
            Polygon polygon = googleMap.addPolygon(polygonOptions);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_view_screen, container, false);
        ButterKnife.bind(this, view);

//        ErrorReporter.getInstance().Init(getActivity());
        MainActivity.drawerFragment.setDrawerState(true);
        header = (TextView) getActivity().findViewById(R.id.hdr_title);

        header.setText("Map");
        rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setVisibility(View.VISIBLE);
        rightIcon.setImageResource(R.drawable.filtr);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("FROMMAPVIEW : In fragment .");
                Intent intToFilter = new Intent(getActivity(), FilterActivity.class);
                intToFilter.putExtra("FROMMAPVIEW", true);

                startActivityForResult(intToFilter, 666);
            }
        });
        setfont();

        if (refresh_iv != null) {
            refresh_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        listView_home.smoothScrollToPosition(0);
                        callGetAllPostsWS(_radiusForWS);
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), getActivity());
                    }

                }
            });
        }


        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (search_et.getText().length() == 0) {
                    search_et.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search_icon, 0, 0, 0);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (search_et.getText().length() == 0) {
                    search_et.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search_icon, 0, 0, 0);
                } else {
                    search_et.setCompoundDrawables(null, null, null, null);
                }

                String searchString = search_et.getText().toString().toLowerCase();
                Log.d("Search Text : ", "" + searchString);
                int realtext = searchString.length();
                if (arrayTemplist.size() > 0) {
                    arrayTemplist.clear();
                }

                for (int i = 0; i < _mainList.size(); i++) {
                    HomeBean bean = _mainList.get(i);
                    String userName = bean.getUser_name().toLowerCase();
                    String postText = bean.getPost_text().toLowerCase();

                    if (realtext <= userName.length() && realtext <= postText.length()) {

                        if (userName.contains(searchString) ||
                                postText.contains(searchString)) {
                            arrayTemplist.add(bean);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapterList = new HomeListAdapter(arrayTemplist, getActivity(), false);
                                    listView_home.setAdapter(adapterList);
                                    listView_home.requestLayout();
                                }
                            });


                        }

                    } else {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapterList.notifyDataSetChanged();
                                listView_home.requestLayout();

                            }
                        });

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_et.getText().length() == 0) {
                    search_et.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search_icon, 0, 0, 0);
                }

            }
        });
        InputMethodManager imm;
        et_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                System.out.println("setOnTouchListener ");
                search_et.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search_et, InputMethodManager.SHOW_IMPLICIT);

                return false;
            }
        });

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);

        /*ImageView rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setImageResource(R.drawable.filtr);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), FilterActivity.class));

            }
        });*/


        //Load More
        listView_home.setSelector(android.R.color.transparent);
        listView_home.setXListViewListener(this);
        listView_home.setPullRefreshEnable(true);
        listView_home.setPullLoadEnable(false);
        listView_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                System.out.println("onItemClick Called");
                String editTextValue = search_et.getText().toString().trim();

//                Constant.showToast("onItem Called",getActivity());

                if (editTextValue.length() > 0 && arrayTemplist.size() > 0) {
                    // TODO: 26/2/16 Search YES
                    /*Fetch value from Temp array list*/
                    final HomeBean bean = arrayTemplist.get(position - 1);
                    Intent intentToTxtImg = new Intent(getActivity(), PostDetailsTextImg.class);
                    intentToTxtImg.putExtra("POSTID", bean.getPost_id());
                    startActivity(intentToTxtImg);

                } else {
                    // TODO: 26/2/16 Search NO
                    /*Fetch value from Main array list*/
                    final HomeBean bean = _mainList.get(position - 1);
                    Intent intentToTxtImg = new Intent(getActivity(), PostDetailsTextImg.class);
                    intentToTxtImg.putExtra("POSTID", bean.getPost_id());
                    startActivity(intentToTxtImg);
                }



              /*  if (bean.getType() == 2) {//TYPE_TEXT_IMG
                    Intent intentToTxtImg = new Intent(getActivity(), PostDetailsTextImg.class);
                    intentToTxtImg.putExtra("POSTID", bean.getPost_id());
                    startActivity(intentToTxtImg);
                }*/

            }
        });

//        listView_home.getFirstVisiblePosition();

        return view;
    }

    private void gpsCheck() {


        /**
         *
         * GPS CHECK
         */


        // check if GPS enabled
        gps = new GPSTracker(getActivity());

        if (gps.canGetLocation()) {

//            Constant.showToast("GPS IS ON ",getActivity());
            double latitude = gps.getLatitude();
            mlatitude = String.valueOf(latitude);

//            editor.putString("LATITUDE",String.valueOf(latitude));
            double longitude = gps.getLongitude();
            mlongitude = String.valueOf(longitude);
//            editor.putString("LONGITUDE",String.valueOf(longitude));
/*
            editor.commit();


            mlongitude = sPref.getString("LONGITUDE", "");
            mlatitude = sPref.getString("LATITUDE", "");*/

            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                callStartThread(20);
            } else {
                Constant.showToast(getResources().getString(R.string.internet), getActivity());
            }
            Constant.showLoader(getActivity());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        callGetAllPostsWS(_radiusForWS);
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), getActivity());
                    }

                }
            }, 2000);

            // TODO: 10/2/16 GPS ON
        } else {
//            Constant.showToast("GPS IS OFF ",getActivity());
            // showSettingsAlert();
            showLocationAlertDialog(getActivity());
           /* if (gps.canGetLocation()) {

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

//                 \n is for new line
                *//* Toast.makeText(getActivity(),
                 "Your Location is - \nLat: " + latitude + "\nLong: " +
                 longitude, Toast.LENGTH_LONG).show();*//*
            }*/

        }

    }


    private GoogleApiClient mGoogleApiClient;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {


    }

    /**
     * If connected get lat and long
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10 * 1000);      // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1 * 1000); // 1 second, in milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private LocationRequest mLocationRequest;

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();

//            Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int cause) {

    }

    /**
     * Show dialog for location using Google API Client
     *
     * @param activity
     */

    @SuppressWarnings("unchecked")
    public void showLocationAlertDialog(final Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API).build();
                mGoogleApiClient.connect();
                Object obj = LocationRequest.create();
                ((LocationRequest) (obj)).setPriority(100);
                ((LocationRequest) (obj)).setInterval(30000L);
                ((LocationRequest) (obj)).setFastestInterval(5000L);
                obj = new LocationSettingsRequest.Builder().addLocationRequest(
                        ((LocationRequest) (obj))).setAlwaysShow(true);

                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        ((LocationSettingsRequest.Builder) (obj)).build())
                        .setResultCallback(
                                new ResultCallback<LocationSettingsResult>() {

                                    @Override
                                    public void onResult(
                                            LocationSettingsResult locationsettingsresult) {
                                        // TODO Auto-generated method stub
                                        Status status;
                                        Log.e("result of yes no",
                                                (new StringBuilder())
                                                        .append("=")
                                                        .append(locationsettingsresult
                                                                .getStatus())
                                                        .toString());
                                        status = locationsettingsresult
                                                .getStatus();
                                        locationsettingsresult
                                                .getLocationSettingsStates();
                                        status.getStatusCode();

                                        try {
                                            status.startResolutionForResult(
                                                    getActivity(), 1000);

                                        } catch (IntentSender.SendIntentException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                    }
                                });
            }
            return;
        } else {
            startActivityForResult(new Intent(
                    "android.settings.LOCATION_SOURCE_SETTINGS"), 1);
            return;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult Called Of Map View");


        if (resultCode == 0) {
            //NO
//            Constant.showToast("NO:" , getActivity());


            mlongitude = "-77.1546507";
            mlatitude = "38.8992651";

            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                callStartThread(20);
            } else {
                Constant.showToast(getResources().getString(R.string.internet), getActivity());
            }
            Constant.showLoader(getActivity());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        callGetAllPostsWS(_radiusForWS);
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), getActivity());
                    }

                }
            }, 2000);
        } else if (resultCode == -1) {
            //Yes
            if (requestCode == 1000) {
//                Constant.showToast("YES:" , getActivity());
                gps = new GPSTracker(getActivity());
                gpsCheck();

            } else if (requestCode == 666) {
                if (resultCode == Activity.RESULT_OK) {
                    boolean result = data.getBooleanExtra("applied", false);
                    if (result) {
                        System.out.println("applied : " + result);
                        System.out.println("_radiusForWS : " + _radiusForWS);
                        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                            Constant.showLoader(getActivity());
                            callGetAllPostsWS(_radiusForWS);
                        }

                    }

                }
                if (resultCode == Activity.RESULT_CANCELED) {
                    //Write your code if there's no result
                }
            }

             /*else  if (requestCode == 555) {
                boolean result = data.getBooleanExtra("applied", false);
                if (result) {
                    System.out.println("applied success");
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
//                    callActivityReport();
                }
            }*/
        }


    }

    private void callStartThread(final int radius) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

//                Constant.showLoader(getActivity());

                try {
                    mJsonObject.put("latitude", mlatitude);
                    mJsonObject.put("longitude", mlongitude);
                    mJsonObject.put("radius", radius);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of START THREAD: " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.START_THREAD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                           /* urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);*/
                            urlConnection.setConnectTimeout(80 * 1000);
                            urlConnection.setReadTimeout(80 * 1000);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
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

                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();
                            System.out.println("Response of START_THREAD : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

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
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {
//                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

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

    private void callGetAllPostsWS(final String radius) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
            boolean isDateFilter = false;
            boolean isMeetUpFilter = false;
            boolean isTwitterFilter = false;
            boolean isYoutubeFilter = false;
            boolean isInstaFilter = false;
            boolean isFLICKRFilter = false;
            boolean isVKFilter = false;
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                isFirstCall = true;
//                Constant.showLoader(getActivity());

                if (refresh_iv != null) {
                    refresh_iv.setVisibility(View.GONE);
                }

                try {


                    mJsonObject.put("latitude", mlatitude);
                    mJsonObject.put("longitude", mlongitude);
                    mJsonObject.put("recordCount", recordCount);
                    mJsonObject.put("lastPostTime", lastPostTime);
                    mJsonObject.put("radius", radius);

                    if (userLocationType.equalsIgnoreCase("CIRCULAR")) {
                        mJsonObject.put("isArea", 0);
                        mJsonObject.put("userLocationId", "");
                    } else if (userLocationType.equalsIgnoreCase("AREA")) {

                        mJsonObject.put("isArea", 1);
                        mJsonObject.put("userLocationId", locationId);

                    }

                    if (_isList) {
                        mJsonObject.put("isMap", 0);
                        mJsonObject.put("pageNo", _page_number);
                        mJsonObject.put("limit", 20);

                    } else {
                        mJsonObject.put("isMap", 1);
                        mJsonObject.put("pageNo", 1);
                        mJsonObject.put("limit", 1000);
                    }


                    isDateFilter = sPref.getBoolean("DATE", false);
                    if (isDateFilter) {
                        long STARTDATE = sPref.getLong("STARTDATE", 0);
                        if (STARTDATE > 0) {
                            STARTDATE = STARTDATE / 1000;
                        }
                        long ENDDATE = sPref.getLong("ENDDATE", 0);
                        if (ENDDATE > 0) {
                            ENDDATE = ENDDATE / 1000;
                        }

                        mJsonObject.put("startDate", STARTDATE);
                        mJsonObject.put("endDate", ENDDATE);

                    }
                    Set<String> stringArrayList = new HashSet<>();
                    editor.putStringSet("", stringArrayList);


                    isMeetUpFilter = sPref.getBoolean("MEETUP", false);
                    isTwitterFilter = sPref.getBoolean("TWITTER", false);
                    isYoutubeFilter = sPref.getBoolean("YOUTUBE", false);
                    isInstaFilter = sPref.getBoolean("INSTA", false);
                    isFLICKRFilter = sPref.getBoolean("FLICKR", false);
                    isVKFilter = sPref.getBoolean("VK", false);


                    JSONArray sourcesArray = new JSONArray();
                    if (isMeetUpFilter) {
                        sourcesArray.put("MEETUP");
                    }

                    if (isTwitterFilter) {
                        sourcesArray.put("TWITTER");
                    }

                    if (isYoutubeFilter) {
                        sourcesArray.put("YOUTUBE");
                    }

                    if (isInstaFilter) {
                        sourcesArray.put("INSTAGRAM");
                    }

                    if (isFLICKRFilter) {
                        sourcesArray.put("FLICKR");
                    }

                    if (isVKFilter) {
                        sourcesArray.put("VK");
                    }

                    if (sourcesArray.length() > 0) {
                        mJsonObject.put("sources", sourcesArray);
                    }


                    int KEYWORDSIZE = sPref.getInt("KEYWORDSIZE", 0);
                    JSONArray keywords = new JSONArray();
                    if (KEYWORDSIZE > 0) {

                        for (int i = 0; i < KEYWORDSIZE; i++) {

                            keywords.put(sPref.getString("KEYWORD_" + i, ""));


                        }
                    }

                    if (keywords.length() > 0) {
                        mJsonObject.put("keywords", keywords);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of Get All Posts: " + mJsonObject.toString());
                System.out.println("Request of Get All Posts: userLocationType " + userLocationType);

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_ALL_POSTS));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                           /* urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);*/
                            urlConnection.setConnectTimeout(80 * 1000);
                            urlConnection.setReadTimeout(80 * 1000);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
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

                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();
                            System.out.println("Response of GetAllPosts : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.hideLoader();
                                Constant.showToast("Server Error ", getActivity());
                            }
                        });

                    }


                } else {
                    Constant.hideLoader();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {

                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

                        JSONObject jsonObject = new JSONObject(_responseMain);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {

                            if (userLocationType.equalsIgnoreCase("AREA")) {
                                // TODO: 23/2/16 HIDDEN Miles tabs
                                if (miles_tabs != null) {
                                    miles_tabs.setVisibility(View.GONE);
                                }

                                drawPolygonOnMap();
                            } else {
                                if (miles_tabs != null) {
                                    miles_tabs.setVisibility(View.VISIBLE);
                                }

                            }


                            if (_page_number == 1) {
                                if (_mainList.size() > 0) {
                                    _mainList.clear();
                                }
                            }


                            JSONArray _resultJSONArray = jsonObject.getJSONArray("data");
                            if (_resultJSONArray.length() > 0) {

                                if (_isList) {
                                    if (no_post_layout != null) {
                                        no_post_layout.setVisibility(View.GONE);
                                    }

                                    map_view.setVisibility(View.GONE);
                                    list_layout.setVisibility(View.VISIBLE);
                                } else {
                                    if (_mainList.size() > 0) {
                                        _mainList.clear();
                                    }
                                    if (no_post_layout != null) {
                                        no_post_layout.setVisibility(View.GONE);
                                    }
                                    if (map_view != null) {
                                        map_view.setVisibility(View.VISIBLE);
                                    }
                                    if (list_layout != null) {
                                        list_layout.setVisibility(View.GONE);
                                    }


                                }
                                int length = _resultJSONArray.length();

                                for (int i = 0; i < length; i++) {
                                    JSONObject _jSubObject = _resultJSONArray.getJSONObject(i);
                                    HomeBean bean = new HomeBean();
                                    JSONObject mObjectLotLng = new JSONObject(_jSubObject.getString("loc"));
                                    if (mObjectLotLng.has("x")) {
                                        String userlong = String.valueOf(mObjectLotLng.getDouble("x"));
                                        bean.setPost_long(userlong);
                                    }
                                    if (mObjectLotLng.has("y")) {
                                        String userlat = String.valueOf(mObjectLotLng.getDouble("y"));
                                        bean.setPost_lat(userlat);
                                    }
                                    /*System.out.println("userlong : " +userlong);
                                    System.out.println("userlat : " +userlat);*/


                                    JSONObject mJsonObjectUser = new JSONObject(_jSubObject.getString("user"));
                                    if (mJsonObjectUser.has("username")) {
                                        bean.setUser_name(mJsonObjectUser.getString("username"));
                                    } else {
                                        bean.setUser_name("");
                                    }

                                    if (mJsonObjectUser.has("isFollowed")) {
                                        bean.setIsFollowed(mJsonObjectUser.getBoolean("isFollowed"));
                                    } else {
                                        bean.setIsFollowed(false);
                                    }
                                    if (mJsonObjectUser.has("id")) {
                                        bean.setId(mJsonObjectUser.getString("id"));
                                    } else {
                                        bean.setId("");
                                    }


                                    if (mJsonObjectUser.has("profilePic")) {
                                        JSONObject mJsonObjectPic = mJsonObjectUser.getJSONObject("profilePic");
                                        String postImgUrl = "";
                                        String mSmall = mJsonObjectPic.getString("small");
                                        String medium = mJsonObjectPic.getString("medium");
                                        String large = mJsonObjectPic.getString("large");
                                        if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                            postImgUrl = mSmall;
                                        } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                            postImgUrl = medium;
                                        } else if (large != null && !large.equalsIgnoreCase("")) {
                                            postImgUrl = large;
                                        }
                                        bean.setUser_image(postImgUrl);

                                    } else {
                                        bean.setUser_image("");
                                    }


                                    if (mJsonObjectUser.has("address")) {
                                        bean.setUser_address(mJsonObjectUser.getString("address"));
                                    } else {
                                        bean.setUser_address("");
                                    }


                                    if (_jSubObject.has("postText")) {

                                        String originalString = _jSubObject.getString("postText");
//                                        System.out.println("Post Text originalString : " + originalString);
                                        if (originalString.contains("http")) {
                                            String newString = originalString.replaceAll("(?:https?|ftps?)://[\\w/%.-]+", "<a href='$0'>$0</a>");
//                                            System.out.println("Post Text newString : " + newString);
                                            bean.setPost_text(newString);
                                        } else {
//                                            System.out.println("Post Text originalString SET : " + originalString);
                                            bean.setPost_text(originalString);
                                        }


                                    } else {
                                        bean.setPost_text("");
                                    }

                                    if (_jSubObject.has("id")) {
                                        bean.setPost_id(_jSubObject.getString("id"));
//                                        System.out.println("post Id : " +_jSubObject.getString("id"));
                                    } else {
                                        bean.setPost_id("");
                                    }

                                    if (_jSubObject.has("sourceType")) {
                                        bean.setSource(_jSubObject.getString("sourceType"));
                                    } else {
                                        bean.setSource("");
                                    }


                                    if (_jSubObject.has("source_id")) {
                                        bean.setSource_id(_jSubObject.getString("source_id"));
                                    } else {
                                        bean.setSource_id("");
                                    }


                                    if (_jSubObject.has("postType")) {
                                        System.out.println("POst Type : " + _jSubObject.getString("postType"));
                                        bean.setView_type(_jSubObject.getString("postType"));
                                        if (_jSubObject.getString("postType").equalsIgnoreCase("TEXT_ONLY")) {
                                            bean.setType(1);
                                        } else if (_jSubObject.getString("postType").equalsIgnoreCase("TEXT_WITH_IMAGE")) {
                                            bean.setType(2);
                                        } else if (_jSubObject.getString("postType").equalsIgnoreCase("I")) {
                                            bean.setType(3);
                                        } else if (_jSubObject.getString("postType").equalsIgnoreCase("VIDEO_ONLY") ||
                                                _jSubObject.getString("postType").equalsIgnoreCase("TEXT_WITH_VIDEO")) {
                                            bean.setType(4);
                                        } else {
                                            bean.setType(1);
                                        }
                                        /*else if (_jSubObject.getString("view_type").equalsIgnoreCase("V")){
                                            bean.setType(3);
                                        }*/

                                    } else {
                                        bean.setView_type("");
                                    }

                                    if (_jSubObject.has("postImage")) {

                                        JSONObject mpostImage = _jSubObject.getJSONObject("postImage");
                                        String postImgUrl = "";
                                        String mSmall = mpostImage.getString("small");
                                        String medium = mpostImage.getString("medium");
                                        String large = mpostImage.getString("large");
                                        if (large != null && !large.equalsIgnoreCase("")) {
                                            postImgUrl = large;
                                        } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                            postImgUrl = medium;
                                        } else if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                            postImgUrl = mSmall;
                                        }


                                        bean.setPost_image(postImgUrl);
                                    } else {
                                        bean.setPost_image("");
                                    }

                                    if (_jSubObject.has("postLocation")) {
                                        bean.setPost_location(_jSubObject.getString("postLocation"));
                                    } else {
                                        bean.setPost_location("");
                                    }

                                    if (_jSubObject.has("postTime")) {
                                        long postTime = _jSubObject.getLong("postTime");
                                        postTime = postTime * 1000;
                                        bean.setPost_time(getDateFromMilliseconds(postTime, DateTime.DATE_FORMAT));
                                    } else {
                                        bean.setPost_time("");
                                    }

                                    if (_jSubObject.has("postVideo")) {
                                        bean.setPost_video(_jSubObject.getString("postVideo"));
                                    } else {
                                        bean.setPost_video("");
                                    }

//                                    System.out.println("User Img while adding : " + bean.getUser_image());


                                    _mainList.add(bean);

                                }

                                if (_isList) {
                                    //todo setting Adapter here

                                    if (_page_number == 1) {
                                        adapterList = new HomeListAdapter(_mainList, getActivity(), false);
                                        listView_home.setAdapter(adapterList);
//                                        onLoad();
                                        listView_home.stopRefresh();
                                    } else {

                                        adapterList.notifyDataSetChanged();
                                    }


                                } else {


                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!userLocationType.equalsIgnoreCase("AREA")) {

                                                if (googleMap != null) {
                                                    googleMap.clear();
                                                }
                                                // TODO: 4/12/15 setting Map here
                                                if (radius.equalsIgnoreCase("2")) {
                                                    mapWithZooming(12);
                                                } else if (radius.equalsIgnoreCase("5")) {
                                                    mapWithZooming(11);
                                                } else if (radius.equalsIgnoreCase("10")) {
                                                    mapWithZooming(10);
                                                } else if (radius.equalsIgnoreCase("20")) {
                                                    mapWithZooming(9);
                                                }

                                                addCircleToMap(Integer.parseInt(radius));
                                            } else {

                                                mapWithZooming(13);
                                            }
                                        }
                                    });


                                }


                            } else {
                                no_post_layout.setVisibility(View.VISIBLE);
                                map_view.setVisibility(View.GONE);
                                list_layout.setVisibility(View.GONE);
                            }

                            if (jsonObject.has("isMore")) {
                                String _is_more = jsonObject.getString("isMore");
                                if (listView_home != null) {
                                    if (_is_more.equalsIgnoreCase("Yes")) {
                                        listView_home.setPullLoadEnable(true);
                                    } else {
                                        listView_home.setPullLoadEnable(false);
                                    }
                                }


                            } else {
                                if (listView_home != null) {
                                    listView_home.setPullLoadEnable(false);
                                }

                            }

                            if (jsonObject.has("recordCount")) {
                                recordCount = jsonObject.getInt("recordCount");

                            }
                            if (jsonObject.has("lastPostTime")) {
                                lastPostTime = jsonObject.getLong("lastPostTime");

                            }
                            if (jsonObject.has("isNewPostArrived")) {
                                boolean isNewPostArrived = jsonObject.getBoolean("isNewPostArrived");
                                if (isNewPostArrived) {
                                    /*New Post Refresh Enable*/
                                    recordCount = 0;
                                    lastPostTime = 0;

                                    if (refresh_iv != null) {
                                        refresh_iv.setVisibility(View.VISIBLE);
                                    }

                                } else {
                                    /*New Post Refresh Disable*/
                                    if (refresh_iv != null) {
                                        refresh_iv.setVisibility(View.GONE);
                                    }
                                }
                            }

                            Constant.hideLoader();
                        } else if (status == 0) {
                            Constant.hideLoader();
                            if (no_post_layout != null) {
                                no_post_layout.setVisibility(View.VISIBLE);
                            }

                            map_view.setVisibility(View.GONE);
                            list_layout.setVisibility(View.GONE);
//                            Constant.showToast("Server Error", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.hideLoader();
                            Constant.alertForLogin(getActivity());
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                        Constant.hideLoader();
                        Constant.showToast("Server Error ", getActivity());

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


    private String getDateFromMilliseconds(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    private void init() {
//        gps = new GPSTracker(getActivity());
        if (!mlatitude.equalsIgnoreCase("") &&
                !mlongitude.equalsIgnoreCase("")) {

            latitude = Double.parseDouble(mlatitude);
            longitude = Double.parseDouble(mlongitude);
            try {
                // Loading map
                initilizeMap();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
//            Constant.showToast("Server Error ", getActivity());
        }

        /*if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            if (!String.valueOf(latitude).equalsIgnoreCase("0.0") &&
                    !String.valueOf(longitude).equalsIgnoreCase("0.0")) {

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
        }*/


    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy Called");
        editor.putString("LATITUDE", "");
        editor.putString("LONGITUDE", "");
        editor.putString("userLocationType", "");
        editor.putString("LOCATIONID", "");

        editor.commit();

        if (arrayPoints.size() > 0) {
            arrayPoints.clear();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        userLocationType = sPref.getString("userLocationType", "CIRCULAR");
        locationId = sPref.getString("LOCATIONID", "");
        editor = sPref.edit();
        try {

            boolean fromMenu = getArguments().getBoolean("FROMMENU");
            if (fromMenu) {
                //Clear location
                editor.putString("LATITUDE", "");
                editor.putString("LONGITUDE", "");

                editor.commit();
            }
        } catch (Exception e) {
//            e.printStackTrace();
        }
        createLocationRequest();
        gps = new GPSTracker(getActivity());
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.mapView);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapView, fragment).commit();
        }

        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        user_id = sPref.getString("id", "");
        mlongitude = sPref.getString("LONGITUDE", "");
        mlatitude = sPref.getString("LATITUDE", "");

        if (mlatitude == null || mlatitude.equalsIgnoreCase("") || mlatitude.equalsIgnoreCase("0.0")) {
            gpsCheck();
        } else {
            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                callStartThread(20);
            } else {
                Constant.showToast(getResources().getString(R.string.internet), getActivity());
            }
            Constant.showLoader(getActivity());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        callGetAllPostsWS(_radiusForWS);
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), getActivity());
                    }

                }
            }, 2000);
        }


    }

    /**
     * function to load map. If map is not created it will create it for you
     */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = fragment.getMap();
            // Set a listener for info window events.
            googleMap.setOnInfoWindowClickListener(this);
//            googleMap.setMyLocationEnabled(true);

            try {

                mapWithZooming(13);
//                addCircleToMap(2);


            } catch (Exception e) {
                System.out.println("exception " + e);
            }

        }
    }

    private void mapWithZooming(int zoom) {

        int _length = _mainList.size();
        System.out.println("latitude : mapWithZooming " + latitude);
        _latLong = new LatLng(latitude, longitude);
        if (_length > 0) {


            cameraPosition = new CameraPosition.Builder().target(_latLong)
                    .zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            for (int i = 0; i < _length; i++) {
                HomeBean bean = _mainList.get(i);
                double _lat = Double.parseDouble(bean.getPost_lat());
                double _long = Double.parseDouble(bean.getPost_long());
                LatLng ll = new LatLng(_lat, _long);
                BitmapDescriptor bitmapMarker = null;
                if (!bean.getSource().equalsIgnoreCase("")) {
                    if (bean.getSource().equalsIgnoreCase("TWITTER")) { //Tweet
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.twitter_pin);
                    } else if (bean.getSource().equalsIgnoreCase("INSTAGRAM")) {//Instagram
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.instagram_pin);
                    } else if (bean.getSource().equalsIgnoreCase("YOUTUBE")) {//Youtube
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.youtube_pin);
                    } else if (bean.getSource().equalsIgnoreCase("MEETUP")) {//MeetUp
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.meetup_pin);
                    } else if (bean.getSource().equalsIgnoreCase("VK")) {//Vk
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.vk_pin);
                    } else if (bean.getSource().equalsIgnoreCase("FLICKR")) {//FLICKR
                        bitmapMarker = BitmapDescriptorFactory.fromResource(R.mipmap.flicker_pin);
                    } else {
                        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    }

                } else {
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                }


                // Setting an info window adapter allows us to change the both the contents and look of the

                String userName = "";
                if (!bean.getUser_name().equalsIgnoreCase("")) {
                    userName = bean.getUser_name();
                } else {
                    userName = bean.getSource() + " User";
                }
                String postID = bean.getPost_id();
                String snippet = "";
                if (!postID.equalsIgnoreCase("")) {
                    String userImg = bean.getUser_image();
                    if (userImg.equalsIgnoreCase("")) {
                        userImg = "demoimg";
                    } else {
                        userImg = bean.getUser_image();
                    }
                    snippet = bean.isFollowed() + "&" + userImg + "&" + bean.getPost_id() + "&" + bean.getPost_text();
                }/* else {
                    snippet = bean.getUser_image();
                }*/
//                System.out.println("FINAL SNIPPET ON MAP @#@#@#@ :" + snippet + " With Post Id : " + postID);
                googleMap.addMarker(new MarkerOptions().position(ll)
                        .title(userName)
                        .snippet(snippet)

                        .icon(bitmapMarker));

            }


            // info window.
            googleMap.setInfoWindowAdapter(new CustomWindAdapter(getActivity()));
        }


    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */
    boolean isFirstCall = false;

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        mlatitude = String.valueOf(latitude);
        double longitude = location.getLongitude();
        mlongitude = String.valueOf(longitude);
        init();
        if (!isFirstCall) {
            try {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    callStartThread(20);
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), getActivity());
                }
                Constant.showLoader(getActivity());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                            callGetAllPostsWS(_radiusForWS);
                        } else {
                            Constant.showToast(getResources().getString(R.string.internet), getActivity());
                        }

                    }
                }, 2000);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }


    private void addCircleToMap(int miles) {

        // circle settings
        double radiusM = miles * 1609.34;// your radius in meters
        int radiusInt = (int) radiusM;


        CircleOptions circleOptions = new CircleOptions().center(_latLong) // set center
                .radius(radiusInt) // set radius in meters
                .strokeColor(0x10000000).strokeWidth(5).
                        fillColor(ContextCompat.getColor(getActivity(), R.color.colorForDrawArea));
        // Fill color of the circle 0x55fe9f9f PINK color
        // 0x represents, this is an hexadecimal code
        // 55 represents percentage of transparency. For 100% transparency, specify 00.
        // For 0% transparency ( ie, opaque ) , specify ff
        // The remaining 6 characters(00ff00) specify the fill color

        if (mapCircle != null) {
            mapCircle.remove();
        }
        mapCircle = googleMap.addCircle(circleOptions);

      /*  googleMap.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(_latLong, radiusInt * 2, radiusInt * 2).
                transparency(0.6f));*/
    }


    public void setfont() {
        Constant.setFont(getActivity(), two_miles_btn, 0);
        Constant.setFont(getActivity(), five_miles_btn, 0);
        Constant.setFont(getActivity(), ten_miles_btn, 0);
        Constant.setFont(getActivity(), twenty_miles_btn, 0);
        Constant.setFont(getActivity(), map_btn, 0);
        Constant.setFont(getActivity(), list_btn, 0);
        Constant.setFont(getActivity(), search_et, 0);
        Constant.setFont(getActivity(), sorry_tv, 0);
        Constant.setFont(getActivity(), no_post_tv, 0);
        Constant.setFont(getActivity(), header, 0);
    }


    private void activeMilesBtn(int activeBtnId) {

        switch (activeBtnId) {
            case 2:
                two_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));

                two_miles_btn.setTextColor(Color.BLACK);
                five_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                ten_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                twenty_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));


                break;
            case 5:
                five_miles_btn.setBackgroundColor(Color.WHITE);
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));

                two_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                five_miles_btn.setTextColor(Color.BLACK);
                ten_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                twenty_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));


                break;
            case 10:
                ten_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                twenty_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));

                two_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                five_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                ten_miles_btn.setTextColor(Color.BLACK);
                twenty_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));


                break;

            case 20:
                twenty_miles_btn.setBackgroundColor(Color.WHITE);
                five_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                two_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
                ten_miles_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));


                two_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                five_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
                twenty_miles_btn.setTextColor(Color.BLACK);
                ten_miles_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));

                break;
            default:
                break;

        }

    }

    @Override
    public void onRefresh() {
//For Pull To Refresh
        _page_number = 1;
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callStartThread(20);
        } else {
            Constant.showToast(getResources().getString(R.string.internet), getActivity());
        }
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetAllPostsWS(_radiusForWS);
        } else {
            Constant.showToast(getResources().getString(R.string.internet), getActivity());
        }

    }

    @Override
    public void onLoadMore() {
//For Load More from Bottom
        _page_number = _page_number + 1;
        callGetAllPostsWS(_radiusForWS);
//        onLoad();
    }

    private void onLoad() {
        listView_home.stopLoadMore();


    }


    @Override
    public void onInfoWindowClick(Marker marker) {


        String snippet = marker.getSnippet();
        System.out.println("snippet : " + snippet);
        String userImg = "";
        String postId = "";
        String isFollowed = "";

        String post_text = "";
        if (snippet.contains("&")) {
            String[] snippetWHole = snippet.split("&");
            isFollowed = snippetWHole[0];
            userImg = snippetWHole[1];
            postId = snippetWHole[2];
            if (snippetWHole.length > 3) {
                post_text = snippetWHole[3];
            } else {
                post_text = "";
            }
            System.out.println("postId " + postId);

            Intent intentToTxtImg = new Intent(getActivity(), PostDetailsTextImg.class);
            intentToTxtImg.putExtra("POSTID", postId);
            startActivity(intentToTxtImg);
        } else {
            Constant.showToast("Post not found.", getActivity());
        }


    }

}
