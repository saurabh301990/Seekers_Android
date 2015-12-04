package com.tv.seekers.menu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.tv.seekers.activities.AddFollowedActivity;
import com.tv.seekers.activities.FilterActivity;
import com.tv.seekers.adapter.HomeListAdapter;
import com.tv.seekers.adapter.LandingAdapter;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
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

import butterknife.OnClick;

/**
 * Created by shoeb on 3/11/15.
 */
public class MapView extends Fragment {

    //map Related
    private GoogleMap googleMap;
    CameraPosition cameraPosition;
    private SupportMapFragment fragment;
    Marker marker;
    private double latitude = 0.0;
    private double longitude = 0.0;
    private ArrayList<HomeBean> _mainList = new ArrayList<HomeBean>();


    LatLng _latLong;
    GPSTracker gps;
    Circle mapCircle;

    @Bind(R.id.two_miles_btn)
    Button two_miles_btn;

    @OnClick(R.id.two_miles_btn)
    public void two_miles_btn(View view) {
        activeMilesBtn(2);
        _radiusForWS = "2";
        callGetAllPostsWS(_radiusForWS);
    }

    @Bind(R.id.five_miles_btn)
    Button five_miles_btn;

    @OnClick(R.id.five_miles_btn)
    public void five_miles_btn(View view) {
        if (view.getId() == R.id.five_miles_btn) {
            activeMilesBtn(5);
            _radiusForWS = "5";
            callGetAllPostsWS(_radiusForWS);

        }
    }

    @Bind(R.id.ten_miles_btn)
    Button ten_miles_btn;
    private String _radiusForWS = "2";

    @OnClick(R.id.ten_miles_btn)
    public void ten_miles_btn(View view) {
        if (view.getId() == R.id.ten_miles_btn) {

            activeMilesBtn(10);
            _radiusForWS = "10";
            callGetAllPostsWS(_radiusForWS);
        }
    }

    @Bind(R.id.twenty_miles_btn)
    Button twenty_miles_btn;

    @OnClick(R.id.twenty_miles_btn)
    public void twenty_miles_btn(View view) {
        if (view.getId() == R.id.twenty_miles_btn) {
            activeMilesBtn(20);
            _radiusForWS = "20";
            callGetAllPostsWS(_radiusForWS);

        }
    }

    @Bind(R.id.map_btn)
    Button map_btn;


    @Bind(R.id.search_et)
    EditText search_et;

    @OnClick(R.id.map_btn)
    public void map_btn(View view) {
        if (view.getId() == R.id.map_btn) {
            map_layout.setVisibility(View.GONE);
            map_layout.setVisibility(View.VISIBLE);
            map_btn.setBackgroundColor(Color.WHITE);
            list_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
            header.setText("Map");
            list_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
            map_btn.setTextColor(Color.BLACK);
            list_layout.setVisibility(View.GONE);

            _isList = false;
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
            header.setText("List");
            map_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
            list_btn.setTextColor(Color.BLACK);

            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                _isList = true;
                callGetAllPostsWS(_radiusForWS);
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }
            list_layout.setVisibility(View.VISIBLE);
        }
    }


    @Bind(R.id.map_layout)
    LinearLayout map_layout;


    @Bind(R.id.list_layout)
    RelativeLayout list_layout;

    @Bind(R.id.listView_home)
    ListView listView_home;
    HomeListAdapter adapterList;

    private boolean _isList = false;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private TextView header;
    private ImageView rightIcon;
    SharedPreferences sPref;
    private String user_id = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_view_screen, container, false);
        ButterKnife.bind(this, view);

        header = (TextView) getActivity().findViewById(R.id.hdr_title);
        setfont();
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

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
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_et.getText().length() == 0) {
                    search_et.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.search_icon, 0, 0, 0);
                }

            }
        });

        rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setImageResource(R.drawable.filtr);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), FilterActivity.class));

            }
        });

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetAllPostsWS(_radiusForWS);
        } else {
            Constant.showToast(getResources().getString(R.string.internet), getActivity());
        }


        return view;
    }

    private void callGetAllPostsWS(final String radius) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("radius", radius)
                        .appendQueryParameter("source", "1,2,3,4");

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

                            String query = builder.build().getEncodedQuery();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_ALL_POSTS));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);


                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(5000);
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

                            if (_mainList.size() > 0) {
                                _mainList.clear();
                            }
                            JSONObject _jObject = jsonObject.getJSONObject("social_post");
                            JSONArray _resultJSONArray = _jObject.getJSONArray("result");
                            if (_resultJSONArray.length() > 0) {
                                int length = _resultJSONArray.length();

                                for (int i = 0; i < length; i++) {
                                    JSONObject _jSubObject = _resultJSONArray.getJSONObject(i);
                                    HomeBean bean = new HomeBean();
                                    bean.setPost_lat(_jSubObject.getString("post_lat"));
                                    bean.setPost_long(_jSubObject.getString("post_long"));
                                    bean.setPost_text(_jSubObject.getString("post_text"));
                                    bean.setSource(_jSubObject.getString("source"));
                                    bean.setUser_address(_jSubObject.getString("user_address"));
                                    bean.setUser_image(_jSubObject.getString("user_image"));
                                    _mainList.add(bean);

                                }

                                if (_isList) {
                                    //todo setting Adapter here
                                    adapterList = new HomeListAdapter(_mainList, getActivity());
                                    listView_home.setAdapter(adapterList);

                                } else {

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
                                }


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


    private void init() {
        gps = new GPSTracker(getActivity());


        if (gps.canGetLocation()) {
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
     */
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
      /*  _latLong = new LatLng(latitude, longitude);
        cameraPosition = new CameraPosition.Builder().target(_latLong)
                .zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (marker != null) {
            marker.remove();
        }
        marker = googleMap.addMarker(new MarkerOptions()
                .position(_latLong).icon(BitmapDescriptorFactory.
                        defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/
        int _length = _mainList.size();
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
                BitmapDescriptor bitmapMarker;
                bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                googleMap.addMarker(new MarkerOptions().position(ll)
                        .title("")
                        .icon(bitmapMarker));


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
                        fillColor(0x55fe9f9f);
        // Fill color of the circle
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
}
