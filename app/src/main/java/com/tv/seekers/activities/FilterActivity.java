package com.tv.seekers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class FilterActivity extends Activity implements View.OnClickListener {
    //Textview
    @Bind(R.id.txtcancel)
    TextView txtheadercancel;

    @Bind(R.id.txtfilter)
    TextView txtheaderfilter;

    @Bind(R.id.txtapply)
    TextView txtheaderapply;

    @Bind(R.id.txtfilterbydate)
    TextView filterbydatetxt;

    @Bind(R.id.txtstartdate)
    TextView startdatetext;

    @Bind(R.id.txtSdatetime)
    TextView Sdatetime;

    @Bind(R.id.txtenddate)
    TextView enddatetext;

    @Bind(R.id.txtEdatetime)
    TextView Edatetime;

    @Bind(R.id.fbtext)
    TextView fbtext;

    @Bind(R.id.twittertxt)
    TextView twittertext;

    @Bind(R.id.youtubetxt)
    TextView youtubetxt;

    @Bind(R.id.instatext)
    TextView instatext;

    @Bind(R.id.flickrtext)
    TextView flickertxt;

    @Bind(R.id.tumblrtext)
    TextView tumblrtxt;

    @Bind(R.id.vinetxt)
    TextView vinetxt;

    @Bind(R.id.vktext)
    TextView vktext;

    @Bind(R.id.disquxtext)
    TextView disquxtext;

    @Bind(R.id.txtfilterbykeyword)
    TextView filterbykeywordtxt;

    @Bind(R.id.txtdate)
    TextView datetxt;

    @Bind(R.id.txtnetwork)
    TextView networktxt;

    @Bind(R.id.txtkeyword)
    TextView keywordtxt;


    @Bind(R.id.filterdatetoggle)
    ToggleButton filterbydatetgl;

    @Bind(R.id.fbtoggle)
    ToggleButton fbtgl;

    @Bind(R.id.twittertoggle)
    ToggleButton twittertoggle;

    @Bind(R.id.youtubetoggle)
    ToggleButton youtubetoggle;

    @Bind(R.id.instatoggle)
    ToggleButton instatgl;

    @Bind(R.id.filkertoggle)
    ToggleButton flickertgl;

    @Bind(R.id.tumbletoogle)
    ToggleButton tumblertgl;

    @Bind(R.id.vinetoggle)
    ToggleButton vinetgl;

    @Bind(R.id.vktoggle)
    ToggleButton vktgl;


    @Bind(R.id.disquxtoogle)
    ToggleButton disquxtgl;


    @Bind(R.id.filterbykeywordtoggle)
    ToggleButton filterbykeywordtgl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        ButterKnife.bind(this);

        setfont();
        setonclick();
    }

    public void setonclick(){
        txtheadercancel.setOnClickListener(this);
        txtheaderapply.setOnClickListener(this);
    }

    public void setfont() {
        Constant.setFont(FilterActivity.this, txtheadercancel, 0);
        Constant.setFont(FilterActivity.this, txtheaderfilter, 0);
        Constant.setFont(FilterActivity.this, txtheaderapply, 0);
        Constant.setFont(FilterActivity.this, filterbydatetxt, 0);
        Constant.setFont(FilterActivity.this, startdatetext, 0);
        Constant.setFont(FilterActivity.this, enddatetext, 0);
        Constant.setFont(FilterActivity.this, Sdatetime, 0);
        Constant.setFont(FilterActivity.this, Edatetime, 0);
        Constant.setFont(FilterActivity.this, fbtext, 0);
        Constant.setFont(FilterActivity.this, twittertext, 0);
        Constant.setFont(FilterActivity.this, youtubetxt, 0);
        Constant.setFont(FilterActivity.this, instatext, 0);
        Constant.setFont(FilterActivity.this, tumblrtxt, 0);
        Constant.setFont(FilterActivity.this, vinetxt, 0);
        Constant.setFont(FilterActivity.this, vktext, 0);
        Constant.setFont(FilterActivity.this, disquxtext, 0);
        Constant.setFont(FilterActivity.this, filterbykeywordtxt, 0);
        Constant.setFont(FilterActivity.this, datetxt, 0);
        Constant.setFont(FilterActivity.this, networktxt, 0);
        Constant.setFont(FilterActivity.this, keywordtxt, 0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.filterdatetoggle:

                break;

            case R.id.fbtoggle:

                break;
            case R.id.twittertoggle:

                break;
            case R.id.youtubetoggle:

                break;
            case R.id.instatoggle:

                break;
            case R.id.filkertoggle:

                break;
            case R.id.tumbletoogle:

                break;
            case R.id.vinetoggle:

                break;
            case R.id.vktoggle:

                break;
            case R.id.disquxtoogle:

                break;

            case R.id.filterbykeywordtoggle:

                break;
            case R.id.txtcancel:
                finish();

                break;
            case R.id.txtapply:

                finish();
                break;


        }
    }
}