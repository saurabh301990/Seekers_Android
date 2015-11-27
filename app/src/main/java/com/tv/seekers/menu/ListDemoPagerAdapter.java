package com.tv.seekers.menu;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;

import java.util.ArrayList;

/**
 * Created by shoeb on 27/11/15.
 */
public class ListDemoPagerAdapter extends PagerAdapter {

    private Activity context;
    private ArrayList<String> _imgs = new ArrayList<String>();
    private DisplayImageOptions options;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew ;

    public ListDemoPagerAdapter(Activity _act, ArrayList<String> _imgList) {
        this.context = _act;
        this._imgs = _imgList;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loader)
                .showImageForEmptyUri(R.drawable.loader)
                .showImageOnFail(R.drawable.loader)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
    }

    @Override
    public int getCount() {
        return _imgs.size();
    }

    public Object instantiateItem(View collection, final int position) {

        ImageView view = new ImageView(context);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


        //todo img loader here
        imageLoaderNew.displayImage(_imgs.get(position),
                view,
                options,
                null);
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        ((ViewPager) collection).addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
