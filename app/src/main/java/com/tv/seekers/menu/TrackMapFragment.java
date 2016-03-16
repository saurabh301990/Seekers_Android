package com.tv.seekers.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;
import com.tv.seekers.activities.PostDetailsTextImg;
import com.tv.seekers.adapter.CustomWindAdapter;
import com.tv.seekers.adapter.HomeListAdapter;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.CircleBitmapDisplayer;
import com.tv.seekers.utils.HttpConnection;
import com.tv.seekers.utils.NetworkAvailablity;
import com.tv.seekers.utils.PathJSONParser;
import com.tv.seekers.utils.SquareImageView;
import com.tv.seekers.utils.XListView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by shoeb on 4/11/15.
 */
public class TrackMapFragment extends Activity implements XListView.IXListViewListener,GoogleMap.OnInfoWindowClickListener {


    /* private static final LatLng LOWER_MANHATTAN = new LatLng(22.7176622,
             75.8685777);
     private static final LatLng BROOKLYN_BRIDGE = new LatLng(22.728874, 75.8732923);
     private static final LatLng WALL_STREET = new LatLng(22.738021, 75.9088663);*/
    GoogleMap googleMap;
    private SupportMapFragment fragment;
    final String TAG = "TrackMapFragment";
    ArrayList<LatLng> markerPoints;

    private DisplayImageOptions optionsUserImg;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;

    @Bind(R.id.img)
    SquareImageView img;

    @Bind(R.id.name_info_tv)
    TextView name_info_tv;
/*
    @Bind(R.id.loc_info_tv)
    TextView loc_info_tv;*/

    @Bind(R.id.name_tv)
    TextView name_tv;

    @Bind(R.id.loc_tv)
    TextView loc_tv;

    @Bind(R.id.map_btn)
    Button map_btn;


    private TextView _header;
    private ImageView _ivRight;
    private ImageView _ivLeft;


    @OnClick(R.id.map_btn)
    public void map_btnClick(View view) {
        _isList = false;
        map_view.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        list_btn.setBackgroundColor(ContextCompat.getColor(TrackMapFragment.this, R.color.miles_inactive_color));
        map_btn.setBackgroundColor(Color.WHITE);

        map_btn.setTextColor(Color.BLACK);
        list_btn.setTextColor(ContextCompat.getColor(TrackMapFragment.this, R.color.grey_color));
    }

    @Bind(R.id.list_btn)
    Button list_btn;

    @OnClick(R.id.list_btn)
    public void list_btnClick(View view) {
        _isList = true;
        map_view.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        map_btn.setBackgroundColor(ContextCompat.getColor(TrackMapFragment.this, R.color.miles_inactive_color));
        list_btn.setBackgroundColor(Color.WHITE);

        list_btn.setTextColor(Color.BLACK);
        map_btn.setTextColor(ContextCompat.getColor(TrackMapFragment.this, R.color.grey_color));


//        addData();

    }

    @Bind(R.id.map_view_rel)
    RelativeLayout map_view;

    @Bind(R.id.unsave_iv)
    ImageView unsave_iv;

    boolean misFollow = true;

    @OnClick(R.id.unsave_iv)
    public void unsave_ivClick(View view) {
        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {


            callFollowUnFollowWS(misFollow);
        }

    }

    private void callFollowUnFollowWS(final boolean isFollow) {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(TrackMapFragment.this);


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {

                            System.out.println("Request of Add Follow With Cookie: " + sPref.getString(Constant.Cookie, ""));
                            String serviceUrl = "";
                            if (isFollow) {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.UN_FOLLOW_USER) + "?id=" + user_id;
                                System.out.println("Request of UN_FOLLOW_USER: " + serviceUrl);

                            } else {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOW_USER) + "?id=" + user_id;
                                System.out.println("Request of FOLLOW_USER: " + serviceUrl);
                            }

                            url = new URL(serviceUrl);
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
                            TrackMapFragment.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Constant.showToast("Server Error ", TrackMapFragment.this);
                                }
                            });
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        TrackMapFragment.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", TrackMapFragment.this);
                            }
                        });

                    }


                } else {
                    TrackMapFragment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
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

                            if (isFollow) {
                                misFollow = false;
                                unsave_iv.setImageResource(R.mipmap.grey);


                            } else {
                                misFollow = true;
                                unsave_iv.setImageResource(R.mipmap.blue);
                            }

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", TrackMapFragment.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(TrackMapFragment.this);
                        }

                    } catch (Exception e) {

                        Constant.showToast("Server Error    ", TrackMapFragment.this);
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error    ", TrackMapFragment.this);
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

    @Bind(R.id.list)
    XListView list;
    private List<HomeBean> _mainList = new ArrayList<HomeBean>();
    HomeListAdapter adapterList;
    private boolean _isList = false;
    SharedPreferences sPref;
    private int _page_number = 1;
    private String user_id = "";

    public void setfont() {
        Constant.setFont(TrackMapFragment.this, name_info_tv, 0);
        Constant.setFont(TrackMapFragment.this, name_tv, 0);
//        Constant.setFont(TrackMapFragment.this, loc_info_tv, 0);
        Constant.setFont(TrackMapFragment.this, loc_tv, 0);
        Constant.setFont(TrackMapFragment.this, map_btn, 0);
        Constant.setFont(TrackMapFragment.this, list_btn, 0);
        Constant.setFont(TrackMapFragment.this, _header, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_map_screen);
        ButterKnife.bind(this);



        //Load More
        list.setSelector(android.R.color.transparent);
        list.setXListViewListener(this);
        list.setPullRefreshEnable(true);
        list.setPullLoadEnable(false);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final HomeBean bean = _mainList.get(position - 1);
                Intent intentToTxtImg = new Intent(TrackMapFragment.this, PostDetailsTextImg.class);
                intentToTxtImg.putExtra("POSTID", bean.getPost_id());
                startActivity(intentToTxtImg);
            }
        });

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(TrackMapFragment.this));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        optionsUserImg = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.followed_profile_bg)
                .showImageForEmptyUri(R.mipmap.followed_profile_bg)
                .showImageOnFail(R.mipmap.followed_profile_bg)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();

        _ivRight = (ImageView) findViewById(R.id.hdr_fltr);
        _ivLeft = (ImageView) findViewById(R.id.tgl_menu);
        _ivLeft.setImageResource(R.mipmap.back);
        _ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        _ivRight.setVisibility(View.GONE);
        _header = (TextView) findViewById(R.id.hdr_title);
        _header.setText("Saved Profile");

        sPref = TrackMapFragment.this.getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        try {
            user_id = getIntent().getStringExtra("USERID");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {
            // Initializing
            markerPoints = new ArrayList<LatLng>();

          /*  FragmentManager fm = getChildFragmentManager();
            fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_view);
            if (fragment == null) {
                fragment = SupportMapFragment.newInstance();
                fm.beginTransaction().replace(R.id.map_view, fragment).commit();
            }*/
        } else {
            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
        }

        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {
            callGetUserDetails();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), TrackMapFragment.this);
        }

        setfont();
    }


    private void callGetUserDetails() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();


            @Override
            protected void onPreExecute() {


                Constant.showLoader(TrackMapFragment.this);
                /*{
    "userId":"3204c72d-5418-4b9c-ae0f-ac06dd4cc718",
    "pageNo":2,
    "limit":100
}*/

                try {

                    mJsonObject.put("userId", user_id);
                    mJsonObject.put("pageNo", _page_number);
                    mJsonObject.put("limit", 20);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                System.out.println("Request of FOLLOWED_USER_DETAILS: " + mJsonObject.toString());


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {

                    try {

                        HttpURLConnection urlConnection;
                        try {

//                            String query = builder.build().getEncodedQuery();
                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOWED_USER_DETAILS));
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
                            System.out.println("Response of FOLLOWED_USER_DETAILS : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        TrackMapFragment.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", TrackMapFragment.this);
                            }
                        });

                    }


                } else {
                    TrackMapFragment.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
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
                            String userId = "";
                            String username = "";
                            String userImgUrl = "";
                            String address = "";
                            String lastName = "";
                            String firstName = "";
                            String userType = "";


                            if (_page_number == 1) {
                                if (_mainList.size() > 0) {
                                    _mainList.clear();
                                }
                            }

                            JSONObject _resultJSONArray = jsonObject.getJSONObject("data");


                            if (_isList) {
//                                    no_post_layout.setVisibility(View.GONE);
                                map_view.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                            } else {
                                    /*if (_mainList.size() > 0) {
                                        _mainList.clear();
                                    }*/
                                map_view.setVisibility(View.VISIBLE);
                                list.setVisibility(View.GONE);
                            }

                            if (_resultJSONArray.has("id")) {
                                userId = _resultJSONArray.getString("id");
                            }

                            if (_resultJSONArray.has("username")) {
                                username = _resultJSONArray.getString("username");

                            }


                            if (_resultJSONArray.has("firstName")) {
                                firstName = _resultJSONArray.getString("firstName");

                            }
                            if (_resultJSONArray.has("lastName")) {
                                lastName = _resultJSONArray.getString("lastName");
                            }

                            if (firstName != null && !firstName.equalsIgnoreCase("") && lastName != null && !lastName.equalsIgnoreCase("")) {
                                name_tv.setText(firstName + " " + lastName);
                            } else {
                                name_tv.setText(username);
                            }


                            if (_resultJSONArray.has("userType")) {
                                userType = _resultJSONArray.getString("userType");
                            }


                            if (_resultJSONArray.has("profilePic")) {
                                JSONObject mJsonObjectPic = _resultJSONArray.getJSONObject("profilePic");

                                String mSmall = mJsonObjectPic.getString("small");
                                String medium = mJsonObjectPic.getString("medium");
                                String large = mJsonObjectPic.getString("large");
                                if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                    userImgUrl = mSmall;
                                } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                    userImgUrl = medium;
                                } else if (large != null && !large.equalsIgnoreCase("")) {
                                    userImgUrl = large;
                                }

                                imageLoaderNew.displayImage(userImgUrl, img,
                                        optionsUserImg,
                                        null);

                            }


                            if (_resultJSONArray.has("address")) {
                                address = _resultJSONArray.getString("address");
                                loc_tv.setText(" " + address);
                            }
                            JSONArray mJsonArrayposts = _resultJSONArray.getJSONArray("posts");
                            int length = mJsonArrayposts.length();
                            if (length > 0) {
//                                MarkerOptions options = new MarkerOptions();
                                for (int i = 0; i < length; i++) {
                                    JSONObject _jSubObject = mJsonArrayposts.getJSONObject(i);
                                    HomeBean bean = new HomeBean();
                                    bean.setId(userId);
                                    bean.setUser_name(username);
                                    bean.setUser_image(userImgUrl);
                                    bean.setUser_address(address);

                                    if (_jSubObject.has("id")) {
                                        bean.setPost_id(_jSubObject.getString("id"));
                                    } else {
                                        bean.setPost_id("");
                                    }
                                    if (_jSubObject.has("postText")) {
                                        bean.setPost_text(_jSubObject.getString("postText"));
                                    } else {
                                        bean.setPost_text("");
                                    }


                                    if (_jSubObject.has("sourceType")) {
                                        bean.setSource(_jSubObject.getString("sourceType"));
                                    } else {
                                        bean.setSource("");
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
                                        } else if (_jSubObject.getString("postType").equalsIgnoreCase("V")) {
                                            bean.setType(4);
                                        } else {
                                            bean.setType(1);
                                        }
                                    } else {
                                        bean.setView_type("");
                                    }

                                    if (_jSubObject.has("postImage")) {

                                        JSONObject mpostImage = _jSubObject.getJSONObject("postImage");
                                        String postImgUrl = "";
                                        String mSmall = mpostImage.getString("small");
                                        String medium = mpostImage.getString("medium");
                                        String large = mpostImage.getString("large");
                                        if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                            postImgUrl = mSmall;
                                        } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                            postImgUrl = medium;
                                        } else if (large != null && !large.equalsIgnoreCase("")) {
                                            postImgUrl = large;
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
                                        bean.setPost_time(getDateFromMilliseconds(postTime, "dd MMMM yyyy hh:mm a"));
                                    } else {
                                        bean.setPost_time("");
                                    }

                                    if (_jSubObject.has("postVideo")) {
                                        bean.setPost_video(_jSubObject.getString("postVideo"));
                                    } else {
                                        bean.setPost_video("");
                                    }

                                    double latitude = 0.0;
                                    double longitude = 0.0;
                                    JSONObject mObjectLotLng = new JSONObject(_jSubObject.getString("loc"));
                                    if (mObjectLotLng.has("x")) {
                                        longitude = mObjectLotLng.getDouble("x");
                                        bean.setPost_long(String.valueOf(longitude));

                                    }
                                    if (mObjectLotLng.has("y")) {
                                        latitude = mObjectLotLng.getDouble("y");
                                        bean.setPost_lat(String.valueOf(latitude));

                                    }

                                    LatLng mLatLong = new LatLng(latitude, longitude);
                                    // TODO: 19/2/16 Map
                                    markerPoints.add(i, mLatLong);

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
                                        snippet = "true" + "&" + userImg + "&" + bean.getPost_id() + "&" + bean.getPost_text();
                                    }/* else {
                    snippet = bean.getUser_image();
                }*/
//                System.out.println("FINAL SNIPPET ON MAP @#@#@#@ :" + snippet + " With Post Id : " + postID);
                                    googleMap.addMarker(new MarkerOptions().position(mLatLong)
                                            .title(userName)
                                            .snippet(snippet)

                                            .icon(bitmapMarker));

                                    _mainList.add(bean);

                                }
                                // info window.
                                googleMap.setInfoWindowAdapter(new CustomWindAdapter(TrackMapFragment.this));
                            }


                            //Todo map
                            if (_mainList.size() > 0) {

                                if (_mainList.size() > 8) {
                                    HomeBean bean = _mainList.get(0);
                                    double latitude = Double.valueOf(bean.getPost_lat());
                                    double longitude = Double.valueOf(bean.getPost_long());
                                    LatLng lower = new LatLng(latitude, longitude);
                                    HomeBean bean1 = _mainList.get(_mainList.size() - 1);
                                    double latitude1 = Double.valueOf(bean1.getPost_lat());
                                    double longitude1 = Double.valueOf(bean1.getPost_long());
                                    LatLng higher = new LatLng(latitude1, longitude1);

                                    int sizeOfList = _mainList.size() / 8;
                                    if (sizeOfList % 8 != 0)
                                        sizeOfList = sizeOfList + 1;

                                    int tempSize = 0;
                                    int startTempSize = 0;
                                    for (int i = 0; i <= sizeOfList; i++) {

                                        tempSize = tempSize + 8;

                                        if (tempSize > _mainList.size()) {
                                            tempSize = _mainList.size();
                                        }
                                        if (i == 0)
                                            startTempSize = tempSize - 8;
                                        else
                                            startTempSize = tempSize - 9;

                                        List<HomeBean> newArrayList = _mainList.subList(startTempSize, tempSize);

                                        String url = getDirectionsUrl(newArrayList);
                                        System.out.println("url : " + url);
                                        ReadTask downloadTask = new ReadTask();
                                        downloadTask.execute(url);
                                    }

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lower,
                                            14));
                                } else {
                                    // TODO: 19/2/16 Normal code

                                    HomeBean bean = _mainList.get(0);
                                    double latitude = Double.valueOf(bean.getPost_lat());
                                    double longitude = Double.valueOf(bean.getPost_long());
                                    LatLng lower = new LatLng(latitude, longitude);
                                    String url = getDirectionsUrl(_mainList);
                                    System.out.println("url : " + url);
                                    ReadTask downloadTask = new ReadTask();
                                    downloadTask.execute(url);
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lower,
                                            14));
                                }


                            }

                            //todo setting Adapter here

                            if (_page_number == 1) {
                                adapterList = new HomeListAdapter(_mainList, TrackMapFragment.this, true);
                                list.setAdapter(adapterList);
//                                        onLoad();
                                list.stopRefresh();
                            } else {

                                adapterList.notifyDataSetChanged();
                            }


                            if (jsonObject.has("isMore")) {
                                String _is_more = jsonObject.getString("isMore");
                                if (_is_more.equalsIgnoreCase("Yes")) {
                                    list.setPullLoadEnable(true);
                                } else {
                                    list.setPullLoadEnable(false);
                                }

                            } else {
                                list.setPullLoadEnable(false);
                            }
                        } else if (status == 0) {
                            Constant.showToast("Server Error", TrackMapFragment.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(TrackMapFragment.this);
                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                        Constant.showToast("Server Error ", TrackMapFragment.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", TrackMapFragment.this);

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
    public void onResume() {
        super.onResume();

        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {
            initMap();
        } else {
            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
        }


    }

    private void initMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_view)).getMap();
            googleMap.setOnInfoWindowClickListener(this);
           /* MarkerOptions options = new MarkerOptions();
            options.position(LOWER_MANHATTAN);
            options.position(BROOKLYN_BRIDGE);
            options.position(WALL_STREET);
            googleMap.addMarker(options);*/

//            addMarkers();
//            String url = getMapsApiDirectionsUrl();
//            String url ="https://maps.googleapis.com/maps/api/directions/json?origin=22.7176622,75.8685777&destination=22.738021,75.9088663&%20waypoints=optimize:true|22.7176622,75.8685777|22.728874,75.8732923|22.738021,75.9088663&sensor=false";

            // Getting URL to the Google Directions API


        }
    }

    private String getDirectionsUrl(List<HomeBean> mList) {

        HomeBean bean = mList.get(0);
        double lowerLat = Double.valueOf(bean.getPost_lat());
        double lowerLong = Double.valueOf(bean.getPost_long());

        HomeBean bean1 = mList.get(mList.size() - 1);
        double higherLat = Double.valueOf(bean1.getPost_lat());
        double higherLong = Double.valueOf(bean1.getPost_long());

        // Origin of route
        String str_origin = "origin=" + lowerLat + "," + lowerLong;

        // Destination of route
        String str_dest = "destination=" + higherLat + "," + higherLong;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";

        waypoints = "waypoints=";

        for (int i = 1; i <= mList.size() - 2; i++) {
            HomeBean bean2 = mList.get(i);
            double latitude = Double.valueOf(bean2.getPost_lat());
            double longitude = Double.valueOf(bean2.getPost_long());
            waypoints += latitude + "," + longitude + "|";
        }


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + waypoints;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

  /*  private String getMapsApiDirectionsUrl() {

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
    }*/

  /*  private void addMarkers() {
        if (googleMap != null) {
            googleMap.addMarker(new MarkerOptions().position(BROOKLYN_BRIDGE)
                    .title("First Point"));
            googleMap.addMarker(new MarkerOptions().position(LOWER_MANHATTAN)
                    .title("Second Point"));
            googleMap.addMarker(new MarkerOptions().position(WALL_STREET)
                    .title("Third Point"));
        }
    }*/

    @Override
    public void onRefresh() {
//For Pull To Refresh
        _page_number = 1;
        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {
            callGetUserDetails();
        } else {
            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
        }

    }

    @Override
    public void onLoadMore() {
//For Load More from Bottom
        _page_number = _page_number + 1;
        if (NetworkAvailablity.checkNetworkStatus(TrackMapFragment.this)) {
            callGetUserDetails();
        } else {
            Constant.showToast(TrackMapFragment.this.getResources().getString(R.string.internet), TrackMapFragment.this);
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

            try {
                googleMap.addPolyline(polyLineOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constant.hideKeyBoard(TrackMapFragment.this);
    }

    private String getDateFromMilliseconds(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
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
            post_text = snippetWHole[3];
            System.out.println("postId " + postId);

            Intent intentToTxtImg = new Intent(TrackMapFragment.this, PostDetailsTextImg.class);
            intentToTxtImg.putExtra("POSTID", postId);
            startActivity(intentToTxtImg);
        } else {
            Constant.showToast("Post not found.", TrackMapFragment.this);
        }


    }
}
