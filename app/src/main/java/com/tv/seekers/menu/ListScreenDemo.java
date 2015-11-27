package com.tv.seekers.menu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tv.seekers.R;

import java.util.ArrayList;

/**
 * Created by shoeb on 27/11/15.
 */
public class ListScreenDemo extends Fragment {

    private ListView listView = null;
    private ArrayList<ListScreenBeanDemo> listItems = new ArrayList<ListScreenBeanDemo>();
    private ListScreenAdapter _adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_screen, container, false);

        listView = (ListView) view.findViewById(R.id.list_view);

        addData();
        return view;
    }

    private void addData() {
        ListScreenBeanDemo bean1 = new ListScreenBeanDemo();
        bean1.setImgURL1("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg");
        bean1.setImgURL2("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg");
        bean1.setImgURL3("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg");
        bean1.setImgURL4("http://www.menucool.com/slider/jsImgSlider/images/image-slider-2.jpg");
        listItems.add(bean1);

        ListScreenBeanDemo bean2 = new ListScreenBeanDemo();
        bean2.setImgURL1("https://www.wonderplugin.com/wp-content/plugins/wonderplugin-lightbox/images/demo-image2.jpg");
        bean2.setImgURL2("https://www.wonderplugin.com/wp-content/plugins/wonderplugin-lightbox/images/demo-image2.jpg");
        bean2.setImgURL3("https://www.wonderplugin.com/wp-content/plugins/wonderplugin-lightbox/images/demo-image2.jpg");
        bean2.setImgURL4("https://www.wonderplugin.com/wp-content/plugins/wonderplugin-lightbox/images/demo-image2.jpg");
        listItems.add(bean2);

        ListScreenBeanDemo bean3 = new ListScreenBeanDemo();
        bean3.setImgURL1("http://wowslider.com/sliders/demo-85/data1/images/southtyrol350698.jpg");
        bean3.setImgURL2("http://wowslider.com/sliders/demo-85/data1/images/southtyrol350698.jpg");
        bean3.setImgURL3("http://wowslider.com/sliders/demo-85/data1/images/southtyrol350698.jpg");
        bean3.setImgURL4("http://wowslider.com/sliders/demo-85/data1/images/southtyrol350698.jpg");
        listItems.add(bean3);

        ListScreenBeanDemo bean4 = new ListScreenBeanDemo();
        bean4.setImgURL1("http://www.menucool.com/slider/jsImgSlider/images/image-slider-4.jpg");
        bean4.setImgURL2("http://www.menucool.com/slider/jsImgSlider/images/image-slider-4.jpg");
        bean4.setImgURL3("http://www.menucool.com/slider/jsImgSlider/images/image-slider-4.jpg");
        bean4.setImgURL4("http://www.menucool.com/slider/jsImgSlider/images/image-slider-4.jpg");
        listItems.add(bean4);

        _adapter = new ListScreenAdapter(getActivity(), listItems);
        listView.setAdapter(_adapter);

    }

}
