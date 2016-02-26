package com.tv.seekers.menu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.activities.AddFollowedActivity;
import com.tv.seekers.adapter.TrackAdapter;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
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
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class Track extends Fragment implements XListView.IXListViewListener {


    @Nullable
    private ArrayList<TrackBean> userlist = new ArrayList<TrackBean>();
    TrackBean trackBean;
    TrackAdapter trackAdapter;


    @Bind(R.id.search_et)
    EditText search_et;

    @Bind(R.id.lvtrack)
    XListView listuser;
    private int _page_number = 1;

    private SharedPreferences sPref;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        Constant.hideKeyBoard(getActivity());
    }

    private TextView _header;
    private ImageView rightIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.track, container, false);
        ButterKnife.bind(this, view);
//        ErrorReporter.getInstance().Init(getActivity());
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);

        System.out.println("onCreateView Called");

        _header = (TextView) getActivity().findViewById(R.id.hdr_title);
        _header.setText("Saved Profiles");
        rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setVisibility(View.VISIBLE);
        rightIcon.setImageResource(R.mipmap.plus);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getActivity(), AddFollowedActivity.class));

            }
        });


        addData();


        //Load More
        listuser.setSelector(android.R.color.transparent);
        listuser.setXListViewListener(this);
        listuser.setPullRefreshEnable(true);
        listuser.setPullLoadEnable(false);
        listuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                replaceFragment(position-1);
            }
        });

        setFont();

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

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetFollowedUsers();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }
    }

    private void callGetFollowedUsers() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                Constant.showLoader(getActivity());

                try {
                    mJsonObject.put("pageNo", _page_number);
                    mJsonObject.put("limit", 20);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of GET_FOLLOWED_USERS : " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_FOLLOWED_USERS));
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
                            System.out.println("Response of GET_FOLLOWED_USERS : " + _responseMain);


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
                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {
                        JSONObject mJsonObject = new JSONObject(_responseMain);
                        if (mJsonObject.has("status")) {
                            int mStatus = mJsonObject.getInt("status");
                            if (mStatus == 1) {
                                if (mJsonObject.has("data")) {


                                    JSONArray mData = mJsonObject.getJSONArray("data");
                                    if (mData.length() > 0) {

                                        if (userlist.size() > 0) {
                                            userlist.clear();
                                        }
                                        for (int i = 0; i < mData.length(); i++) {
                                            JSONObject mSubJsonObject = mData.getJSONObject(i);
                                            TrackBean bean = new TrackBean();
                                            String id = mSubJsonObject.getString("id");
                                            bean.setId(id);
                                            String username = mSubJsonObject.getString("username");
                                            bean.setUsername(username);
                                            JSONObject _picJsonObject = mSubJsonObject.getJSONObject("profilePic");

                                            String userImgUrl = "";
                                            String mSmall = _picJsonObject.getString("small");
                                            String medium = _picJsonObject.getString("medium");
                                            String large = _picJsonObject.getString("large");
                                            if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                                userImgUrl = mSmall;
                                            } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                                userImgUrl = medium;
                                            } else if (large != null && !large.equalsIgnoreCase("")) {
                                                userImgUrl = large;
                                            }
                                            bean.setImageURL(userImgUrl);


                                            String follower_count = mSubJsonObject.getString("follower_count");
                                            bean.setUserfollowed(follower_count);

                                            userlist.add(bean);
                                        }

                                        if (userlist.size() > 0) {
                                            listuser.stopRefresh();
                                            //Set Adapter
                                            trackAdapter = new TrackAdapter(userlist, getActivity());
                                            listuser.setAdapter(trackAdapter);
                                        } else {
                                            Constant.showToast("No users found!", getActivity());
                                        }

                                    } else {
                                        Constant.showToast("No users found!", getActivity());
                                    }

                                } else {
                                    Constant.showToast("Server Error    ", getActivity());
                                }


                                if (mJsonObject.has("isMore")) {
                                    String _is_more = mJsonObject.getString("isMore");
                                    if (_is_more.equalsIgnoreCase("Yes")) {
                                        listuser.setPullLoadEnable(true);
                                    } else {
                                        listuser.setPullLoadEnable(false);
                                    }

                                } else {
                                    listuser.setPullLoadEnable(false);
                                }

                            } else if (mStatus == 0) {
                                Constant.showToast("Server Error    ", getActivity());
                            } else if (mStatus == -1) {
                                //Redirect to Login
                                Constant.alertForLogin(getActivity());
                            }
                        } else {
                            Constant.showToast("Server Error    ", getActivity());
                        }


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

    private void setFont() {
        Constant.setFont(getActivity(), search_et, 0);
        Constant.setFont(getActivity(), _header, 0);
    }

    private void replaceFragment(int mPosition) {

        TrackMapFragment fragment = new TrackMapFragment();
        if (fragment != null) {

            TrackBean bean = userlist.get(mPosition);
            Bundle mBundle = new Bundle();
            mBundle.putString("USERID", bean.getId());
            fragment.setArguments(mBundle);


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();


        }
    }

    public void addData() {

        for (int i = 0; i <= 20; i++) {

            trackBean = new TrackBean();
            trackBean.setUsername("Demo");
            trackBean.setUserfollowed("2 Followed");
            trackBean.setUsertack("1 track");
            userlist.add(trackBean);
        }
    }

    @Override
    public void onRefresh() {
//For Pull To Refresh
        _page_number = 1;
        callGetFollowedUsers();

    }

    @Override
    public void onLoadMore() {
//For Load More from Bottom
        _page_number = _page_number + 1;
        callGetFollowedUsers();

    }
}
