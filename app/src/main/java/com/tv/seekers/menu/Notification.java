package com.tv.seekers.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.activities.PostDetailsTextImg;
import com.tv.seekers.adapter.NotificationAdapter;
import com.tv.seekers.bean.Notificationbean;
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
 * Created by Shoeb on 4/11/15.
 */
public class Notification extends Fragment implements XListView.IXListViewListener {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constant.hideKeyBoard(getActivity());
    }

    ArrayList<Notificationbean> notifDataList = new ArrayList<Notificationbean>();
    Notificationbean notificationbean;
    NotificationAdapter notificationAdapter;

    @Bind(R.id.txtnotification)
    TextView tvnotification;

    @Bind(R.id.notificationtoggle)
    ToggleButton notificationtgl;

    @Bind(R.id.notificationlist)
    XListView lvnotify;

    private boolean notificationStatus = false;
    private SharedPreferences sPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_screen, container, false);
        ButterKnife.bind(this, view);
//        ErrorReporter.getInstance().Init(getActivity());
       /* adddata();

        notificationAdapter = new NotificationAdapter(notifDataList, getActivity());
        lvnotify.setAdapter(notificationAdapter);*/
        Constant.setFont(getActivity(), tvnotification, 0);

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);

        //Load More
        lvnotify.setSelector(android.R.color.transparent);
        lvnotify.setXListViewListener(this);
        lvnotify.setPullRefreshEnable(true);
        lvnotify.setPullLoadEnable(false);
        lvnotify.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notificationbean bean = notifDataList.get(position - 1);
                String postId = bean.getPostId();
                Intent i = new Intent(getActivity(), PostDetailsTextImg.class);
                i.putExtra("POSTID", postId);
                startActivity(i);

            }
        });

        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        notificationStatus = sPref.getBoolean(Constant.notificationStatus, false);
        notificationtgl.setChecked(notificationStatus);
        notificationtgl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    callUpdateNotifStatus(isChecked);
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), getActivity());
                }
            }
        });

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetNotifListWS();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), getActivity());
        }

        return view;
    }

    private int _page_number = 1;

    private void callGetNotifListWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();


            @Override
            protected void onPreExecute() {

                Constant.showLoader(getActivity());

                try {

                    mJsonObject.put("pageNo", _page_number);
                    mJsonObject.put("limit", 20);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of GET_NOTIF_LIST: " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {


                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_NOTIF_LIST));
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
                            System.out.println("Response of GET_NOTIF_LIST : " + _responseMain);


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

                                        if (notifDataList.size() > 0) {
                                            notifDataList.clear();
                                        }
                                        for (int i = 0; i < mData.length(); i++) {
                                            JSONObject mSubJsonObject = mData.getJSONObject(i);
                                            Notificationbean bean = new Notificationbean();

                                            String message = mSubJsonObject.getString("message");
                                            String postId = mSubJsonObject.getString("postId");
                                            bean.setNotificationtxt(message);
                                            bean.setPostId(postId);

                                            notifDataList.add(bean);
                                        }

                                        if (notifDataList.size() > 0) {
                                            lvnotify.stopRefresh();
                                            //Set Adapter
                                            notificationAdapter = new NotificationAdapter(notifDataList, getActivity());
                                            lvnotify.setAdapter(notificationAdapter);
                                        } else {

                                        }

                                    } else {

                                    }

                                } else {
                                    Constant.showToast("Server Error    ", getActivity());
                                }


                                if (mJsonObject.has("isMore")) {
                                    String _is_more = mJsonObject.getString("isMore");
                                    if (_is_more.equalsIgnoreCase("Yes")) {
                                        lvnotify.setPullLoadEnable(true);
                                    } else {
                                        lvnotify.setPullLoadEnable(false);
                                    }

                                } else {
                                    lvnotify.setPullLoadEnable(false);
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

    private void callUpdateNotifStatus(final boolean isChecked) {

        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                Constant.showLoader(getActivity());

                try {
                    mJsonObject.put("notificationStatus", isChecked);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of UPDATE_NOTIF_STATUS: " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.UPDATE_NOTIF_STATUS));
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
                            System.out.println("Response of UPDATE_NOTIF_STATUS : " + _responseMain);


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
                        JSONObject jsonObject = new JSONObject(_responseMain);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {

                        } else if (status == 0) {
                            Constant.showToast("Server Error", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
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


    public void adddata() {

        for (int i = 0; i <= 5; i++) {
            notificationbean = new Notificationbean();
            notificationbean.setNotificationtxt("It is A Dummy Data ");
            notifDataList.add(notificationbean);
        }

    }

    @Override
    public void onRefresh() {
//For Pull To Refresh
        _page_number = 1;
        callGetNotifListWS();

    }

    @Override
    public void onLoadMore() {
//For Load More from Bottom
        _page_number = _page_number + 1;
        callGetNotifListWS();

    }
}
