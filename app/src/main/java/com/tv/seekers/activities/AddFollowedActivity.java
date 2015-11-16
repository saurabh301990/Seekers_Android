package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 8/11/15.
 */
public class AddFollowedActivity extends Activity {

    /*Header*/
    @Bind(R.id.tgl_menu)
    ImageView tgl_menu;

    @Bind(R.id.hdr_title)
    TextView hdr_title;

    @Bind(R.id.hdr_fltr)
    ImageView hdr_fltr;

    private String user_id = "";
    private SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_followed_screen);
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();

        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

    }

    private void setOnClick() {

    }

    private void setData() {
        hdr_title.setText(getResources().getString(R.string.addFollowedtext));
        tgl_menu.setImageResource(R.mipmap.back);
        hdr_fltr.setVisibility(View.GONE);
    }

    private void setFont() {

    }
}
