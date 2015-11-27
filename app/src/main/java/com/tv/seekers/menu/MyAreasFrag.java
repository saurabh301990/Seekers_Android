package com.tv.seekers.menu;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.tv.seekers.utils.CustomAutoCompletetextview;
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

//        search_et.setThreshold(1);
      /*  search_et.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.hideKeyBoard(getActivity());
            }
        });*/
        /*search_et.addTextChangedListener(new TextWatcher() {
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
                *//**
                 * Coding for smart Search
                 *//*

            }
        });*/


        return view;
    }

    private void setFont() {
        Constant.setFont(getActivity(), search_et, 0);
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


                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_LOC));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);


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

                            if (jsonObject.has("user_locations")) {

                                JSONArray user_locations = jsonObject.getJSONArray("user_locations");
                                if (user_locations.length() > 0) {
                                    if (myAreasList.size() > 0) {
                                        myAreasList.clear();
                                    }
                                    MyAreasBean beanDemo = new MyAreasBean();
                                    beanDemo.setLoc_name("");
                                    beanDemo.setLoc_add("");
                                    myAreasList.add(beanDemo);
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

                                        myAreasList.add(bean);


                                    }

                                    areasAdapter = new MyAreaAdapter(myAreasList, getActivity());
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
                                            aList, R.layout.landing_resource_row, from, to) {};
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
        if (position == 0) {
//            Constant.showToast("New ACtivity", getActivity());
        }
    }
}
