package com.tv.seekers.menu;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.activities.AddFollowedActivity;
import com.tv.seekers.adapter.TrackAdapter;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class Track extends Fragment {
    @Nullable
    private ArrayList<TrackBean> userlist = new ArrayList<TrackBean>();
    TrackBean trackBean;
    TrackAdapter trackAdapter;


    @Bind(R.id.search_et)
    EditText search_et;

    @Bind(R.id.lvtrack)
    ListView listuser;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private TextView _header;
    private ImageView rightIcon;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.track, container, false);
        ButterKnife.bind(this, view);
        addData();

        System.out.println("onCreateView Called");

        _header = (TextView) getActivity().findViewById(R.id.hdr_title);
        _header.setText("Followed");
        rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setImageResource(R.mipmap.plus);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getActivity(), AddFollowedActivity.class));

            }
        });


        trackAdapter = new TrackAdapter(userlist, getActivity());
        listuser.setAdapter(trackAdapter);

        listuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                replaceFragment();
            }
        });

        setFont();

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

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);
        return view;
    }

    private void setFont() {
        Constant.setFont(getActivity(), search_et, 0);
        Constant.setFont(getActivity(), _header, 0);
    }

    private void replaceFragment() {

        TrackMapFragment fragment = new TrackMapFragment();
        if (fragment != null) {


            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();


        }
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
