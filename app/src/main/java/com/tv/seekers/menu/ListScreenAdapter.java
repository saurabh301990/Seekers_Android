package com.tv.seekers.menu;

import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tv.seekers.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by shoeb on 27/11/15.
 */
public class ListScreenAdapter extends BaseAdapter {
    private ArrayList<ListScreenBeanDemo> listItems = new ArrayList<ListScreenBeanDemo>();
    private Activity _context;


    //TIMER
    Timer timer;
    int _page = 0;
    int _pageOG = 0;

    private MyCustomTimer myTimer;

    public ListScreenAdapter(Activity _context, ArrayList<ListScreenBeanDemo> _get_data) {
        this._context = _context;
        this.listItems = _get_data;
        myTimer = new MyCustomTimer();
        _pageOG = listItems.size();

    }


    @Override
    public int getCount() {
        return listItems.size();
    }


    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

       ViewHolder _viewHolder;


        if (convertView == null) {
            _viewHolder = new ViewHolder();
            LayoutInflater _layInflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = _layInflater.inflate(R.layout.list_screen_row_item_demo, null);

            _viewHolder._pager = (ViewPager) convertView.findViewById(R.id.pager_images);


            convertView.setTag(_viewHolder);

        } else {
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        //setting data
        ListScreenBeanDemo _bean = listItems.get(position);
        ArrayList<String> _imgList = new ArrayList<String>();
        if (_bean.getImgURL1() != null && !_bean.getImgURL1().equalsIgnoreCase("")) {
            _imgList.add(_bean.getImgURL1());
        }
        if (_bean.getImgURL2() != null && !_bean.getImgURL2().equalsIgnoreCase("")) {
            _imgList.add(_bean.getImgURL2());
        }
        if (_bean.getImgURL3() != null && !_bean.getImgURL3().equalsIgnoreCase("")) {
            _imgList.add(_bean.getImgURL3());
        }
        if (_bean.getImgURL4() != null && !_bean.getImgURL4().equalsIgnoreCase("")) {
            _imgList.add(_bean.getImgURL4());
        }
        if (_imgList.size() > 0) {

            ListDemoPagerAdapter _adapter = new ListDemoPagerAdapter(_context, _imgList);
            _viewHolder._pager.setAdapter(_adapter);
            if ((position & 1) == 0) {
                //  even...
                _viewHolder._pager.setCurrentItem(0);
            } else {
                //    odd...
                _viewHolder._pager.setCurrentItem(_imgList.size());
            }

        }


        //todo  timer code here

        long diff = System.currentTimeMillis() + 999999999;
        myTimer.setTimer(_viewHolder._pager, diff);
        /*timer = new Timer();
        timer.schedule(new TimerTask() {


            @Override
            public void run() {

                if (_context != null) {
                    _context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            //System.out.println("Timer  Count : " + _page + " / " + _pageOG);

                            if (_page == _pageOG) {
                                _page = 0;

                            }

                            _viewHolder._pager.setCurrentItem(_page, true);

                            _page = _page + 1;


                        }
                    });
                }


            }
        }, 0, 3000);//3 seconds*/
        return convertView;
    }


    public static class ViewHolder {

        ViewPager _pager = null;

    }

    public class MyCustomTimer {
        public MyCustomTimer() {
        }

        public void setTimer(final ViewPager _viewPager, long dateEnd) {

            new CountDownTimer(dateEnd, 10) {
                public void onTick(long millisUntilFinished) {
                    if (_page == _pageOG) {
                        _page = 0;

                    }

                    _viewPager.setCurrentItem(_page, true);

                    _page = _page + 1;

                }

                public void onFinish() {
                    System.out.println("On Finish Called");
                }
            }.start();
        }
    }
}
