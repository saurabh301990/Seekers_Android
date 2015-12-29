package com.tv.seekers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;

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

    @Bind(R.id.userType_tv)
    TextView userType_tv;

    @Bind(R.id.userLocation_tv)
    TextView userLocation_tv;

    @Bind(R.id.post_iv)
    ImageView post_iv;

    @Bind(R.id.userpostDescription_tv)
    TextView userpostDescription_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_details_txt_img_screen);
        ButterKnife.bind(this);
        setFont();
        setData();
        setOnClick();

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
