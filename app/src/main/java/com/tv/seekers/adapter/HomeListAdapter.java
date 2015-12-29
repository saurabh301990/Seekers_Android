package com.tv.seekers.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.tv.seekers.R;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.utils.CircleBitmapDisplayer;

import java.util.ArrayList;

/**
 * Created by shoeb on 4/12/15.
 */
public class HomeListAdapter extends BaseAdapter {
    private ArrayList<HomeBean> list = new ArrayList<HomeBean>();
    Activity context;
    private DisplayImageOptions optionsUser;
    private DisplayImageOptions optionsPostImg;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;


    public HomeListAdapter(ArrayList<HomeBean> dataList, Activity context) {
        this.list = dataList;
        this.context = context;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        optionsUser = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.user)
                .showImageForEmptyUri(R.mipmap.user)
                .showImageOnFail(R.mipmap.user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();

        optionsPostImg = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loader)
                .showImageForEmptyUri(R.mipmap.default_post_img)
                .showImageOnFail(R.mipmap.default_post_img)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .displayer(new CircleBitmapDisplayer())
                        //				.displayer(new CircleBitmapDisplayer(Color.WHITE, 5))
                .build();
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
        return 0;
    }

    public static class ViewHolder {

        TextView tvUserType = null;
        TextView tvUserLocation = null;
        TextView tvUserPost = null;
        TextView date_time_tv = null;
        ImageView userImage = null;
        ImageView userTypeImage = null;
        ImageView postImage = null;


    }

    public static final int TYPE_TEXT = 1;
    public static final int TYPE_TEXT_IMG = 2;
    public static final int TYPE_IMG = 3;
    public static final int TYPE_VID = 4;


    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        HomeBean bean = list.get(position);

        return bean.getType();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.e("HOME ADAPTER : ", "getView Called. with POs " + position);

        ViewHolder view_holder;

        final HomeBean bean = list.get(position);
        int listViewItemType = getItemViewType(position);

        if (convertView == null) {

            view_holder = new ViewHolder();

            if (listViewItemType == TYPE_TEXT) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_text, null);

                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.tvUserPost = (TextView) convertView.findViewById(R.id.userpostDescription_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);


            } else if (listViewItemType == TYPE_TEXT_IMG) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_text_img, null);
                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.tvUserPost = (TextView) convertView.findViewById(R.id.userpostDescription_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.postImage = (ImageView) convertView.findViewById(R.id.post_iv);
            } else if (listViewItemType == TYPE_IMG) {
                convertView = context.getLayoutInflater().inflate(R.layout.home_list_item_row_img, null);
                view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
                view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
                view_holder.date_time_tv = (TextView) convertView.findViewById(R.id.date_time_tv);
                view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
                view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);
                view_holder.postImage = (ImageView) convertView.findViewById(R.id.post_iv);
            }


            convertView.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) convertView.getTag();
        }


        if (bean.getUser_name().equalsIgnoreCase("")) {
            view_holder.tvUserType.setText(bean.getSource() + " User");
        } else {
            view_holder.tvUserType.setText(bean.getUser_name() + " / " + bean.getSource() + " User");
        }

        view_holder.tvUserLocation.setText(bean.getPost_location());
        if (view_holder.tvUserPost != null) {
            view_holder.tvUserPost.setText(bean.getPost_text());
        }
        if (view_holder.date_time_tv != null) {
            view_holder.date_time_tv.setText(bean.getPost_time());
        }

        if (bean.getSource().equalsIgnoreCase("Twitter")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.twit_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("Instagram")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.instagr_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("Youtube")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.youtube_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("Vk")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.vk_top_corner);
        } else if (bean.getSource().equalsIgnoreCase("Meetup")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.meetup_top_corner);
        }

        imageLoaderNew.displayImage(bean.getUser_image(), view_holder.userImage,
                optionsUser,
                null);
        Constant.setFont(context, view_holder.tvUserType, 0);
        Constant.setFont(context, view_holder.tvUserLocation, 0);
        Constant.setFont(context, view_holder.tvUserPost, 0);
        Constant.setFont(context, view_holder.date_time_tv, 0);

        try {

            if (listViewItemType == TYPE_TEXT_IMG) {
                if (view_holder.postImage != null) {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image.");
                    imageLoaderNew.displayImage(bean.getPost_image(), view_holder.postImage,
                            optionsPostImg,
                            null);
                } else {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image. NULL");
                }
            } else if (listViewItemType == TYPE_IMG) {
                if (view_holder.postImage != null) {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image.");
                    imageLoaderNew.displayImage(bean.getPost_image(), view_holder.postImage,
                            optionsPostImg,
                            null);
                } else {
                    Log.e("HOME ADAPTER ", "Img Loader for Post Image. NULL");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }


}
