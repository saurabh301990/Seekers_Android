package com.tv.seekers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

/**
 * Created by Saurabh on 4/11/15.
 */
public class TrackAdapter extends BaseAdapter {
    ArrayList<TrackBean> slist = new ArrayList<TrackBean>();
    Activity context;

    public TrackAdapter(ArrayList<TrackBean> slist, Activity context) {
        this.slist = slist;
        this.context = context;
    }

    @Override
    public int getCount() {
        return slist.size();
    }

    @Override
    public Object getItem(int position) {
        return slist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {

        public TextView txtfolloweduser = null;
        public TextView txtnofolloweduser = null;
        public TextView txtnotrackuser = null;
        public ImageView userimage = null;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();

        final ViewHolder view_holder;
        if (view == null) {

            view_holder = new ViewHolder();


            view = inflater.inflate(R.layout.trackuser_row, null);

            view_holder.userimage = (ImageView) view.findViewById(R.id.trackprofileimage);
            view_holder.txtfolloweduser = (TextView) view.findViewById(R.id.txtfolloweduser);
            view_holder.txtnofolloweduser = (TextView) view.findViewById(R.id.txtnooffollowed);
            view_holder.txtnotrackuser = (TextView) view.findViewById(R.id.txttrackuser);

            Constant.setFont(context, view_holder.txtfolloweduser , 0);
            Constant.setFont(context, view_holder.txtnofolloweduser , 0);
            Constant.setFont(context, view_holder.txtnotrackuser , 0);

            view.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        final TrackBean trackBean = slist.get(position);
        view_holder.userimage.setImageResource(R.mipmap.user);
        view_holder.txtfolloweduser.setText(trackBean.getUsername());
        view_holder.txtnofolloweduser.setText(trackBean.getUserfollowed());
        view_holder.txtnotrackuser.setText(trackBean.getUsertack());


        return view;
    }
}
