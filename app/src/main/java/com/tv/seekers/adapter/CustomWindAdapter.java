package com.tv.seekers.adapter;

import android.app.Activity;
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

    public CustomWindAdapter(Activity context) {
        this.mContext = context;
        mWindow = mContext.getLayoutInflater().inflate(R.layout.custom_info_window, null);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(mContext));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.user)
                .showImageForEmptyUri(R.mipmap.user)
                .showImageOnFail(R.mipmap.user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();

    }


    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout
        render(marker, mWindow);
        return mWindow;
    }

    private void render(Marker marker, View view) {
        System.out.println("render Called");

        ImageView imgView = (ImageView) view.findViewById(R.id.badge);
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

        } else {
            userImg = snippet;
        }

        System.out.println("User Img @#@#@: " + userImg);
        System.out.println("postId @#@#@" + postId);
        System.out.println("post_text @#@#@" + post_text);
        System.out.println("isFollowed @#@#@" + isFollowed);

        imageLoaderNew.displayImage(userImg, imgView,
                options,
                null);


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

}
