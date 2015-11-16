package com.tv.seekers.menu;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.utils.HttpConnection;
import com.tv.seekers.utils.PathJSONParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by shoeb on 4/11/15.
 */
public class TrackMapFragment extends Fragment {


    private static final LatLng LOWER_MANHATTAN = new LatLng(22.7176622,
            75.8685777);
    private static final LatLng BROOKLYN_BRIDGE = new LatLng(22.728874, 75.8732923);
    private static final LatLng WALL_STREET = new LatLng(22.738021, 75.9088663);
    GoogleMap googleMap;
    private SupportMapFragment fragment;
    final String TAG = "TrackMapFragment";
    ArrayList<LatLng> markerPoints;

    @Bind(R.id.img)
    ImageView img;

    @Bind(R.id.name_info_tv)
    TextView name_info_tv;

    @Bind(R.id.loc_info_tv)
    TextView loc_info_tv;

    @Bind(R.id.name_tv)
    TextView name_tv;

    @Bind(R.id.loc_tv)
    TextView loc_tv;

    @Bind(R.id.map_btn)
    Button map_btn;


    TextView _header;

    @OnClick(R.id.map_btn)
    public void map_btnClick(View view) {
        map_view.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        list_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
        map_btn.setBackgroundColor(Color.WHITE);

        map_btn.setTextColor(Color.BLACK);
        list_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));
    }

    @Bind(R.id.list_btn)
    Button list_btn;

    @OnClick(R.id.list_btn)
    public void list_btnClick(View view) {
        map_view.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        map_btn.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.miles_inactive_color));
        list_btn.setBackgroundColor(Color.WHITE);

        list_btn.setTextColor(Color.BLACK);
        map_btn.setTextColor(ContextCompat.getColor(getActivity(), R.color.grey_color));

    }

    @Bind(R.id.map_view)
    RelativeLayout map_view;

    @Bind(R.id.list)
    ListView list;

    public void setfont() {
        Constant.setFont(getActivity(), name_info_tv, 0);
        Constant.setFont(getActivity(), name_tv, 0);
        Constant.setFont(getActivity(), loc_info_tv, 0);
        Constant.setFont(getActivity(), loc_tv, 0);
        Constant.setFont(getActivity(), map_btn, 0);
        Constant.setFont(getActivity(), list_btn, 0);
        Constant.setFont(getActivity(), _header, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.track_map_screen, container, false);

        ButterKnife.bind(this, view);
        setfont();
        _header = (TextView) getActivity().findViewById(R.id.hdr_title);
        _header.setText("Followed Profile");
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("Get Promo", "keyCode: " + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    Log.i("Get Promo", "onKey Back listener is working!!!");
                    getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
        // Initializing
        markerPoints = new ArrayList<LatLng>();
        markerPoints.add(0, LOWER_MANHATTAN);
        markerPoints.add(1, BROOKLYN_BRIDGE);
        markerPoints.add(2, WALL_STREET);
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_view);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_view, fragment).commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initMap();
    }

    private void initMap() {
        if (googleMap == null) {
            googleMap = fragment.getMap();

            MarkerOptions options = new MarkerOptions();
            options.position(LOWER_MANHATTAN);
            options.position(BROOKLYN_BRIDGE);
            options.position(WALL_STREET);
            googleMap.addMarker(options);

            addMarkers();
//            String url = getMapsApiDirectionsUrl();
//            String url ="https://maps.googleapis.com/maps/api/directions/json?origin=22.7176622,75.8685777&destination=22.738021,75.9088663&%20waypoints=optimize:true|22.7176622,75.8685777|22.728874,75.8732923|22.738021,75.9088663&sensor=false";

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(LOWER_MANHATTAN, WALL_STREET);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BROOKLYN_BRIDGE,
                    13));

        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";

        waypoints = "waypoints=";
        waypoints += BROOKLYN_BRIDGE.latitude + "," + BROOKLYN_BRIDGE.longitude + "|";


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String getMapsApiDirectionsUrl() {

        String waypoints = "origin=" + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "&destination=" + WALL_STREET.latitude + "," + WALL_STREET.longitude + "%20"
                + "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;


//        https://maps.googleapis.com/maps/api/directions/json?origin
        // =-23.3246,-51.1489&destination=-23.2975,-51.2007&%20waypoints=optimize:true|
        // -23.3246,-51.1489|-23.3206,-51.1459|-23.2975,-51.2007&sensor=false
        return url;
    }

    private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLACK);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }
}
