package com.tv.seekers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.bean.MyAreasBean;
import com.tv.seekers.bean.Notificationbean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

/**
 * Created by shoeb on 21/11/15.
 */
public class MyAreaAdapter extends BaseAdapter {
    private ArrayList<MyAreasBean> list = new ArrayList<MyAreasBean>();
    Activity context;

    public MyAreaAdapter(ArrayList<MyAreasBean> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {

        public TextView loc_name_tv = null;
        public TextView loc_add_tv = null;
        public ImageView area_img = null;
        public ImageView add_area_img = null;
        public RelativeLayout loc_rl = null;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final ViewHolder view_holder;
        if (view == null) {

            view_holder = new ViewHolder();


            view = inflater.inflate(R.layout.my_areas_row_item, null);

            view_holder.loc_name_tv = (TextView) view.findViewById(R.id.loc_name_tv);
            view_holder.loc_add_tv = (TextView) view.findViewById(R.id.loc_add_tv);
            view_holder.area_img = (ImageView) view.findViewById(R.id.area_img);
            view_holder.add_area_img = (ImageView) view.findViewById(R.id.add_area_img);
            view_holder.loc_rl = (RelativeLayout) view.findViewById(R.id.loc_rl);


            view.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) view.getTag();
        }


        Constant.setFont(context, view_holder.loc_name_tv, 0);
        Constant.setFont(context, view_holder.loc_add_tv, 0);
        if (position == 0) {
            view_holder.loc_rl.setVisibility(View.GONE);
            view_holder.add_area_img.setVisibility(View.VISIBLE);
        }

        final MyAreasBean bean = list.get(position);
        view_holder.loc_name_tv.setText(bean.getLoc_name());
        view_holder.loc_add_tv.setText(bean.getLoc_add());


        return view;
    }
}
