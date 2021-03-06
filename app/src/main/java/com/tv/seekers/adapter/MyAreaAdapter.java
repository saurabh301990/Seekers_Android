package com.tv.seekers.adapter;

import android.app.Activity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
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
//    boolean isDrawOption = false;
    private SparseBooleanArray mSelectedItemsIds;
    private DisplayImageOptions options;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;

    public MyAreaAdapter(ArrayList<MyAreasBean> list, Activity context/*, boolean _isDrawOption*/) {
        this.list = list;
        this.context = context;
//        this.isDrawOption = _isDrawOption;
        mSelectedItemsIds = new SparseBooleanArray();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.demo_loc_img)
                .showImageForEmptyUri(R.mipmap.demo_loc_img)
                .showImageOnFail(R.mipmap.demo_loc_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).build();
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

    public class ViewHolder {

        public TextView loc_name_tv = null;
        public ImageView userimage = null;
       /* public TextView loc_add_tv = null;
        public ImageView area_img = null;
        public ImageView add_area_img = null;
        public RelativeLayout loc_rl = null;
        public RelativeLayout selectedlayout = null;*/

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final ViewHolder view_holder;
        if (view == null) {

            view_holder = new ViewHolder();


            view = inflater.inflate(R.layout.trackuser_row, null);

         /*   view_holder.loc_name_tv = (TextView) view.findViewById(R.id.loc_name_tv);
            view_holder.loc_add_tv = (TextView) view.findViewById(R.id.loc_add_tv);
            view_holder.area_img = (ImageView) view.findViewById(R.id.area_img);
            view_holder.add_area_img = (ImageView) view.findViewById(R.id.add_area_img);
            view_holder.loc_rl = (RelativeLayout) view.findViewById(R.id.loc_rl);
            view_holder.selectedlayout = (RelativeLayout) view.findViewById(R.id.selectedlayout);

*/
            view_holder.userimage = (ImageView) view.findViewById(R.id.trackprofileimage);
            view_holder.loc_name_tv = (TextView) view.findViewById(R.id.txtfolloweduser);
            view.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) view.getTag();
        }


        Constant.setFont(context, view_holder.loc_name_tv, 0);
//        Constant.setFont(context, view_holder.loc_add_tv, 0);
      /*  if (isDrawOption) {
            if (position == 0) {
                view_holder.loc_rl.setVisibility(View.GONE);
                view_holder.add_area_img.setVisibility(View.VISIBLE);
            } else {
                view_holder.loc_rl.setVisibility(View.VISIBLE);
                view_holder.add_area_img.setVisibility(View.GONE);
            }
        }*/

if (view_holder.userimage!=null){
    view_holder.userimage.setVisibility(View.GONE);
}
        final MyAreasBean bean = list.get(position);
        view_holder.loc_name_tv.setText(bean.getLoc_name());
//        view_holder.loc_add_tv.setText(bean.getLoc_add());

        /*if (bean.isSelected()) {
            view_holder.selectedlayout.setVisibility(View.VISIBLE);
        } else {
            view_holder.selectedlayout.setVisibility(View.GONE);
        }


        imageLoaderNew.displayImage(bean.getImg_url(), view_holder.area_img,
                options,
                null);*/

        return view;
    }

   /* public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();

    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;

    }*/
}
