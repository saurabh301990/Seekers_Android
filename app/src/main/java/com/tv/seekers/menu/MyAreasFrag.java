package com.tv.seekers.menu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.LandingAdapter;
import com.tv.seekers.adapter.MyAreaAdapter;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.bean.MyAreasBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
import com.tv.seekers.utils.CustomAutoCompletetextview;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 21/11/15.
 */
public class MyAreasFrag extends Fragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.my_area_grid)
    GridView my_area_grid;

    @Bind(R.id.search_et)
    CustomAutoCompletetextview search_et;

    @Bind(R.id.search_iv)
    ImageView search_iv;

    private MyAreaAdapter areasAdapter;
    private ArrayList<MyAreasBean> myAreasList = new ArrayList<MyAreasBean>();

    private SharedPreferences sPref;
    private String user_id = "";
    private boolean isDrawOption = false;
    private SearchParserTask searchparserTask;
    private SearchPlacesTask searchplacesTask;
    private List<Address> From_geocode = null;
    private double _lat = 0.0;
    private double _long = 0.0;
    private String _address = "";
    GPSTracker gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_areas_screen, container, false);


        ButterKnife.bind(this, view);
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

        setFont();

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callsavedLocationWS();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }

        my_area_grid.setOnItemClickListener(this);

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
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_et.getText().length() == 0) {
                    search_iv.setVisibility(View.VISIBLE);
                }
                searchplacesTask = new SearchPlacesTask();
                searchplacesTask.execute(s.toString());

            }
        });

        search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.hideKeyBoard(getActivity());
                gps = new GPSTracker(getActivity());

                boolean _isLocationAlreadyAdded = false;
                try {
                    Geocoder geocoder = new Geocoder(getActivity());
                    HashMap<String, String> hm = (HashMap<String, String>)
                            parent.getAdapter().getItem(position);
                    String _locName = hm.get("description");
                    System.out.println("_locName : " + _locName);
                    From_geocode = geocoder.getFromLocationName(
                            _locName, 1);
                    search_et.setText(hm.get("description"));
                    _address = search_et.getText().toString();

                    if (From_geocode.size() > 0) {
                        From_geocode.get(0).getLatitude(); // getting // latitude
                        From_geocode.get(0).getLongitude();


                        _lat = From_geocode.get(0).getLatitude();
                        _long = From_geocode.get(0).getLongitude();

                        System.out.println("LatLng : " + _lat + ", " + _long);


                    }

                    if (myAreasList.size() > 0) {
                        for (int j = 0; j < myAreasList.size(); j++) {
                            MyAreasBean _bean = myAreasList.get(j);
                            String _add = _bean.getLoc_add();
                            String _latUser = _bean.get_lat();
                            String _longUser = _bean.get_long();

                            if (_lat != 0.0 && _long != 0.0) {
                                if (String.valueOf(_lat).equalsIgnoreCase(_latUser)
                                        && String.valueOf(_long).equalsIgnoreCase(_longUser)) {
                                    //LatLng Matched
                                    _isLocationAlreadyAdded = true;
                                    break;
                                } else {
                                    _isLocationAlreadyAdded = false;
                                }
                            } else {
                                _isLocationAlreadyAdded = false;
                            }

                         /*   if (_add.equalsIgnoreCase(_address)) {
                                //location already added.
                                _isLocationAlreadyAdded = true;
                                break;
                            } else {
                                _isLocationAlreadyAdded = false;
                            }*/
                        }
                    }


                    if (_isLocationAlreadyAdded) {
                        search_et.setText("");
                        // TODO: 10/12/15 Redirect to Home screen with current LatLong

                        if (!isDrawOption) {
                            // TODO: 10/12/15 Redirect to Home Screen
                            // TODO: 10/12/15  Replace Fragment here with Home Fragment
                            replaceFragment();
                        }
                    } else {
                        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                            callSaveUserLocation();
                        } else {
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Constant.showToast("Server Error ", getActivity());
                }


            }
        });

// TODO: 9/12/15 Checking first time or not.
        try {
            isDrawOption = getArguments().getBoolean("isDrawOption");

        } catch (Exception e) {
            e.printStackTrace();
        }

        gps = new GPSTracker(getActivity());
        return view;
    }

    private void callSaveUserLocation() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("loc_lat", String.valueOf(_lat))
                        .appendQueryParameter("loc_long", String.valueOf(_long))
                        .appendQueryParameter("loc_radius", "5");


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        String query = builder.build().getEncodedQuery();
                        //			String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.USER_SAVE_LOCATION));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setReadTimeout(30000);


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

                            Constant.showToast(jsonObject.getString("message"), getActivity());
                            if (!isDrawOption) {
                                // TODO: 10/12/15 Redirect to Home Screen
                                // TODO: 10/12/15  Replace Fragment here with Home Fragment
                                replaceFragment();
                            }
                        } else {
                            Constant.showToast(jsonObject.getString("message"), getActivity());
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

    private void setFont() {
        Constant.setFont(getActivity(), search_et, 0);
    }


    private class SearchPlacesTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console

            String key = "key=AIzaSyDEkwu2c89RobN7jfQ1nvoyIAC6Gt4FWpI";


            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
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
                SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                        result, R.layout.landing_resource_row, from, to) {
                };

                search_et.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
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

    private void callsavedLocationWS() {


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
                        //			String temp=URLEncoder.encode(uri, "UTF-8");
                        URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_LOC));
                        urlConnection = (HttpURLConnection) ((url.openConnection()));
                        urlConnection.setDoInput(true);
                        urlConnection.setDoOutput(true);
                        urlConnection.setUseCaches(false);
                        urlConnection.setChunkedStreamingMode(1024);
                        urlConnection.setReadTimeout(30000);


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

                            if (jsonObject.has("user_locations")) {

                                JSONArray user_locations = jsonObject.getJSONArray("user_locations");
                                if (user_locations.length() > 0) {
                                    if (myAreasList.size() > 0) {
                                        myAreasList.clear();
                                    }
                                    if (isDrawOption) {
                                        MyAreasBean beanDemo = new MyAreasBean();
                                        beanDemo.setLoc_name("");
                                        beanDemo.setLoc_add("");
                                        myAreasList.add(beanDemo);
                                    }

                                    for (int i = 0; i < user_locations.length(); i++) {
                                        JSONObject jsonobj = user_locations.getJSONObject(i);
                                        String loc_name = jsonobj.getString("loc_name");
                                        String loc_address = jsonobj.getString("loc_address");
                                        String userlat = jsonobj.getString("loc_lat");
                                        String userlong = jsonobj.getString("loc_long");
                                        String loc_radius = jsonobj.getString("loc_radius");
                                        String id = jsonobj.getString("id");

                                        MyAreasBean bean = new MyAreasBean();
                                        bean.setLoc_name(loc_name);
                                        bean.setLoc_add(loc_address);
                                        bean.set_lat(userlat);
                                        bean.set_long(userlong);

                                        myAreasList.add(bean);


                                    }

                                    areasAdapter = new MyAreaAdapter(myAreasList, getActivity(), isDrawOption);
                                    my_area_grid.setAdapter(areasAdapter);

                                    /** Setting data for Auto Complete
                                     *
                                     */

                                   /* AreaAdapterAutoComplete adapter = new AreaAdapterAutoComplete(getActivity(),
                                            R.layout.landing_resource_row, R.id.listtext, myAreasList);
                                    search_et.setAdapter(adapter);*/

                                    // Each row in the list stores country name, currency and flag
                                    List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
                                    for (int i = 0; i < myAreasList.size(); i++) {
                                        HashMap<String, String> hm = new HashMap<String, String>();
                                        MyAreasBean bean = myAreasList.get(i);
                                        hm.put("loc", bean.getLoc_name());
                                        aList.add(hm);
                                    }

                                    String[] from = {"loc"};
                                    int[] to = {R.id.listtext};
                                    // Instantiating an adapter to store each items
                                    // R.layout.listview_layout defines the layout of each item
                                    SimpleAdapter adapter = new SimpleAdapter(getActivity(),
                                            aList, R.layout.landing_resource_row, from, to) {
                                    };
                                   /*     @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View v = super.getView(position, convertView, parent);

                                            TextView _tvData = (TextView) v.findViewById(R.id.listtext);
                                            Constant.setFont(getActivity(), _tvData, 0);


                                            return v;
                                        }
                                    };*//*


                                    *//** Setting the adapter to the listView */
                                    search_et.setAdapter(adapter);


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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (isDrawOption) {
            if (position == 0) {
                // TODO: 9/12/15 Replace Fragment with Plot New Area Fragment
//            Constant.showToast("New ACtivity", getActivity());
            }
        }

    }
}
