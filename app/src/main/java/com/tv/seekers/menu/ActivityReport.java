package com.tv.seekers.menu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.tv.seekers.R;
import com.tv.seekers.activities.FilterActivity;
import com.tv.seekers.constant.Constant;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by admin1 on 4/11/15.
 */
public class ActivityReport extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report,container,false);
        ButterKnife.bind(this, view);

//        ErrorReporter.getInstance().Init(getActivity());
        BarChart chart = (BarChart)view.findViewById(R.id.chart);
        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription(" ");
        chart.animateXY(2000, 2000);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setScaleEnabled(false);
        chart.invalidate();

        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                int v = entry.getXIndex() + 1;
//                Toast.makeText(getActivity(), " " + v, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        MainActivity.drawerFragment.setDrawerState(true);
        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);

        ImageView rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setImageResource(R.drawable.filtr);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getActivity(), FilterActivity.class));

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Constant.hideKeyBoard(getActivity());
    }

    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;

        ArrayList<BarEntry> valueSet2 = new ArrayList<>();
        BarEntry v2e1 = new BarEntry(150.000f, 0);
        valueSet2.add(v2e1);
        BarEntry v2e2 = new BarEntry(90.000f, 1);
        valueSet2.add(v2e2);
        BarEntry v2e3 = new BarEntry(120.000f, 2);
        valueSet2.add(v2e3);
        BarEntry v2e4 = new BarEntry(60.000f, 3);
        valueSet2.add(v2e4);
        BarEntry v2e5 = new BarEntry(83.000f, 4);
        valueSet2.add(v2e5);
        BarEntry v2e6 = new BarEntry(94.000f, 5);
        valueSet2.add(v2e6);
        BarEntry v2e7 = new BarEntry(122.000f, 6);
        valueSet2.add(v2e7);
        BarEntry v2e8 = new BarEntry(110.000f, 7);
        valueSet2.add(v2e8);
        BarEntry v2e9 = new BarEntry(33.000f, 8);
        valueSet2.add(v2e9);
        BarEntry v2e10 = new BarEntry(144.000f, 9);
        valueSet2.add(v2e10);

        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Social Media");
        int[] COLORFUL_COLORS = new int[]{Color.rgb(33, 133, 197), Color.rgb(126, 206, 253),
                Color.rgb(255, 87, 54), Color.rgb(36, 150, 92),
                Color.rgb(50, 84, 161), Color.rgb(177, 33, 33),
                Color.rgb(19, 147, 255), Color.rgb(225, 188, 17),
                Color.rgb(255, 16, 19), Color.rgb(175, 13, 74)};
        barDataSet2.setColors(COLORFUL_COLORS);

        dataSets = new ArrayList<>();
        /*dataSets.add(barDataSet1);*/
        dataSets.add(barDataSet2);
        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        return xAxis;
    }
}