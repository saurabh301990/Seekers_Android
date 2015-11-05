package com.tv.seekers.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.bean.MyKeywordsBean;
import com.tv.seekers.constant.Constant;

import java.util.List;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeywordsAdaptor extends BaseAdapter {
    Context context;
    List<MyKeywordsBean> rowItem;

    public MyKeywordsAdaptor(Context context, List<MyKeywordsBean> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
    }

    private class ViewHolder {
        TextView _title;
        ToggleButton _tglBtn;
    }

    @Override
    public int getCount() {
        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItem.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mykeywords_row, null);
            holder = new ViewHolder();
            holder._title = (TextView) convertView.findViewById(R.id.mykeywords_title);
            Constant.setFont(context,holder._title,0);
            holder._tglBtn = (ToggleButton) convertView.findViewById(R.id.mykeywords_tgl);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyKeywordsBean rowItems = (MyKeywordsBean) rowItem
                .get(position);

        holder._title.setText(rowItems.get_title());
        holder._tglBtn.setChecked(rowItems.get_tglState());

        return convertView;
    }
}
