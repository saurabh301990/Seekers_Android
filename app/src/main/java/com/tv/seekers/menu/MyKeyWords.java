package com.tv.seekers.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.MyKeywordsAdaptor;
import com.tv.seekers.bean.MyKeywordsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeyWords extends android.support.v4.app.Fragment {
    ListView listview;
    List<MyKeywordsBean> rowItem;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_keywords, container, false);
        rowItem = new ArrayList<MyKeywordsBean>();
        listview = (ListView) v.findViewById(R.id.mykeywords_list);
        // Dummy Data
        for (int i=0;i<6;i++){
            MyKeywordsBean rowItemC = new MyKeywordsBean("Bomb", true);
            rowItem.add(rowItemC);
        }

        MyKeywordsAdaptor custombaseadapter = new MyKeywordsAdaptor(getActivity(), rowItem);
        listview.setAdapter(custombaseadapter);
        
        return v;
    }
}
