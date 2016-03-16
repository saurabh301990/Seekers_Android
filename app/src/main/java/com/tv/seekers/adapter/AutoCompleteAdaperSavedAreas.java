package com.tv.seekers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.bean.MyAreasBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by shoeb on 10/3/16.
 */
public class AutoCompleteAdaperSavedAreas<T> extends ArrayAdapter<T> implements Filterable {
    private List<T> listObjects;
    List<T> suggestions = new ArrayList<>();
    private int resource;

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            System.out.println("performFiltering  Called");
            if (constraint != null) {
                suggestions.clear();
                for (T object : listObjects) {

                    if (((MyAreasBean)object).getLoc_name().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        System.out.println("FILTER RESULTs constraint.toString() :  " + constraint.toString());
                        System.out.println("FILTER RESULTs object.toString() :  " + ((MyAreasBean)object).getLoc_name());
                        suggestions.add(object);


                    }
                }

                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            if (results == null) {
                return;
            }

            System.out.println("publishResults Called");

            List<T> filteredList = (List<T>) results.values;
            if (results.count > 0) {
                clear();
                for (T filteredObject : filteredList) {
                    add(filteredObject);
                }
                notifyDataSetChanged();
            }
        }
    };

    public AutoCompleteAdaperSavedAreas(Context context, List<T> listObjects) {
        super(context, R.layout.landing_resource_row, listObjects);
        this.listObjects = new ArrayList<>(listObjects);
        this.resource = R.layout.landing_resource_row;
    }

    @Override
    public Filter getFilter() {
        System.out.println("getFilter Called");
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object listObject = getItem(position);
        viewHolder holder;
        if (convertView != null) {
            holder = (viewHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = new viewHolder(convertView);
            convertView.setTag(holder);
        }

        System.out.println("FILTER RESULTs text" + ((MyAreasBean)listObject).getLoc_name());
        System.out.println("getView Called");
        holder.name.setText( ((MyAreasBean)listObject).getLoc_name());

        return convertView;
    }


    static class viewHolder {
        @Bind(R.id.listtext)
        TextView name;

        public viewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}