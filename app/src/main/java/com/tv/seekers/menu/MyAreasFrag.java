package com.tv.seekers.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.tv.seekers.R;
import com.tv.seekers.adapter.MyAreaAdapter;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 21/11/15.
 */
public class MyAreasFrag extends Fragment implements
        AdapterView.OnItemClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.my_area_grid)
    GridView my_area_grid;

    @Bind(R.id.search_et)
    AutoCompleteTextView search_et;

    @Bind(R.id.search_iv)
    ImageView search_iv;

    private MyAreaAdapter areasAdapter;
    private ArrayList<MyAreasBean> myAreasList = new ArrayList<MyAreasBean>();

    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private String user_id = "";
    private String loc_ids = "";
    private boolean isDrawOption = false;
    private SearchParserTask searchparserTask;
    private SearchPlacesTask searchplacesTask;
    private List<Address> From_geocode = null;
    private double _lat = 0.0;
    private double _long = 0.0;
    private String _address = "";
    private String input = "";
    private TextView header;

    private int checkedCountTotal = 0;
    GPSTracker gps;
    GoogleApiClient mGoogleApiClient;
    FragmentActivity activity = null;

    //todo DataBase
    DB mDataBase;

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_areas_screen, container, false);
//        ErrorReporter.getInstance().Init(getActivity());
        gps = new GPSTracker(getActivity());
        ButterKnife.bind(this, view);
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        user_id = sPref.getString("id", "");

//        System.out.println("Session ID : " +sPref.getString(Constant.Cookie, ""));

        setFont();
        creatingDB();
        activity = getActivity();


        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callsavedLocationWS();
        } else {
            try {
                JSONArray data = mDataBase.getObject("data", JSONArray.class);
                System.out.println("JSON ARRAY length : " + data.length());

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
                        String loc_name = jsonobj.getString("loc_name");
                        String loc_address = jsonobj.getString("loc_address");
                        String userlat = jsonobj.getString("loc_lat");
                        String userlong = jsonobj.getString("loc_long");
                        String loc_radius = jsonobj.getString("loc_radius");
                        String id = jsonobj.getString("id");
                        String loc_image = jsonobj.getString("loc_image");

                        MyAreasBean bean = new MyAreasBean();
                        bean.setLoc_name(loc_name);
                        bean.setLoc_add(loc_address);
                        bean.set_lat(userlat);
                        bean.set_long(userlong);
                        bean.setId(id);
                        bean.setImg_url(loc_image);


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
            } catch (Exception e) {
                e.printStackTrace();
            }


            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }

        my_area_grid.setOnItemClickListener(this);
        my_area_grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
        my_area_grid.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                System.out.println("onPrepareActionMode");
                // TODO Auto-generated method stub
                mode.setTitle("onPrepareActionMode");
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

                System.out.println("onDestroyActionMode");
                // TODO Auto-generated method stub
               /* if (myAreasList.size()>0){
                    for (int i = 0 ; i< myAreasList.size();i++){
                        MyAreasBean selectedItem = myAreasList.get(i);
                        selectedItem.setIsSelected(false);
                        myAreasList.add(i,selectedItem);
                    }
                }*/
                areasAdapter.removeSelection();
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                System.out.println("onCreateActionMode");
                mode.getMenuInflater().inflate(R.menu.delete_action_mode, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete_mode:
                        SparseBooleanArray selected = areasAdapter.getSelectedIds();
                        String prefix = "id=";
                        StringBuilder sb = new StringBuilder();

                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                MyAreasBean selectedItem = myAreasList.get(selected.keyAt(i));
                                loc_ids = selectedItem.getId();


                                sb.append(prefix);
                                prefix = "&id=";
                                sb.append(loc_ids);

                                myAreasList.remove(selectedItem);
                                areasAdapter.notifyDataSetChanged();

                            }
                        }
                        loc_ids = sb.toString();
                        System.out.println("Location ids : " + loc_ids);

                        try {

                            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                                calldeleteSaveLocationWS();
                            } else {
                                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                                  boolean checked) {

                if (isDrawOption) {
                    if (position == 0) {

                    } else {
                        int checkedCount = my_area_grid.getCheckedItemCount();
                        mode.setTitle(checkedCount + " selected");


                        MyAreasBean bean = myAreasList.get(position);
                        bean.setIsSelected(checked);

                        areasAdapter.toggleSelection(position);
                    }
                } else {
                    int checkedCount = my_area_grid.getCheckedItemCount();
                    mode.setTitle(checkedCount + " selected");


                    MyAreasBean bean = myAreasList.get(position);
                    bean.setIsSelected(checked);

                    areasAdapter.toggleSelection(position);
                }


            }
        });
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

             /*   searchplacesTask = new SearchPlacesTask();
                searchplacesTask.execute();*/
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
//                gps = new GPSTracker(getActivity());


                try {

//                    MyAreasBean _bean = myAreasList.get(position);

                    String _currentLoc = search_et.getText().toString();
                    _currentLoc = _currentLoc.replaceAll("\\{", "");
                    _currentLoc = _currentLoc.replaceAll("\\}", "");
                    System.out.println("Current Location from Autocomplete : " + _currentLoc);
                    String[] currentLocArray = _currentLoc.split("=");
                    System.out.println("Current Location from Autocomplete FINAL: " + currentLocArray[1]);
                    search_et.setText(currentLocArray[1] + "");
                    for (int i = 0; i < myAreasList.size(); i++) {
                        MyAreasBean bean = myAreasList.get(i);
                        String savedLoc = bean.getLoc_name();
                        if (savedLoc.equalsIgnoreCase(currentLocArray[1])) {
                            position = i;
                            break;
                        }
                    }

                    saveCurrentLatLong(position);
                    replaceFragment();

                   /* HashMap<String, String> hm = (HashMap<String, String>)
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
*/

                } catch (Exception e) {
                    e.printStackTrace();
                    Constant.showToast("Server Error ", getActivity());
                }


            }
        });

// TODO: 9/12/15 Checking first time or not.
        try {
            isDrawOption = getArguments().getBoolean("isDrawOption");
            header = (TextView) getActivity().findViewById(R.id.hdr_title);


            if (isDrawOption) {
                header.setText("Saved Areas");
            } else {
                header.setText("Choose Location");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

                               /* if (_lat != 0.0 && _long != 0.0) {
                                    if (String.valueOf(_lat).equalsIgnoreCase(_latUser)
                                            && String.valueOf(_long).equalsIgnoreCase(_longUser)) {
                                        //LatLng Matched
                                        _isLocationAlreadyAdded = true;
                                        position = j;
                                        break;
                                    } else {
                                        _isLocationAlreadyAdded = false;
                                    }
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

    private void calldeleteSaveLocationWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

             /*   builder = new Uri.Builder()
                        .appendQueryParameter("location_id", loc_ids);*/


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.DELETE_SAVE_LOCATION) + "?" + loc_ids);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of deleteSaveLocation : " + _responseMain);

                            } else {
                                Log.v("My area", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
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

                            Constant.showToast("Location deleted", getActivity());

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", getActivity());
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

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
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
                            search_et.setText("");

                            // TODO: 23/2/16 Fetching Array List of latlng

//                            Constant.showToast(jsonObject.getString("message"), getActivity());
                            if (!isDrawOption) {
                                // TODO: 10/12/15 Redirect to Home Screen
                                // TODO: 10/12/15  Replace Fragment here with Home Fragment
                                MyAreasBean bean = new MyAreasBean();
                                bean.setIsSelected(false);
                                JSONObject mJsonObjectData = jsonObject.getJSONObject("data");
                                String loc_id = mJsonObjectData.getString("id");
                                String userLocationType = mJsonObjectData.getString("userLocationType");
                                bean.setType(userLocationType);
                                bean.setId(loc_id);

                                bean.setLoc_name(_address);
                                bean.setLoc_add(_address);
                                bean.set_long(_long + "");
                                bean.set_lat(_lat + "");
                                bean.setImg_url(finalimgUrl);
                                myAreasList.add(bean);
                                saveCurrentLatLong(myAreasList.size() - 1);
                                replaceFragment();
                            } else {
                                if (myAreasList.size() == 0) {

                                    MyAreasBean beanDemo = new MyAreasBean();
                                    beanDemo.setLoc_name("");
                                    beanDemo.setLoc_add("");
                                    myAreasList.add(beanDemo);
                                }
                                MyAreasBean bean = new MyAreasBean();
                                bean.setIsSelected(false);
                                JSONObject mJsonObjectData = jsonObject.getJSONObject("data");
                                String loc_id = mJsonObjectData.getString("id");
                                String userLocationType = mJsonObjectData.getString("userLocationType");
                                bean.setType(userLocationType);
                                bean.setId(loc_id);
                                bean.setLoc_name(_address);
                                bean.setLoc_add(_address);
                                bean.set_long(_long + "");
                                bean.set_lat(_lat + "");
                                bean.setImg_url(finalimgUrl);
                                myAreasList.add(bean);
                                if (areasAdapter != null) {
                                    areasAdapter.notifyDataSetChanged();
                                } else {
                                    areasAdapter = new MyAreaAdapter(myAreasList, getActivity(), isDrawOption);
                                    my_area_grid.setAdapter(areasAdapter);
                                }

                                // TODO: 15/12/15 Add to List
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
                ;
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

    String finalimgUrl = "";

    private class PlaceDetails extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("PlaceDetails Called");

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


    private final static String TAG = "MyAreasFragment";

    private void callPhotoAPI() {

        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {


            @Override
            protected void onPreExecute() {


            }

            @Override
            protected String doInBackground(String... arg0) {

                String placeId = "ChIJN1t_tDeuEmsRUsoyG83frY4";

                Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult placePhotoMetadataResult) {
                        if (placePhotoMetadataResult.getStatus().isSuccess()) {
                            PlacePhotoMetadataBuffer photoMetadata = placePhotoMetadataResult.getPhotoMetadata();
                            int photoCount = photoMetadata.getCount();
                            for (int i = 0; i < photoCount; i++) {
                                PlacePhotoMetadata placePhotoMetadata = photoMetadata.get(i);
                                final String photoDetail = placePhotoMetadata.toString();
                                placePhotoMetadata.getScaledPhoto(mGoogleApiClient, 500, 500).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                    @Override
                                    public void onResult(PlacePhotoResult placePhotoResult) {
                                        if (placePhotoResult.getStatus().isSuccess()) {
                                            Log.i(TAG, "Photo " + photoDetail + " loaded");
                                        } else {
                                            Log.e(TAG, "Photo " + photoDetail + " failed to load");
                                        }
                                    }
                                });
                            }
                            photoMetadata.release();
                        } else {
                            Log.e(TAG, "No photos returned");
                        }
                    }
                });
                return null;

            }

            @Override
            protected void onPostExecute(String result) {
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            _Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
        } else {
            _Task.execute((String[]) null);
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
                Constant.showToast("Server Error ", getActivity());
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
            //            Uri.Builder builder;
            JSONObject mJsonObjectUser = new JSONObject();
            JSONObject mJsonObjectUserID = new JSONObject();

            @Override
            protected void onPreExecute() {


                Constant.showLoader(getActivity());

                try {
               /*     mJsonObjectUserID.put("id", user_id);
                    mJsonObjectUser.put("user", mJsonObjectUserID);*/
                    mJsonObjectUser.put("pageNo", 1);
                    mJsonObjectUser.put("limit", 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id);*/

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

                            if (jsonObject.has("data")) {

                                JSONArray data = jsonObject.getJSONArray("data");
                                if (mDataBase.isOpen()) {
                                    mDataBase.put("data", data);
                                }
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

    private void creatingDB() {
        try {
            mDataBase = DBFactory.open(getActivity(), "SeekersDB");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (isDrawOption) {
            if (position == 0) {
                // TODO: 9/12/15 Replace Fragment with Plot New Area Fragment
//            Constant.showToast("New ACtivity", getActivity());
                replaceFragmentPlotArea();
            } else {
                saveCurrentLatLong(position);
                replaceFragment();
            }
        } else {
            saveCurrentLatLong(position);
            replaceFragment();
        }

    }

    private void saveCurrentLatLong(int position) {
        try {
            MyAreasBean bean = myAreasList.get(position);
            System.out.println("Current Location from Autocomplete after position: " + bean.getLoc_name());
            System.out.println("Current Location from Autocomplete after position: POS" + position);
            System.out.println("Current Location from Autocomplete after position: LAT" + bean.get_lat());
            editor.putString("LATITUDE", bean.get_lat());
            editor.putString("LONGITUDE", bean.get_long());
            editor.putString("userLocationType", bean.getType());
            editor.putString("LOCATIONID", bean.getId());
            editor.commit();
            if (MapView.arrayPoints.size() > 0) {
                MapView.arrayPoints.clear();
            }
            MapView.arrayPoints = bean.getAreaLatLng();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void replaceFragmentPlotArea() {
        DemoMapFrag fragment = new DemoMapFrag();
        if (fragment != null) {


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();


        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constant.hideKeyBoard(getActivity());
    }
}
