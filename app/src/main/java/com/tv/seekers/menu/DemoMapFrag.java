package com.tv.seekers.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
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
import com.tv.seekers.utils.CustomAutoCompletetextview;
import com.tv.seekers.utils.GeocodingLocation;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 6/11/15.
 */
public class DemoMapFrag extends Fragment {
    FrameLayout fram_map;
    ImageView btn_draw_State;
    Boolean Is_MAP_Moveable = false; // to detect map is movable
    private double latitude = 0.0;
    private double longitude = 0.0;
    LatLng _latLong;
    GPSTracker gps;
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    //    HashSet<LatLng> val;
    private ArrayList<LatLng> val;
    private ArrayList<HomeBean> mLatLongList = new ArrayList<HomeBean>();
    private SharedPreferences sPref;

    private SharedPreferences.Editor editor;

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


    @Bind(R.id.search_iv)
    ImageView search_iv;

    @Bind(R.id.search_et)
    CustomAutoCompletetextview search_et;


    private String _address = "";
    private String input = "";
    private SearchPlacesTask searchplacesTask;
    private SearchParserTask searchparserTask;
    private boolean isDrawOption = false;
    private double _lat = 0.0;
    private double _long = 0.0;
    private ArrayList<MyAreasBean> myAreasList = new ArrayList<MyAreasBean>();


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


    public void saveData(Activity activity) {
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            if (mLatLongList.size() > 0) {
                showAddLocationsDialog();
            } else {
                Constant.showToast("Please draw area.", getActivity());
            }
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }


    }


    private boolean screenLeave = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.demo_map, container, false);

        ButterKnife.bind(this, view);
        Constant.setFont(getActivity(), search_et, 0);
        fram_map = (FrameLayout) view.findViewById(R.id.fram_map);
        btn_draw_State = (ImageView) view.findViewById(R.id.btn_draw_State);
        _rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        _header = (TextView) getActivity().findViewById(R.id.hdr_title);
        val = new ArrayList<LatLng>();
        // Button will change Map movable state
        areaLatLngJSON = new JSONObject();
        areaLatLngArray = new JSONArray();
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!Is_MAP_Moveable) {
                    btn_draw_State.setImageResource(R.mipmap.edit_on);
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
                    btn_draw_State.setImageResource(R.mipmap.edit_off);
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
                          /*  if (val.size()==3){
                                val.remove(1);
                            }*/

                            val.add(new LatLng(latitude, longitude));
                            screenLeave = false;
                            Draw_Map();

                        case MotionEvent.ACTION_UP:

//                            System.out.println("ACTION_UP");
                            if (!screenLeave) {
                                screenLeave = true;
                            } else {
                                System.out.println("ACTION_UP ELSE CAse");
                                Is_MAP_Moveable = false; // to detect map is movable
                                btn_draw_State.setImageResource(R.mipmap.erase_icon);
                                source = 0;
                                destination = 1;
                                draw_final_polygon();

                            }

                            // finger leaves the screen
//                            Is_MAP_Moveable = false; // to detect map is movable
//                            Draw_Map();
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


        //changed by Shoeb
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (search_et.getText().length() == 0) {
                    search_iv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (search_et.getText().length() == 0) {
                    search_iv.setVisibility(View.VISIBLE);

                } else {
                    search_iv.setVisibility(View.GONE);
                }
                input = s.toString();
                /*if (input.contains(" ")) {
                    input = input.replaceAll(" ", "+");
                }*/

                searchplacesTask = new SearchPlacesTask();
                searchplacesTask.execute();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_et.getText().length() == 0) {
                    search_iv.setVisibility(View.VISIBLE);
                }
         /*       searchplacesTask = new SearchPlacesTask();
                searchplacesTask.execute(s.toString());*/

            }
        });

        search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.hideKeyBoard(getActivity());
                gps = new GPSTracker(getActivity());


                try {

                    HashMap<String, String> hm = (HashMap<String, String>)
                            parent.getAdapter().getItem(position);
                    String _locName = hm.get("description");
                    search_et.setText(_locName);

                    if (_locName.length() > 0) {
                        int lengthOfText = _locName.length();
                        search_et.setSelection(lengthOfText);
                    }

                    _address = search_et.getText().toString();

                    PlaceDetails mPlaceDetails = new PlaceDetails();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        mPlaceDetails.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
                    } else {
                        mPlaceDetails.execute((String[]) null);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Constant.showToast("Server Error ", getActivity());
                }


            }
        });

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callsavedLocationWS();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }
        return view;
    }


    private void draw_final_polygon() {

        val.add(val.get(0));

        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(val);
        polygonOptions.strokeColor(ContextCompat.getColor(getActivity(), R.color.map_circle_color));
        polygonOptions.strokeWidth(8);
        polygonOptions.fillColor(ContextCompat.getColor(getActivity(), R.color.map_circle_color));
        Polygon polygon = googleMap.addPolygon(polygonOptions);
    }


    private void callsavedLocationWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            JSONObject mJsonObjectUser = new JSONObject();


            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                try {

                    mJsonObjectUser.put("pageNo", 1);
                    mJsonObjectUser.put("limit", 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {


                        HttpURLConnection urlConnection;

//                        String query = builder.build().getEncodedQuery();
                        String query = mJsonObjectUser.toString();
                        System.out.println("query for Get Locations : " + query);
                        //			String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_LOC));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setReadTimeout(200000);
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
                            //System.out.println("Uploading............");
                            sb.append(line);
                        }

                        bufferedReader.close();
                        _responseMain = sb.toString();
                        System.out.println("Response of Location Screen : " + _responseMain);


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
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
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

                            if (jsonObject.has("data")) {

                                JSONArray data = jsonObject.getJSONArray("data");

                                if (data.length() > 0) {
                                    if (myAreasList.size() > 0) {
                                        myAreasList.clear();
                                    }
                                    if (isDrawOption) {
                                        MyAreasBean beanDemo = new MyAreasBean();
                                        beanDemo.setLoc_name("");
                                        beanDemo.setLoc_add("");
                                        myAreasList.add(beanDemo);
                                    }


                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject jsonobj = data.getJSONObject(i);
                                        String loc_name = jsonobj.getString("locName");
                                        String loc_address = jsonobj.getString("address");

                                        String id = jsonobj.getString("id");
                                        String loc_image = jsonobj.getString("locImage");
                                        String userLocationType = jsonobj.getString("userLocationType");
                                        MyAreasBean bean = new MyAreasBean();
                                        if (userLocationType.equalsIgnoreCase("AREA")) {
                                            if (jsonobj.has("areaLatLng")) {
                                                JSONArray areaLatLngARRAY = jsonobj.getJSONArray("areaLatLng");
                                                if (areaLatLngARRAY.length() > 0) {
                                                    // TODO: 23/2/16  For Loop

                                                    ArrayList<LatLng> arrayPointsList = new ArrayList<LatLng>();
                                                    for (int j = 0; j < areaLatLngARRAY.length(); j++) {
                                                        JSONObject areaLatLngJSON = areaLatLngARRAY.getJSONObject(j);
                                                        double longitudeArea = areaLatLngJSON.getDouble("x");
                                                        double latitudeArea = areaLatLngJSON.getDouble("y");
                                                        LatLng areaLatLng = new LatLng(latitudeArea, longitudeArea);
                                                        arrayPointsList.add(areaLatLng);

                                                    }
                                                    JSONObject areaLatLngJSON = areaLatLngARRAY.getJSONObject(0);
                                                    double longitudeArea = areaLatLngJSON.getDouble("x");
                                                    double latitudeArea = areaLatLngJSON.getDouble("y");
                                                    LatLng areaLatLng = new LatLng(latitudeArea, longitudeArea);
                                                    arrayPointsList.add(areaLatLng);
                                                    bean.setAreaLatLng(arrayPointsList);


                                                }
                                            }
                                        }

                                        JSONObject mObjectLotLng = new JSONObject(jsonobj.getString("loc"));

                                        bean.setType(userLocationType);
                                        if (mObjectLotLng.has("x")) {
                                            String userlong = String.valueOf(mObjectLotLng.getDouble("x"));
                                            bean.set_long(userlong);
                                        }
                                        if (mObjectLotLng.has("y")) {
                                            String userlat = String.valueOf(mObjectLotLng.getDouble("y"));
                                            bean.set_lat(userlat);
                                        }

                                        bean.setLoc_name(loc_name);
                                        bean.setLoc_add(loc_address);

                                        bean.setId(id);
                                        bean.setImg_url(loc_image);


                                        myAreasList.add(bean);


                                    }


                                }


                            }

                        } else if (status == 0) {
                            Constant.showToast("Server Error", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
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

    String finalimgUrl = "";

    private class PlaceDetails extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("PlaceDetails Called");
            Constant.showLoader(getActivity());

        }


        @Override
        protected String doInBackground(String... place) {

            // For storing data from web service
            String data = "";


            try {

                String loc = URLEncoder.encode(_address, "utf-8");
                String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?" +
                        "query=" + loc + "&" +
                        "key=AIzaSyDEkwu2c89RobN7jfQ1nvoyIAC6Gt4FWpI";


                System.out.println("Final URL Google API : " + url);
                // Fetching the data from we service
                data = downloadUrlsearch(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());

            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println("result From place ID : " + result);
            Constant.hideLoader();
            String _photo_reference = "";
            try {

                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.has("results")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        if (jsonObject1.has("photos")) {
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("photos");
                            if (jsonArray1.length() > 0) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(0);
                                if (jsonObject2.has("photo_reference")) {
                                    _photo_reference = jsonObject2.getString("photo_reference");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!_photo_reference.equalsIgnoreCase("")) {
                finalimgUrl = "https://maps.googleapis.com/maps/api/place/photo?" +
                        "maxwidth=400&" +
                        "photoreference=" + _photo_reference +
                        "&key=AIzaSyDEkwu2c89RobN7jfQ1nvoyIAC6Gt4FWpI";

                System.out.println("Final  Image URL : " + finalimgUrl);
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(_address,
                        getActivity(), new GeocoderHandler());
            } else {
                GeocodingLocation locationAddress = new GeocodingLocation();
                locationAddress.getAddressFromLocation(_address,
                        getActivity(), new GeocoderHandler());
            }
        }
    }

    private String locName = "";

    private void showAddLocationsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_keywords, null);

        final EditText etKeywordName = (EditText) view.findViewById(R.id.etKeywordName);
        etKeywordName.setHint("Enter location name.");

        alert.setView(view);


        TextView title = new TextView(getActivity());
       // You Can Customise your Title here
        title.setText("Name of Location");
        title.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.whiteShade));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.LEFT);
        title.setTextColor(Color.BLACK);
        title.setTextSize(20);

        alert.setCustomTitle(title);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etKeywordName.getText().toString().trim().isEmpty()) {
                    Constant.showToast("Please enter Location name.", getActivity());
                } else {

                    dialog.dismiss();
                    locName = etKeywordName.getText().toString();
                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        callSaveLocationWS(locName, locName, "AREA", "");
                    } else {
                        Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                    }


                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress = "";
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    if (bundle.getString("address") != null) {
                        locationAddress = bundle.getString("address");
                    }

                    break;
                default:
                    locationAddress = "";
            }

            boolean _isLocationAlreadyAdded = false;
            if (!locationAddress.equalsIgnoreCase("")) {
                try {

                    System.out.println("locationAddressLatLng : " + locationAddress);

                    String[] array1 = locationAddress.split("\\:");
                    if (array1.length > 0) {
                        System.out.println("array1 : " + array1[2]);
                        String latLng = "";
                        if (array1[2].contains("\n")) {
                            latLng = array1[2].replace("\n", " ");
                        }
                        System.out.println("latLng : " + latLng);
                        String[] finalLatLng;

                        finalLatLng = latLng.split("\\s+");
                        System.out.println("Lat : " + finalLatLng[1]);
                        System.out.println("Long : " + finalLatLng[2]);

                        _lat = Double.parseDouble(finalLatLng[1]);
                        _long = Double.parseDouble(finalLatLng[2]);
                        int position = 0;
                        if (myAreasList.size() > 0) {
                            for (int j = 0; j < myAreasList.size(); j++) {
                                MyAreasBean _bean = myAreasList.get(j);
                                String _add = _bean.getLoc_add();
                                String _latUser = _bean.get_lat();
                                String _longUser = _bean.get_long();

                                String _locName = _bean.getLoc_name();
                                if (_locName.equalsIgnoreCase(_address)) {
                                    _isLocationAlreadyAdded = true;
                                    position = j;
                                    break;
                                } else {
                                    _isLocationAlreadyAdded = false;
                                }


                            }
                        }


                        if (_isLocationAlreadyAdded) {
                            search_et.setText("");
                            // TODO: 10/12/15 Redirect to Home screen with current LatLong

                            if (!isDrawOption) {
                                // TODO: 10/12/15 Redirect to Home Screen
                                // TODO: 10/12/15  Replace Fragment here with Home Fragment
                                saveCurrentLatLong(position);
                                replaceFragment();

                            } else {
                                // TODO: 18/12/15 Need to Show Pop Up message as Location
                                Constant.showToast(getActivity().getResources().getString(R.string.locationAlreadySavedText), getActivity());
                            }
                        } else {
                            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                                callSaveUserLocation();
                            } else {
                                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                            }
                        }
                    } else {
                        Constant.showToast("Server Error", getActivity());
                        search_et.setText("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Constant.showToast("Server Error", getActivity());
                    search_et.setText("");
                }
            }


        }
    }


    private void callSaveUserLocation() {
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

                    locJsonObject.put("x", _long);
                    locJsonObject.put("y", _lat);

                    mainJsonObject.put("loc", locJsonObject);
                    mainJsonObject.put("address", _address);
                    mainJsonObject.put("locName", _address);
                    mainJsonObject.put("userLocationType", "CIRCULAR");
                    mainJsonObject.put("locImage", finalimgUrl);

                    System.out.println("Request of USER_SAVE_LOCATION : " + mainJsonObject.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                            search_et.setText("");

                            editor.putString("LATITUDE", String.valueOf(_lat));
                            editor.putString("LONGITUDE", String.valueOf(_long));
                            editor.putString("userLocationType", "");

                            editor.commit();
                            replaceFragment();

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
                        } else if (status==-4){
                            Constant.showToast("Location name already exists.", getActivity());
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

    private void saveCurrentLatLong(int position) {
        try {
            MyAreasBean bean = myAreasList.get(position);
            editor.putString("LATITUDE", bean.get_lat());
            editor.putString("LONGITUDE", bean.get_long());

            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void replaceFragment() {
        MapView fragment = new MapView();
        if (fragment != null) {


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();


        }
    }


    JSONObject areaLatLngJSON;
    JSONArray areaLatLngArray;


    private void callSaveLocationWS(final String locName, final String locDescription, final String locType, final String locImg) {
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
                    mainJsonObject.put("address", locDescription);
                    mainJsonObject.put("locName", locName);
                    mainJsonObject.put("userLocationType", locType);
                    mainJsonObject.put("locImage", locImg);
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

                            // TODO: 24/2/16 Replace Current Fragment with Home Fragment.
                            editor.putString("userLocationType", "AREA");
                            JSONObject dataJSON = jsonObject.getJSONObject("data");
                            editor.putString("LOCATIONID", dataJSON.getString("id"));
                            editor.commit();

                            if (MapView.arrayPoints.size() > 0) {
                                MapView.arrayPoints.clear();
                            }

                            if (dataJSON.has("areaLatLng")) {
                                JSONArray areaLatLngJSONARRAY = dataJSON.getJSONArray("areaLatLng");
                                if (areaLatLngJSONARRAY.length() > 0) {
                                    ArrayList<LatLng> arrayPointsList = new ArrayList<LatLng>();
                                    for (int i = 0; i < areaLatLngJSONARRAY.length(); i++) {
                                        JSONObject latLngJsonObj = areaLatLngJSONARRAY.getJSONObject(i);
                                        double longitude = latLngJsonObj.getDouble("x");
                                        double lat = latLngJsonObj.getDouble("y");
                                        LatLng ll = new LatLng(lat, longitude);
                                        arrayPointsList.add(ll);
                                    }

                                    JSONObject areaLatLngJSON = areaLatLngJSONARRAY.getJSONObject(0);
                                    double longitudeArea = areaLatLngJSON.getDouble("x");
                                    double latitudeArea = areaLatLngJSON.getDouble("y");
                                    LatLng areaLatLng = new LatLng(latitudeArea, longitudeArea);
                                    arrayPointsList.add(areaLatLng);

                                    MapView.arrayPoints = arrayPointsList;

                                }
                            }

                            replaceFragment();
                           /* JSONObject dataJSON = jsonObject.getJSONObject("data");
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
                            }*/

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
                        } else if (status == -2) {

                            Constant.showToast("Radius is greater than 20 miles. Please Re-Draw.    ", getActivity());
                        } else if (status == -3) {

                            Constant.showToast("Invalid shape.", getActivity());
                        } else if (status==-4){
                            Constant.showToast("Location name already exists.", getActivity());
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

    int source = 0;
    int destination = 1;

    public void Draw_Map() {


//        specify latitude and longitude of both source and destination Polyline

        if (val.size() > 1) {
            googleMap.addPolyline(new PolylineOptions().add(val.get(source), val.get(destination)).width(8).color(ContextCompat.getColor(getActivity(), R.color.map_circle_color)));
            source++;
            destination++;
        }

        /*googleMap.addPolyline((new PolylineOptions())
                .addAll(val).width(5).color(Color.BLACK)
                .geodesic(true));*/
       /* PolygonOptions rectOptions = new PolygonOptions();
        rectOptions.addAll(val);
        rectOptions.strokeColor(ContextCompat.getColor(getActivity(), R.color.map_circle_color));
        rectOptions.fillColor(Color.YELLOW);
        //        rectOptions.fillColor(Color.BLUE);

//        googleMap.addPolyline(new PolylineOptions().addAll(val).width(2.0f).color(Color.RED));
        polygon = googleMap.addPolygon(rectOptions);*/

    }

    private class SearchPlacesTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("SearchPlacesTask Called");

        }


        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console

            String key = "key=AIzaSyDEkwu2c89RobN7jfQ1nvoyIAC6Gt4FWpI";


            try {
//                input = "input=" + URLEncoder.encode(place[0], "utf-8");
                input = "input=" + URLEncoder.encode(input, "utf-8");
                System.out.println("input String : " + input);
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "language=en";

            /*// Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;*/


            // Building the parameters to the web service

            String parameters = input + "&" + sensor + "&" + key + "&components=country:us";


            // Output format
            String output = "json";
            //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=bangkok&types=geocode&language=en&key=AIzaSyC7Ibf8bOuhEJMSFBS9Qz2z6SicM7qxHDY
            // Building the url to the web service
//            13.758662, 100.496443
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
                    + output + "?" + parameters;

            System.out.println("Final URL Google API : " + url);
//            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/location=13.758662,100.496443" + output + "?" + parameters;

            try {
                // Fetching the data from we service
                data = downloadUrlsearch(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());

            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


//            callPhotoAPI();


            // Creating ParserTask
            searchparserTask = new SearchParserTask();

            // Starting Parsing the JSON string returned by Web Service
            searchparserTask.execute(result);


        }
    }

    /** AUTO SEARCH CODE  */
    /**
     * A method to download json data from url
     */
    private String downloadUrlsearch(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {

            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();


            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            System.out.println("data: " + data);

            br.close();


        } catch (Exception e) {

            e.printStackTrace();
//            Log.d("Exception while downloading url", e.toString());
        } finally {

            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class SearchParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            System.out.println("SearchParserTask Called");
            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
           /* for (int i = 0; i < myAreasList.size(); i++) {
                HashMap<String, String> hm = new HashMap<String, String>();
                MyAreasBean bean = myAreasList.get(i);
                hm.put("loc", bean.getLoc_name());
                result.add(hm);
            }*/

            try {

                String[] from = {"description"};
                int[] to = {R.id.listtext};
                // Instantiating an adapter to store each items
                // R.layout.listview_layout defines the layout of each item

                if (result != null) {
                    SimpleAdapter adapter = new SimpleAdapter(getActivity(),

                            result, R.layout.landing_resource_row, from, to) {
                    };

                    search_et.setAdapter(adapter);
                } else {
                    Constant.showToast("Server Error ", getActivity());
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Constant.showToast("Server Error ", getActivity());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }


        }
    }

    public class PlaceJSONParser {

        /**
         * Receives a JSONObject and returns a list
         */
        public List<HashMap<String, String>> parse(JSONObject jObject) {

            JSONArray jPlaces = null;
            try {
                /** Retrieves all the elements in the 'places' array */
                jPlaces = jObject.getJSONArray("predictions");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            /** Invoking getPlaces with the array of json object
             * where each json object represent a place
             */
            return getPlaces(jPlaces);
        }

        private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
            int placesCount = jPlaces.length();
            List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> place = null;

            /** Taking each place, parses and adds to list object */
            for (int i = 0; i < placesCount; i++) {
                try {
                    /** Call getPlace with place JSON object to parse the place */
                    place = getPlace((JSONObject) jPlaces.get(i));
                    placesList.add(place);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return placesList;
        }

        /**
         * Parsing the Place JSON object
         */
        private HashMap<String, String> getPlace(JSONObject jPlace) {

            HashMap<String, String> place = new HashMap<String, String>();

            String id = "";
            String reference = "";
            String description = "";

            try {

                description = jPlace.getString("description");
                id = jPlace.getString("id");
                reference = jPlace.getString("reference");

                place.put("description", description);
                place.put("_id", id);
                place.put("reference", reference);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return place;
        }
    }


}