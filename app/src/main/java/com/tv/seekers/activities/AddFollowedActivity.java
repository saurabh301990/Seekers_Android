package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.AddFollowedAdapter;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
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
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shoeb on 8/11/15.
 */
public class AddFollowedActivity extends Activity {

    /*Header*/
    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @OnClick(R.id.tgl_menu)
    public void tgl_menu(View view) {
        finish();
    }

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;

    @Bind(R.id.search_et)
    EditText search_et;

    @Bind(R.id.listView)
    ListView listView;

    private ArrayList<TrackBean> userlist = new ArrayList<TrackBean>();
    TrackBean trackBean;
    private AddFollowedAdapter adapter;

    private String user_id = "";
    private SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.add_followed_screen);

//        ErrorReporter.getInstance().Init(AddFollowedActivity.this);
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();
        setOnItemClick();


        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
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

        if (NetworkAvailablity.checkNetworkStatus(AddFollowedActivity.this)) {
            callGetAllUsers();
        } else {
            Constant.showToast(AddFollowedActivity.this.getResources().getString(R.string.internet), AddFollowedActivity.this);
        }

    }

    private void setOnItemClick() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (NetworkAvailablity.checkNetworkStatus(AddFollowedActivity.this)){
                    TrackBean bean = userlist.get(position);
                    callAddFollowed(bean.getId());
                }else{
                    Constant.showToast(getResources().getString(R.string.internet), AddFollowedActivity.this);
                }


            }
        });
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


    private void callAddFollowed(final String id) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(AddFollowedActivity.this);


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(AddFollowedActivity.this)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {

                            System.out.println("Request of Add Follow : "+ WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOW_USER) + "?id=" + id);
                            System.out.println("Request of Add Follow With Cookie: "+sPref.getString(Constant.Cookie, ""));

                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOW_USER) + "?id=" + id);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of FOLLOW_USER : " + _responseMain);

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

                        AddFollowedActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", AddFollowedActivity.this);
                            }
                        });

                    }


                } else {
                    AddFollowedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(getResources().getString(R.string.internet), AddFollowedActivity.this);
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

                            Constant.showToast("User added in followers list.", AddFollowedActivity.this);
                            finish();

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", AddFollowedActivity.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(AddFollowedActivity.this);
                        }

                    } catch (Exception e) {

                        Constant.showToast("Server Error    ", AddFollowedActivity.this);
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error    ", AddFollowedActivity.this);
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

    private void callGetAllUsers() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                Constant.showLoader(AddFollowedActivity.this);

                try {
                    mJsonObject.put("pageNo", 1);
                    mJsonObject.put("limit", 20);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of GET_ALL_USER : " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(AddFollowedActivity.this)) {

                    try {

                        HttpURLConnection urlConnection;


                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_ALL_USER));
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
                            System.out.println("Response of GET_ALL_USER : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        AddFollowedActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", AddFollowedActivity.this);
                            }
                        });

                    }


                } else {
                    AddFollowedActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(AddFollowedActivity.this.getResources().getString(R.string.internet), AddFollowedActivity.this);
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
                                            String profilePic = mSubJsonObject.getString("profilePic");
                                            bean.setImageURL(profilePic);
                                            String follower_count = mSubJsonObject.getString("follower_count");
                                            bean.setUserfollowed(follower_count);

                                            userlist.add(bean);
                                        }

                                        if (userlist.size() > 0) {
                                            //Set Adapter
                                            adapter = new AddFollowedAdapter(userlist, AddFollowedActivity.this);
                                            listView.setAdapter(adapter);

                                        } else {
                                            Constant.showToast("No users found!", AddFollowedActivity.this);
                                        }

                                    } else {
                                        Constant.showToast("No users found!", AddFollowedActivity.this);
                                    }

                                } else {
                                    Constant.showToast("Server Error    ", AddFollowedActivity.this);
                                }
                            } else if (mStatus == 0) {
                                Constant.showToast("Server Error    ", AddFollowedActivity.this);
                            } else if (mStatus == -1) {
                                //Redirect to Login
                                Constant.alertForLogin(AddFollowedActivity.this);
                            }
                        } else {
                            Constant.showToast("Server Error    ", AddFollowedActivity.this);
                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        Constant.showToast("Server Error ", AddFollowedActivity.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", AddFollowedActivity.this);

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


    private void setOnClick() {

    }

    private void setData() {
        hdr_title.setText(getResources().getString(R.string.addFollowedtext));
        tgl_menu.setImageResource(R.mipmap.back);
        hdr_fltr.setVisibility(View.GONE);
    }

    private void setFont() {
        Constant.setFont(AddFollowedActivity.this, hdr_title, 0);
        Constant.setFont(AddFollowedActivity.this, search_et, 0);
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
}
