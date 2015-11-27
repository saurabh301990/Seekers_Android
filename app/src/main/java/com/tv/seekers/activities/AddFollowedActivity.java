package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.tv.seekers.R;
import com.tv.seekers.adapter.AddFollowedAdapter;
import com.tv.seekers.adapter.TrackAdapter;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;

import java.io.IOException;
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
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();
        addData();

        adapter = new AddFollowedAdapter(userlist, AddFollowedActivity.this);
        listView.setAdapter(adapter);

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
