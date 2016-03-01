package com.tv.seekers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shoeb on 4/12/15.
 */
public class HomeListAdapter extends BaseAdapter {
    private List<HomeBean> list = new ArrayList<HomeBean>();
    private Activity context;
    private DisplayImageOptions optionsUser;
    private DisplayImageOptions optionsPostImg;
    private com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;
    private SharedPreferences sPref;
    private boolean isUserDetails = false;
//    private MediaController mMediaController;


    public HomeListAdapter(List<HomeBean> dataList, Activity context, boolean isUserDetails ) {
        this.list = dataList;
        this.context = context;
        this.isUserDetails = isUserDetails;

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        sPref = context.getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
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

//        mMediaController = new MediaController(context);
    }

    @Override

    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {

        TextView tvUserType = null;
        TextView tvUserLocation = null;
        TextView tvUserPost = null;
        TextView date_time_tv = null;
        ImageView userImage = null;
        ImageView userTypeImage = null;
        ImageView postImage = null;
        VideoView videoView = null;
        ImageView isFollow = null;


    }

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_TEXT_IMG = 2;
    public static final int TYPE_IMG = 3;
    public static final int TYPE_VID = 4;


    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        HomeBean bean = list.get(position);

        return bean.getType();
    }

    ViewHolder view_holder;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.e("HOME ADAPTER : ", "getView Called. with POs " + position);


        final HomeBean bean = list.get(position);
        int listViewItemType = getItemViewType(position);

        if (convertView == null) {

            view_holder = new ViewHolder();

            if (listViewItemType == TYPE_TEXT) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_text, null);

                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.tvUserPost = (TextView) convertView.findViewById(R.id.userpostDescription_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.isFollow = (ImageView) convertView.findViewById(R.id.isFollow);


            } else if (listViewItemType == TYPE_TEXT_IMG) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_text_img, null);
                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.tvUserPost = (TextView) convertView.findViewById(R.id.userpostDescription_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.postImage = (ImageView) convertView.findViewById(R.id.post_iv);
                view_holder.isFollow = (ImageView) convertView.findViewById(R.id.isFollow);
            } else if (listViewItemType == TYPE_IMG) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_img, null);
                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.postImage = (ImageView) convertView.findViewById(R.id.post_iv);
                view_holder.isFollow = (ImageView) convertView.findViewById(R.id.isFollow);
            } else if (listViewItemType == TYPE_VID) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_vid, null);
                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.videoView = (VideoView) convertView.findViewById(R.id.post_vid);
                view_holder.isFollow = (ImageView) convertView.findViewById(R.id.isFollow);
            }


            convertView.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) convertView.getTag();
        }

        view_holder.tvUserType.setText(bean.getUser_name());

/*
        if (bean.getUser_name().equalsIgnoreCase("")) {
            view_holder.tvUserType.setText(bean.getSource() + " User");
        } else {
            view_holder.tvUserType.setText(bean.getUser_name() + " / " + bean.getSource() + " User");
        }*/
        if (isUserDetails){
            view_holder.isFollow.setVisibility(View.GONE);
        }

        if (view_holder.isFollow != null) {
            if (bean.isFollowed()) {
                view_holder.isFollow.setImageResource(R.mipmap.blue);
            } else {
                view_holder.isFollow.setImageResource(R.mipmap.grey);
            }

            view_holder.isFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (bean.isFollowed()) {
                        callFollowUnFollowWS(bean.getId(), 2, position);
                    } else {
                        callFollowUnFollowWS(bean.getId(), 1, position);
                    }
                }
            });

        }

        view_holder.tvUserLocation.setText(bean.getPost_location());
        if (view_holder.tvUserPost != null) {
            view_holder.tvUserPost.setText(bean.getPost_text());
        }
        if (view_holder.date_time_tv != null) {
            view_holder.date_time_tv.setText(bean.getPost_time());
        }

        if (bean.getSource().equalsIgnoreCase("TWITTER")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.twit_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("INSTAGRAM")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.instagr_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("YOUTUBE")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.youtube_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("VK")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.vk_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("MEETUP")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.meetup_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("FLIKER")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.flickr_top_corner);
        }

        if (bean.getUser_image() != null && !bean.getUser_image().equalsIgnoreCase("")) {
            imageLoaderNew.displayImage(bean.getUser_image(), view_holder.userImage,
                    optionsUser,
                    null);
        }

        Constant.setFont(context, view_holder.tvUserType, 0);
        Constant.setFont(context, view_holder.tvUserLocation, 0);
        Constant.setFont(context, view_holder.tvUserPost, 0);
        Constant.setFont(context, view_holder.date_time_tv, 0);


        try {

            if (listViewItemType == TYPE_TEXT_IMG) {
                if (view_holder.postImage != null) {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image.");
                    System.out.println("Image With TExt : " + bean.getPost_image());

                    imageLoaderNew.displayImage(bean.getPost_image(), view_holder.postImage,
                            optionsPostImg,
                            null);


                } else {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image. NULL");
                }
            } else if (listViewItemType == TYPE_IMG) {
                if (view_holder.postImage != null) {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image.");
                    System.out.println("Image ONLY : " + bean.getPost_image());

                    imageLoaderNew.displayImage(bean.getPost_image(), view_holder.postImage,
                            optionsPostImg,
                            null);
                } else {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image. NULL");
                }
            } else if (listViewItemType == TYPE_VID) {
                if (view_holder.videoView != null) {
                    // TODO: 30/12/15 Play Video Here
                    System.out.println("VID ONLY : " + Constant.YOUTUBELINK + bean.getPost_video());
                    Uri vidUri = Uri.parse(Constant.YOUTUBELINK + bean.getPost_video());
                    System.out.println("vidUri : " + vidUri);
                    view_holder.videoView.setVideoURI(vidUri);
//                    view_holder.videoView.setMediaController(mMediaController);
//                    view_holder.videoView.start();
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Errorrrrrrr in Home adapter");
        }


        return convertView;
    }

    private void callFollowUnFollowWS(final String id, final int i, final int position) {

        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {


                Constant.showLoader(context);


            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(context)) {

                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {


                            System.out.println("Request of Add Follow With Cookie: " + sPref.getString(Constant.Cookie, ""));
                            String serviceUrl = "";
                            if (i == 1) {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.FOLLOW_USER) + "?id=" + id;
                                System.out.println("Request of FOLLOW_USER: " + serviceUrl);
                            } else {
                                serviceUrl = WebServiceConstants.getMethodUrl(WebServiceConstants.UN_FOLLOW_USER) + "?id=" + id;
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
                            context.runOnUiThread(new Runnable() {
                                public void run() {
                                    Constant.showToast("Server Error ", context);
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

                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", context);
                            }
                        });

                    }


                } else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(context.getResources().getString(R.string.internet), context);
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

//                            Constant.showToast("User added in followers list.", context);
                            if (i == 1) {
                                HomeBean bean = list.get(position);
                                bean.setIsFollowed(true);
                            } else {
                                HomeBean bean = list.get(position);
                                bean.setIsFollowed(false);
                            }
                            notifyDataSetChanged();

                        } else if (status == 0) {
                            Constant.showToast("Server Error    ", context);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(context);
                        }

                    } catch (Exception e) {

                        Constant.showToast("Server Error    ", context);
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error    ", context);
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
}
