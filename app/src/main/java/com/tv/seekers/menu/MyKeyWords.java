package com.tv.seekers.menu;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.MyKeywordsAdaptor;
import com.tv.seekers.bean.MyKeywordsBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeyWords extends android.support.v4.app.Fragment implements View.OnClickListener{
    ListView listview;
    List<MyKeywordsBean> rowItem;
    MyKeywordsAdaptor custombaseadapter;

    @Bind(R.id.et_mk_search)
    EditText _searchET;

    @Bind(R.id.addKeywords)
    ImageView _addIV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_keywords, container, false);
        ButterKnife.bind(this, v)                                                                                               ;
        Constant.setFont(getActivity(), _searchET,0);
        rowItem = new ArrayList<MyKeywordsBean>();
        listview = (ListView) v.findViewById(R.id.mykeywords_list);
        // Dummy Data
        for (int i=0;i<6;i++){
            MyKeywordsBean rowItemC = new MyKeywordsBean("Bomb", true);
            rowItem.add(rowItemC);
        }
        custombaseadapter = new MyKeywordsAdaptor(getActivity(), rowItem);
        listview.setAdapter(custombaseadapter);
        _addIV.setOnClickListener(this);
        
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addKeywords:
                Constant.hideKeyBoard(getActivity());
                if(!(_searchET.getText().toString().equalsIgnoreCase(""))){
                    MyKeywordsBean rowItemC = new MyKeywordsBean(_searchET.getText().toString(), true);
                    rowItem.add(rowItemC);
                    _searchET.setText("");
                    custombaseadapter.notifyDataSetChanged();
                }else{
                    Constant.showToast("Insert text",getActivity());
                }
                break;
            default:
                break;
        }
    }
}
