package com.tv.seekers.activities;

import android.app.Activity;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.CircleBitmapDisplayer;
import com.tv.seekers.utils.NetworkAvailablity;

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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 28/12/15.
 */
public class PostDetailsTextImg extends Activity implements View.OnClickListener {

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

    @Bind(R.id.userpostDescription_tv)
    TextView userpostDescription_tv;

    @Bind(R.id.date_time_tv)
    TextView date_time_tv;

    private String mPostId = "";

    private DisplayImageOptions optionsUser;
    private DisplayImageOptions optionsPostImg;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_details_txt_img_screen);
        ButterKnife.bind(this);
        init();
        setFont();
        setData();
        setOnClick();

        gettingIntentData();

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

                builder = new Uri.Builder()
                        .appendQueryParameter("post_id", mPostId);
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(PostDetailsTextImg.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

                            String query = builder.build().getEncodedQuery();
                            System.out.println("Request : " + query);
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_POST_DETAILS));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setReadTimeout(300000);

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
                            System.out.println("Response of Get Post Details : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
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
                            JSONObject jsonObject = _JsonObject.getJSONObject("post_details");
                            JSONObject _jSubObject = jsonObject.getJSONObject("result");

                            if (_jSubObject.has("id")) {
                                String mID = _jSubObject.getString("id");
                            }

                            if (_jSubObject.has("source_id")) {
                                String mSourceID = _jSubObject.getString("source_id");
                            }
                            if (_jSubObject.has("cop_id")) {
                                String cop_id = _jSubObject.getString("cop_id");
                            }
                            if (_jSubObject.has("post_text")) {
                                String post_text = _jSubObject.getString("post_text");
                                userpostDescription_tv.setText(post_text);
                            }
                            if (_jSubObject.has("post_type")) {
                                String post_type = _jSubObject.getString("post_type");
                            }
                            if (_jSubObject.has("post_description")) {
                                String post_description = _jSubObject.getString("post_description");
                            }
                            if (_jSubObject.has("post_image")) {
                                String post_image = _jSubObject.getString("post_image");
                                imageLoaderNew.displayImage(post_image, post_iv,
                                        optionsPostImg,
                                        null);
                            }
                            if (_jSubObject.has("post_video")) {
                                String post_video = _jSubObject.getString("post_video");
                            }
                            if (_jSubObject.has("post_id")) {
                                String post_id = _jSubObject.getString("post_id");
                            }
                            if (_jSubObject.has("post_location")) {
                                String post_location = _jSubObject.getString("post_location");
                                userLocation_tv.setText(post_location);
                            }
                            if (_jSubObject.has("post_lat")) {
                                String post_lat = _jSubObject.getString("post_lat");
                            }
                            if (_jSubObject.has("post_long")) {
                                String post_long = _jSubObject.getString("post_long");
                            }
                            if (_jSubObject.has("post_radius")) {
                                String post_radius = _jSubObject.getString("post_radius");
                            }
                            if (_jSubObject.has("post_url")) {
                                String post_url = _jSubObject.getString("post_url");
                            }
                            if (_jSubObject.has("post_time")) {
                                String post_time = _jSubObject.getString("post_time");
                            }
                            if (_jSubObject.has("post_fetch_time")) {
                                String post_fetch_time = _jSubObject.getString("post_fetch_time");
                            }
                            if (_jSubObject.has("post_user_id")) {
                                String post_user_id = _jSubObject.getString("post_user_id");
                            }

                            if (_jSubObject.has("user_image")) {
                                String user_image = _jSubObject.getString("user_image");
                                imageLoaderNew.displayImage(user_image, user_img_iv,
                                        optionsUser,
                                        null);
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
                            if (_jSubObject.has("source")) {
                                source = _jSubObject.getString("source");
                                if (source.equalsIgnoreCase("Twitter")) {
                                    user_imgType_iv.setImageResource(R.mipmap.twit_top_corner);
                                } else if (source.equalsIgnoreCase("Instagram")) {
                                    user_imgType_iv.setImageResource(R.mipmap.instagr_top_corner);
                                } else if (source.equalsIgnoreCase("Youtube")) {
                                    user_imgType_iv.setImageResource(R.mipmap.youtube_top_corner);
                                } else if (source.equalsIgnoreCase("Vk")) {
                                    user_imgType_iv.setImageResource(R.mipmap.vk_top_corner);
                                } else if (source.equalsIgnoreCase("Meetup")) {
                                    user_imgType_iv.setImageResource(R.mipmap.meetup_top_corner);
                                }
                            }
                            if (_jSubObject.has("user_name")) {
                                String user_name = _jSubObject.getString("user_name");
                                if (user_name.equalsIgnoreCase("")) {
                                    userType_tv.setText(source + " User");
                                } else {
                                    userType_tv.setText(user_name + " / " + source + " User");
                                }
                            }

                            if (_jSubObject.has("view_type")) {
                                String view_type = _jSubObject.getString("view_type");
                                if (view_type.equalsIgnoreCase("T")) {
                                    post_iv.setVisibility(View.GONE);
                                    userpostDescription_tv.setVisibility(View.VISIBLE);

                                } else if (view_type.equalsIgnoreCase("TI")) {
                                    post_iv.setVisibility(View.VISIBLE);
                                    userpostDescription_tv.setVisibility(View.VISIBLE);

                                } else if (view_type.equalsIgnoreCase("I")) {
                                    post_iv.setVisibility(View.VISIBLE);
                                    userpostDescription_tv.setVisibility(View.GONE);

                                } else {

                                }
                            }

                            if (_jSubObject.has("post_time")) {
                                date_time_tv.setText(_jSubObject.getString("post_time"));
                            }


                        } else {
                            Constant.showToast("Server Error ", PostDetailsTextImg.this);
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

    private void setOnClick() {
        tgl_menu.setOnClickListener(this);
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
            default:
                break;
        }
    }


}
