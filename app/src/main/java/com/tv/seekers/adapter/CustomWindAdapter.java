package com.tv.seekers.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;

/**
 * Created by shoeb on 22/12/15.
 */
public class CustomWindAdapter
        implements GoogleMap.InfoWindowAdapter {

    private View mWindow;
    private Activity mContext;
    private DisplayImageOptions options;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;
    private boolean isImageLoaded = false;

    public CustomWindAdapter(Activity context) {
        this.mContext = context;
        mWindow = mContext.getLayoutInflater().inflate(R.layout.custom_info_window, null);
//        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();


    }


    @Override
    public View getInfoWindow(Marker marker) {
        System.out.println("getInfoWindow");
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        System.out.println("getInfoContents");
        // Getting view from the layout file info_window_layout
        render(marker, mWindow);

    /*    ImageView followed_iv = (ImageView) mWindow.findViewById(R.id.followed_iv);
        if (followed_iv != null) {
            followed_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.showToast("Star Clicked New", mContext);
                }
            });
        }*/
        return mWindow;
    }

    String userImg = "";
    ImageView imgView;


    private void render(Marker marker, View view) {
        System.out.println("render Called");
        /*options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.user)
                .showImageForEmptyUri(R.mipmap.user)
                .showImageOnFail(R.mipmap.user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();*/
        imgView = (ImageView) view.findViewById(R.id.badge);
        ImageView followed_iv = (ImageView) view.findViewById(R.id.followed_iv);
        if (followed_iv != null) {
            followed_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.showToast("Star Clicked", mContext);
                }
            });
        }
//        imgView.setImageResource(R.mipmap.user);


        String snippet = marker.getSnippet();
        System.out.println("snippet : " + snippet);

        String postId = "";
        String isFollowed = "";

        String post_text = "";
        if (snippet.contains("&")) {
            String[] snippetWHole = snippet.split("&");

            isFollowed = snippetWHole[0];
            userImg = snippetWHole[1];
            postId = snippetWHole[2];
            if (snippetWHole.length > 3) {
                post_text = snippetWHole[3];
            } else {
                post_text = "";
            }


        } else {
            userImg = snippet;
        }

        System.out.println("User Img @#@#@: " + userImg);
        System.out.println("postId @#@#@" + postId);
        System.out.println("post_text @#@#@" + post_text);
        System.out.println("isFollowed @#@#@" + isFollowed);
        if (userImg != null) {

            /*mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageLoaderNew.displayImage(userImg, imgView,
                            options,
                            new SimpleImageLoadingListener() {
                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    // Do whatever you want with Bitmap
                                    System.out.println("IMAGELOADER : onLoadingComplete");
                                }
                            });
                }
            });*/


            if (isImageLoaded) {
                isImageLoaded = false;
                Picasso.with(mContext)
                        .load(userImg)
                        .placeholder(R.mipmap.user)
                        .error(R.mipmap.user)
                        .into(imgView);
            } else {
                isImageLoaded = true;
                Picasso.with(mContext)
                        .load(userImg)
                        .placeholder(R.mipmap.user)
                        .error(R.mipmap.user)
                        .into(imgView, new InfoWindowRefresher(marker));
            }


        }


        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        TextView post_text_tv = ((TextView) view.findViewById(R.id.post_text_tv));
        if (title != null && !title.equalsIgnoreCase("")) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

        if (post_text_tv != null) {
            if (post_text != null && !post_text.equalsIgnoreCase("")) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(post_text);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                post_text_tv.setText(titleText);
            }
        }

        if (followed_iv != null) {
            if (isFollowed != null && !isFollowed.equalsIgnoreCase("")) {
                if (isFollowed.equalsIgnoreCase("true")) {
                    followed_iv.setImageResource(R.mipmap.blue);
                } else {
                    followed_iv.setImageResource(R.mipmap.grey);
                }
            }
        }

        /*String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null && snippet.length() > 12) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }*/
    }

    public class InfoWindowRefresher implements Callback {
        private Marker markerToRefresh;

        public InfoWindowRefresher(Marker markerToRefresh) {
            this.markerToRefresh = markerToRefresh;
        }

        @Override
        public void onSuccess() {
            markerToRefresh.showInfoWindow();
        }

        @Override
        public void onError() {
        }
    }

}
