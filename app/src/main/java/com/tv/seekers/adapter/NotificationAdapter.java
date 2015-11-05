package com.tv.seekers.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.bean.Notificationbean;
import com.tv.seekers.bean.TrackBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

/**
 * Created by dheerendra on 4/11/15.
 */
public class NotificationAdapter extends BaseAdapter {

    ArrayList<Notificationbean> notifylist = new ArrayList<Notificationbean>();
    Activity context;

    public NotificationAdapter(ArrayList<Notificationbean> notifylist, Activity context) {
        this.notifylist = notifylist;
        this.context = context;
    }


    public static class ViewHolder{

        public TextView Notifytxt = null;

    }
    @Override
    public int getCount() {
        return notifylist.size();
    }

    @Override
    public Object getItem(int position) {
        return notifylist.get(position);
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


            view = inflater.inflate(R.layout.notification_row, null);


            view_holder.Notifytxt = (TextView) view.findViewById(R.id.notifytext);
            Constant.setFont(context, view_holder.Notifytxt, 0);


            view.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) view.getTag();
        }

        final Notificationbean notificationbean = notifylist.get(position);

        view_holder.Notifytxt.setText(notificationbean.getNotificationtxt());


        return view;
    }
}
