package com.tv.seekers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.bean.Notificationbean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

/**
 * Created by dheerendra on 4/11/15.
 */
public class LandingAdapter extends BaseAdapter {

    ArrayList<LandingBean> landinglist = new ArrayList<LandingBean>();
    Activity context;


    public LandingAdapter(ArrayList<LandingBean> landinglist, Activity context) {
        this.landinglist = landinglist;
        this.context = context;
    }

    public static class ViewHolder {

        public TextView landinglocation = null;
    }

    @Override
    public int getCount() {
        return landinglist.size();
    }

    @Override
    public Object getItem(int position) {
        return landinglist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final ViewHolder view_holder;
        if (view == null) {

            view_holder = new ViewHolder();
            view = inflater.inflate(R.layout.landing_resource_row, null);
            view_holder.landinglocation = (TextView) view.findViewById(R.id.listtext);

            view.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) view.getTag();
        }


        Constant.setFont(context, view_holder.landinglocation, 0);
        final LandingBean landingBean = landinglist.get(position);
        view_holder.landinglocation.setText(landingBean.getLandingplace());


        return view;
    }
}

