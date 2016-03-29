package com.tv.seekers.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.CircleBitmapDisplayer;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 28/12/15.
 */
public class PostDetailsTextImg extends YouTubeBaseActivity implements View.OnClickListener {

    /*Header*/
    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;

    @Bind(R.id.user_img_iv)
    ImageView user_img_iv;

    @Bind(R.id.user_imgType_iv)
    ImageView user_imgType_iv;

    @Bind(R.id.userType_tv)
    TextView userType_tv;

    @Bind(R.id.userLocation_tv)
    TextView userLocation_tv;

    @Bind(R.id.post_iv)
    ImageView post_iv;

    @Bind(R.id.isFollow)
    ImageView isFollow;

    @Bind(R.id.userpostDescription_tv)
    TextView userpostDescription_tv;

    @Bind(R.id.date_time_tv)
    TextView date_time_tv;

    @Bind(R.id.post_vid)
    YouTubePlayerView post_vid;

    private String mPostId = "";
    private String post_video = "";
    private String userIdForFollow = "";
    private boolean isFollowed = false;
    SharedPreferences sPref;
    private MediaController mMediaController;

    private DisplayImageOptions optionsUser;
    private DisplayImageOptions optionsPostImg;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_details_txt_img_screen);
        ButterKnife.bind(this);
        //ErrorReporter.getInstance().Init(PostDetailsTextImg.this);
        init();
        setFont();
        setData();
        setOnClick();
        sPref = getSharedPreferences("LOGINPREF", PostDetailsTextImg.this.MODE_PRIVATE);
        mMediaController = new MediaController(PostDetailsTextImg.this);

        post_vid.setVisibility(View.VISIBLE);

        gettingIntentData();

//        Constant.showToast("POst Details Called", PostDetailsTextImg.this);

    }

    private void init() {
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        optionsUser = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.user)
                .showImageForEmptyUri(R.mipmap.user)
                .showImageOnFail(R.mipmap.user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();

        optionsPostImg = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loader)
                .showImageForEmptyUri(R.mipmap.default_post_img)
                .showImageOnFail(R.mipmap.default_post_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    private void gettingIntentData() {

        try {

            mPostId = getIntent().getStringExtra("POSTID");
            if (NetworkAvailablity.checkNetworkStatus(PostDetailsTextImg.this)) {
                callGetPostDetail();
            } else {
                Constant.showToast(getResources().getString(R.string.internet), PostDetailsTextImg.this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Constant.showToast("Server Error ", PostDetailsTextImg.this);
        }
    }

    private void callGetPostDetail() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(PostDetailsTextImg.this);

                /*builder = new Uri.Builder()
                        .appendQueryParameter("id", mPostId);*/
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(PostDetailsTextImg.this)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            System.out.println("Request of Post Details screen : " + WebServiceConstants.getMethodUrl(WebServiceConstants.GET_POST_DETAILS) + "?id=" + mPostId);
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_POST_DETAILS) + "?id=" + mPostId);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("RESPONSE of Post Details screen : " + _responseMain);

                            } else {
                                Log.v("Post Details", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", PostDetailsTextImg.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", PostDetailsTextImg.this);
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
                        JSONObject _JsonObject = new JSONObject(_responseMain);
                        int status = _JsonObject.getInt("status");

                        if (status == 1) {

                            JSONObject _jSubObject = _JsonObject.getJSONObject("data");

                            if (_jSubObject.has("id")) {
                                String mID = _jSubObject.getString("id");
                            }

                           /* if (_jSubObject.has("source_id")) {
                                String mSourceID = _jSubObject.getString("source_id");
                            }*/
                            if (_jSubObject.has("cop_id")) {
                                String cop_id = _jSubObject.getString("cop_id");
                            }
                            if (_jSubObject.has("postText")) {

                                String originalString = _jSubObject.getString("postText");
                                System.out.println("Post Text originalString : " + originalString);
                                if (originalString.contains("http")) {
                                    String newString = originalString.replaceAll("(?:https?|ftps?)://[\\w/%.-]+", "<a href='$0'>$0</a>");
                                    System.out.println("Post Text newString : " + newString);

                                    userpostDescription_tv.setText(Html.fromHtml(newString));
                                } else {
                                    System.out.println("Post Text originalString SET : " + originalString);
                                    userpostDescription_tv.setText(Html.fromHtml(originalString));
                                }


                            }
                            if (_jSubObject.has("postType")) {
                                String post_type = _jSubObject.getString("postType");
                            }

                            if (_jSubObject.has("postDescription")) {
                                String post_description = _jSubObject.getString("postDescription");
                            }
                            if (_jSubObject.has("postImage")) {

                                JSONObject mpostImage = _jSubObject.getJSONObject("postImage");
                                String postImgUrl = "";
                                String mSmall = mpostImage.getString("small");
                                String medium = mpostImage.getString("medium");
                                String large = mpostImage.getString("large");
                                if (large != null && !large.equalsIgnoreCase("")) {
                                    postImgUrl = large;
                                    imageLoaderNew.displayImage(postImgUrl, post_iv,
                                            optionsPostImg,
                                            null);
                                } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                    postImgUrl = medium;
                                    imageLoaderNew.displayImage(postImgUrl, post_iv,
                                            optionsPostImg,
                                            null);
                                }
                                if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                    postImgUrl = mSmall;
                                    imageLoaderNew.displayImage(postImgUrl, post_iv,
                                            optionsPostImg,
                                            null);
                                }


                            }

                            if (_jSubObject.has("postVideo")) {
                                post_video = _jSubObject.getString("postVideo");
                            }
                            if (_jSubObject.has("postId")) {
                                String post_id = _jSubObject.getString("postId");
                            }
                            if (_jSubObject.has("postLocation")) {
                                String post_location = _jSubObject.getString("postLocation");
                                userLocation_tv.setText(post_location);
                            }

                           /* JSONObject mObjectLotLng = new JSONObject(_jSubObject.getString("loc"));
                            JSONArray mArrayCoordinates = mObjectLotLng.getJSONArray("coordinates");

                            String post_lat = mArrayCoordinates.getString(0);
                            String post_long = mArrayCoordinates.getString(1);
                         */
                            if (_jSubObject.has("post_radius")) {
                                String post_radius = _jSubObject.getString("post_radius");
                            }
                            if (_jSubObject.has("postUrl")) {
                                String post_url = _jSubObject.getString("postUrl");
                            }
                            if (_jSubObject.has("postTime")) {
                                long post_time = _jSubObject.getLong("postTime");
                                post_time = post_time * 1000;
                                date_time_tv.setText(getDateFromMilliseconds(post_time, "MMMM dd yyyy hh:mm a"));
                            }
                            if (_jSubObject.has("post_fetch_time")) {
                                String post_fetch_time = _jSubObject.getString("post_fetch_time");
                            }
                            if (_jSubObject.has("post_user_id")) {
                                String post_user_id = _jSubObject.getString("post_user_id");
                            }

                            if (_jSubObject.has("user_gender")) {
                                String user_gender = _jSubObject.getString("user_gender");
                            }
                            if (_jSubObject.has("user_lat")) {
                                String user_lat = _jSubObject.getString("user_lat");
                            }
                            if (_jSubObject.has("user_long")) {
                                String user_long = _jSubObject.getString("user_long");
                            }
                            if (_jSubObject.has("user_address")) {
                                String user_address = _jSubObject.getString("user_address");
                            }
                            if (_jSubObject.has("user_mobile")) {
                                String user_mobile = _jSubObject.getString("user_mobile");
                            }
                            String source = "";
                            if (_jSubObject.has("sourceType")) {
                                source = _jSubObject.getString("sourceType");
                                if (source.equalsIgnoreCase("TWITTER")) {
                                    user_imgType_iv.setImageResource(R.mipmap.twit_top_corner);
                                } else if (source.equalsIgnoreCase("INSTAGRAM")) {
                                    user_imgType_iv.setImageResource(R.mipmap.instagr_top_corner);
                                } else if (source.equalsIgnoreCase("YOUTUBE")) {
                                    user_imgType_iv.setImageResource(R.mipmap.youtube_top_corner);
                                } else if (source.equalsIgnoreCase("VK")) {
                                    user_imgType_iv.setImageResource(R.mipmap.vk_top_corner);
                                } else if (source.equalsIgnoreCase("MEETUP")) {
                                    user_imgType_iv.setImageResource(R.mipmap.meetup_top_corner);
                                } else if (source.equalsIgnoreCase("FLICKR")) {
                                    user_imgType_iv.setImageResource(R.mipmap.flickr_top_corner);
                                }
                            }

                            JSONObject mJsonObjectUser = new JSONObject(_jSubObject.getString("user"));
                            if (mJsonObjectUser.has("username")) {
                                String user_name = mJsonObjectUser.getString("username");
                                userType_tv.setText(user_name);
                                /*if (user_name.equalsIgnoreCase("")) {
                                    userType_tv.setText(source + " User");
                                } else {
                                    userType_tv.setText(user_name + " / " + source + " User");
                                }*/
                            }
                            if (mJsonObjectUser.has("isFollowed")) {
                                isFollowed = mJsonObjectUser.getBoolean("isFollowed");
                                if (isFollowed) {
                                    isFollow.setImageResource(R.mipmap.blue);
                                } else {
                                    isFollow.setImageResource(R.mipmap.grey);
                                }
                            }
                            if (mJsonObjectUser.has("id")) {
                                userIdForFollow = mJsonObjectUser.getString("id");

                            }
                            if (mJsonObjectUser.has("profilePic")) {

                                JSONObject mJsonObjectPic = mJsonObjectUser.getJSONObject("profilePic");
                                String postImgUrl = "";
                                String mSmall = mJsonObjectPic.getString("small");
                                String medium = mJsonObjectPic.getString("medium");
                                String large = mJsonObjectPic.getString("large");
                                if (mSmall != null && !mSmall.equalsIgnoreCase("")) {
                                    postImgUrl = mSmall;
                                } else if (medium != null && !medium.equalsIgnoreCase("")) {
                                    postImgUrl = medium;
                                } else if (large != null && !large.equalsIgnoreCase("")) {
                                    postImgUrl = large;
                                }
                                imageLoaderNew.displayImage(postImgUrl, user_img_iv,
                                        optionsUser,
                                        null);
                            }


                            if (_jSubObject.has("postType")) {
                                String view_type = _jSubObject.getString("postType");
                                if (view_type.equalsIgnoreCase("TEXT_ONLY")) {
                                    post_iv.setVisibility(View.GONE);
                                    userpostDescription_tv.setVisibility(View.VISIBLE);
                                    post_vid.setVisibility(View.GONE);

                                } else if (view_type.equalsIgnoreCase("TEXT_WITH_IMAGE")) {
                                    post_iv.setVisibility(View.VISIBLE);
                                    userpostDescription_tv.setVisibility(View.VISIBLE);
                                    post_vid.setVisibility(View.GONE);

                                } else if (view_type.equalsIgnoreCase("IMAGE_ONLY")) {
                                    post_iv.setVisibility(View.VISIBLE);
                                    userpostDescription_tv.setVisibility(View.GONE);
                                    post_vid.setVisibility(View.GONE);

                                } else if (view_type.equalsIgnoreCase("VIDEO_ONLY") || view_type.equalsIgnoreCase("TEXT_WITH_VIDEO")) {

                                    if (source.equalsIgnoreCase("YOUTUBE")) {
                                        post_iv.setVisibility(View.GONE);
                                        post_vid.setVisibility(View.VISIBLE);
                                        userpostDescription_tv.setVisibility(View.VISIBLE);
                                        post_vid.initialize(Constant.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                                            @Override
                                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                                /*youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
                                                youTubePlayer.setPlaybackEventListener(playbackEventListener);*/

                                                /** Start buffering **/
                                                if (!b) {
                                                    youTubePlayer.cueVideo(post_video);
                                                }
                                            }

                                            @Override
                                            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                                            }
                                        });
                                    } else {
                                        post_vid.setVisibility(View.GONE);
                                    }

                                } else {

                                }
                            }


                        } else if (status == 0) {
                            Constant.showToast("Server Error", PostDetailsTextImg.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(PostDetailsTextImg.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Constant.showToast("Server Error ", PostDetailsTextImg.this);
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", PostDetailsTextImg.this);
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

    private void setOnClick() {
        tgl_menu.setOnClickListener(this);
        isFollow.setOnClickListener(this);
    }

    private void setData() {
        hdr_title.setText(getResources().getString(R.string.postText));
        tgl_menu.setImageResource(R.mipmap.back);
        hdr_fltr.setVisibility(View.GONE);
    }

    private void setFont() {
        Constant.setFont(PostDetailsTextImg.this, hdr_title, 0);
        Constant.setFont(PostDetailsTextImg.this, userType_tv, 0);
        Constant.setFont(PostDetailsTextImg.this, userLocation_tv, 0);
        Constant.setFont(PostDetailsTextImg.this, userpostDescription_tv, 0);
        Constant.setFont(PostDetailsTextImg.this, date_time_tv, 0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tgl_menu:
                finish();
                break;
            case R.id.isFollow:
                if (NetworkAvailablity.checkNetworkStatus(PostDetailsTextImg.this)) {
                    callFollowUnFollowWS();
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), PostDetailsTextImg.this);
                }

                break;
            default:
                break;
        }
    }

    private void callFollowUnFollowWS() {

        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(PostDetailsTextImg.this);


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(PostDetailsTextImg.this)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {


                            System.out.println("Request of Add Follow With Cookie: " + sPref.getString(Constant.Cookie, ""));
                            String serviceUrl = "";
                            if (!isFollowed) {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOW_USER) + "?id=" + userIdForFollow;
                                System.out.println("Request of FOLLOW_USER: " + serviceUrl);
                            } else {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.UN_FOLLOW_USER) + "?id=" + userIdForFollow;
                                System.out.println("Request of UN_FOLLOW_USER: " + serviceUrl);
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
                            PostDetailsTextImg.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Constant.showToast("Server Error ", PostDetailsTextImg.this);
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

                        PostDetailsTextImg.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", PostDetailsTextImg.this);
                            }
                        });

                    }


                } else {
                    PostDetailsTextImg.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(PostDetailsTextImg.this.getResources().getString(R.string.internet), PostDetailsTextImg.this);
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
                            if (isFollowed) {
                                isFollowed = false;
                                isFollow.setImageResource(R.mipmap.grey);
                            } else {
                                isFollowed = true;
                                isFollow.setImageResource(R.mipmap.blue);
                            }
//                            Constant.showToast("User added in followers list.", PostDetailsTextImg.this);


                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", PostDetailsTextImg.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(PostDetailsTextImg.this);
                        }

                    } catch (Exception e) {

                        Constant.showToast("Server Error    ", PostDetailsTextImg.this);
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error    ", PostDetailsTextImg.this);
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


    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onPlaying() {
        }

        @Override
        public void onSeekTo(int arg0) {
        }

        @Override
        public void onStopped() {
        }

    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onVideoStarted() {
        }
    };
}
