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
//        imgView.setImageResource(R.mipmap.user);


        String snippet = marker.getSnippet();
        System.out.println("User Img : " + snippet);

        imageLoaderNew.displayImage(snippet, imgView,
                options,
                null);


        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null && !title.equalsIgnoreCase("")) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
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
