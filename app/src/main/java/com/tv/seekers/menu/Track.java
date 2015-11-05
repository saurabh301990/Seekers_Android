package com.tv.seekers.menu;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.TrackAdapter;
import com.tv.seekers.bean.TrackBean;

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


    @Bind(R.id.etlandingsearch)
    EditText etlandingsearch;

    @Bind(R.id.lvtrack)
    ListView listuser;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.track, container, false);
        ButterKnife.bind(this, view);
        addData();


        trackAdapter = new TrackAdapter(userlist, getActivity());
        listuser.setAdapter(trackAdapter);

        listuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                replaceFragment();
            }
        });
        return view;
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
