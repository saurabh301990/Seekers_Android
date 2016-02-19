package com.tv.seekers.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.adapter.NotificationAdapter;
import com.tv.seekers.bean.Notificationbean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Shoeb on 4/11/15.
 */
public class Notification extends Fragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constant.hideKeyBoard(getActivity());
    }

    ArrayList<Notificationbean> notifydata = new ArrayList<Notificationbean>();
    Notificationbean notificationbean;
    NotificationAdapter notificationAdapter;

    @Bind(R.id.txtnotification)
    TextView tvnotification;

    @Bind(R.id.notificationtoggle)
    ToggleButton notificationtgl;

    @Bind(R.id.notificationlist)
    ListView lvnotify;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.notification_screen , container,false);
        ButterKnife.bind(this,view);
//        ErrorReporter.getInstance().Init(getActivity());
        adddata();

        notificationAdapter = new NotificationAdapter(notifydata , getActivity());
        lvnotify.setAdapter(notificationAdapter);
        Constant.setFont(getActivity() , tvnotification,0);

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);
        return view;
    }


    public void adddata(){

        for (int i = 0 ; i<=5 ; i++){
            notificationbean = new Notificationbean();
            notificationbean.setNotificationtxt("It is A Dummy Data ");
            notifydata.add(notificationbean);
        }

    }
}
