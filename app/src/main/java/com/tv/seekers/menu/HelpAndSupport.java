package com.tv.seekers.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tv.seekers.R;

/**
 * Created by shoeb on 5/11/15.
 */
public class HelpAndSupport extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.helpandsupport,container,false);


        return view;
    }
}
