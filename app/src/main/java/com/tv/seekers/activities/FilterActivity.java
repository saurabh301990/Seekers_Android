package com.tv.seekers.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;

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


        }
    }
}
