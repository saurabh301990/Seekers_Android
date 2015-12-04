package com.tv.seekers.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.tv.seekers.R;
import com.tv.seekers.bean.HomeBean;
import com.tv.seekers.bean.LandingBean;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

/**
 * Created by shoeb on 4/12/15.
 */
public class HomeListAdapter extends BaseAdapter {
    private ArrayList<HomeBean> list = new ArrayList<HomeBean>();
    Activity context;
    private DisplayImageOptions options;
    com.nostra13.universalimageloader.core.ImageLoader imageLoaderNew;


    public HomeListAdapter(ArrayList<HomeBean> dataList, Activity context) {
        this.list = dataList;
        this.context = context;
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(context));
        imageLoaderNew = com.nostra13.universalimageloader.core.ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.user)
                .showImageForEmptyUri(R.mipmap.user)
                .showImageOnFail(R.mipmap.user)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer())
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

    public class ViewHolder {

        TextView tvUserType = null;
        TextView tvUserLocation = null;
        TextView tvUserPost = null;
        ImageView userImage = null;
        ImageView userTypeImage = null;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        final ViewHolder view_holder;

        if (convertView == null) {

            view_holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.home_list_item_row, null);

            view_holder.tvUserType = (TextView) convertView.findViewById(R.id.userType_tv);
            view_holder.tvUserLocation = (TextView) convertView.findViewById(R.id.userLocation_tv);
            view_holder.tvUserPost = (TextView) convertView.findViewById(R.id.userpostDescription_tv);
            view_holder.userImage = (ImageView) convertView.findViewById(R.id.user_img_iv);
            view_holder.userTypeImage = (ImageView) convertView.findViewById(R.id.user_imgType_iv);

            convertView.setTag(view_holder);

        } else {
            view_holder = (ViewHolder) convertView.getTag();
        }

        final HomeBean bean = list.get(position);
        view_holder.tvUserType.setText(bean.getSource() + " User");
        view_holder.tvUserLocation.setText(bean.getUser_address());
        view_holder.tvUserPost.setText(bean.getPost_text());

        if (bean.getSource().equalsIgnoreCase("Twitter")) {
            view_holder.userTypeImage.setImageResource(R.mipmap.twit_top_corner);
        }

        imageLoaderNew.displayImage(bean.getUser_image(), view_holder.userImage,
                options,
                null);
        Constant.setFont(context, view_holder.tvUserType, 0);
        Constant.setFont(context, view_holder.tvUserLocation, 0);
        Constant.setFont(context, view_holder.tvUserPost, 0);


        return convertView;
    }

    class CircleBitmapDisplayer implements BitmapDisplayer {

        private float borderWidth = 0;
        private int borderColor;

        public CircleBitmapDisplayer() {
            super();
        }

        public CircleBitmapDisplayer(int borderColor, int borderWidth) {
            super();
            this.borderColor = borderColor;
            this.borderWidth = borderWidth;
        }

        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);

            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
//        paint.setColor(color);

            //--CROP THE IMAGE
            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2 - 1, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

//        //--ADD BORDER IF NEEDED
//        if(this.borderWidth &gt; 0)
//        {
//            final Paint paint2 = new Paint();
//            paint2.setAntiAlias(true);
//            paint2.setColor(this.borderColor);
//            paint2.setStrokeWidth(this.borderWidth);
//            paint2.setStyle(Paint.Style.STROKE);
//            canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, (float) (bitmap.getWidth() / 2 - Math.ceil(this.borderWidth / 2)), paint2);
//        }
            imageAware.setImageBitmap(output);
        }

    }
}
