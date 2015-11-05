package com.tv.seekers.menu;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.LandingAdapter;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class Landing  extends Fragment{
    LandingBean landingBean;
    LandingAdapter landingAdapter;

    ArrayList<LandingBean> listlanding = new ArrayList<LandingBean>();

    @Bind(R.id.etlandingsearchitem)
    EditText etsearch;

    @Bind(R.id.txtserachselctlo)
    TextView txtserachselectloc;

    @Bind(R.id.landinglist)
    ListView landinglist;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.landing , container , false);
        ButterKnife.bind(this,view);
        getdata();
        landingAdapter = new LandingAdapter(listlanding ,getActivity());
        landinglist.setAdapter(landingAdapter);
        setfont();
        return view;
    }

    public void getdata(){
        for (int i = 0 ; i<=20 ; i++){
            landingBean = new LandingBean();
            landingBean.setLandingplace("Hudson valley");
            listlanding.add(landingBean);
        }
    }

    public void setfont(){
        Constant.setFont(getActivity() ,txtserachselectloc ,0);
        Constant.setFont(getActivity(),etsearch,0);
    }
}
