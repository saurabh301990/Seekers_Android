package com.tv.seekers.menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
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
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by admin1 on 4/11/15.
 */
public class ActivityReport extends Fragment {


    private BarChart chart;
    private BarData data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report, container, false);
        ButterKnife.bind(this, view);

//        ErrorReporter.getInstance().Init(getActivity());
        chart = (BarChart) view.findViewById(R.id.chart);

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

        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);

        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetActivityReportBar();
        } else {
            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
        }


        return view;
    }

    SharedPreferences sPref;

    private void callGetActivityReportBar() {

        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject = new JSONObject();
            boolean isDateFilter = false;
            boolean isMeetUpFilter = false;
            boolean isTwitterFilter = false;
            boolean isYoutubeFilter = false;
            boolean isInstaFilter = false;
            boolean isFlikerFilter = false;
            boolean isVKFilter = false;
//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                Constant.showLoader(getActivity());

                try {
                    isMeetUpFilter = sPref.getBoolean("MEETUP", false);
                    isTwitterFilter = sPref.getBoolean("TWITTER", false);
                    isYoutubeFilter = sPref.getBoolean("YOUTUBE", false);
                    isInstaFilter = sPref.getBoolean("INSTA", false);
                    isFlikerFilter = sPref.getBoolean("FLICKER", false);
                    isVKFilter = sPref.getBoolean("VK", false);
                    isDateFilter = sPref.getBoolean("DATE", false);
                    if (isDateFilter) {
                        long STARTDATE = sPref.getLong("STARTDATE", 0);
                        if (STARTDATE > 0) {
                            STARTDATE = STARTDATE / 1000;
                        }
                        long ENDDATE = sPref.getLong("ENDDATE", 0);
                        if (ENDDATE > 0) {
                            ENDDATE = ENDDATE / 1000;
                        }

                        mJsonObject.put("startDate", STARTDATE);
                        mJsonObject.put("endDate", ENDDATE);

                    }


                    JSONArray sourcesArray = new JSONArray();
                    if (isMeetUpFilter) {
                        sourcesArray.put("MEETUP");
                    }

                    if (isTwitterFilter) {
                        sourcesArray.put("TWITTER");
                    }

                    if (isYoutubeFilter) {
                        sourcesArray.put("YOUTUBE");
                    }

                    if (isInstaFilter) {
                        sourcesArray.put("INSTAGRAM");
                    }

                    if (isFlikerFilter) {
                        sourcesArray.put("FLIKER");
                    }

                    if (isVKFilter) {
                        sourcesArray.put("VK");
                    }

                    if (sourcesArray.length() > 0) {
                        mJsonObject.put("sources", sourcesArray);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of GET_ACTIVITY_REPORT: " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {


                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_ACTIVITY_REPORT));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setConnectTimeout(80 * 1000);
                            urlConnection.setReadTimeout(80 * 1000);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            urlConnection.setRequestMethod("POST");

                            urlConnection.connect();

                            //Write
                            OutputStream outputStream = urlConnection.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            writer.write(query);
                            writer.close();
                            outputStream.close();

                            //Read
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                            String line = null;
                            StringBuilder sb = new StringBuilder();

                            while ((line = bufferedReader.readLine()) != null) {

                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();
                            System.out.println("Response of GET_ACTIVITY_REPORT : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", getActivity());
                            }
                        });

                    }


                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {
                Constant.hideLoader();
                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

                        JSONObject mJsonObject = new JSONObject(_responseMain);

                        int status = mJsonObject.getInt("status");
                        if (status == 1) {

                            JSONObject mJsonObjectdata = mJsonObject.getJSONObject("data");
                            int instagramCount = mJsonObjectdata.getInt("instagramCount");
                            int twitterCount = mJsonObjectdata.getInt("twitterCount");
                            int youtubeCount = mJsonObjectdata.getInt("youtubeCount");
                            int flickerCount = mJsonObjectdata.getInt("flickerCount");
                            int vkCount = mJsonObjectdata.getInt("vkCount");
                            int meetupCount = mJsonObjectdata.getInt("meetupCount");
                            ArrayList<BarDataSet> dataSets = null;

                            ArrayList<BarEntry> valueSet2 = new ArrayList<>();
                            BarEntry v2e1 = new BarEntry(instagramCount, 0);
                            valueSet2.add(v2e1);
                            BarEntry v2e2 = new BarEntry(twitterCount, 1);
                            valueSet2.add(v2e2);
                            BarEntry v2e3 = new BarEntry(youtubeCount, 2);
                            valueSet2.add(v2e3);
                            BarEntry v2e4 = new BarEntry(flickerCount, 3);
                            valueSet2.add(v2e4);
                            BarEntry v2e5 = new BarEntry(vkCount, 4);
                            valueSet2.add(v2e5);
                            BarEntry v2e6 = new BarEntry(meetupCount, 5);
                            valueSet2.add(v2e6);
                        /*    BarEntry v2e7 = new BarEntry(122.000f, 6);
                            valueSet2.add(v2e7);
                            BarEntry v2e8 = new BarEntry(110.000f, 7);
                            valueSet2.add(v2e8);
                            BarEntry v2e9 = new BarEntry(33.000f, 8);
                            valueSet2.add(v2e9);
                            BarEntry v2e10 = new BarEntry(144.000f, 9);
                            valueSet2.add(v2e10);*/

                            System.out.println("Size of Value Set : " + valueSet2.size());

                            BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Social Media");
                            int[] COLORFUL_COLORS = new int[]{Color.rgb(33, 133, 197), Color.rgb(126, 206, 253),
                                    Color.rgb(255, 87, 54), Color.rgb(36, 150, 92),
                                    Color.rgb(50, 84, 161), Color.rgb(177, 33, 33)/*,
                                    Color.rgb(19, 147, 255), Color.rgb(225, 188, 17),
                                    Color.rgb(255, 16, 19), Color.rgb(175, 13, 74)*/};
                            barDataSet2.setColors(COLORFUL_COLORS);

                            dataSets = new ArrayList<>();
                            dataSets.add(barDataSet2);

                            System.out.println("XVAlues : " + getXAxisValues());
                            data = new BarData(getXAxisValues(), dataSets);
                            chart.setData(data);
                        } else {

                        }


                    } catch (Exception e) {

                        e.printStackTrace();
                        Constant.showToast("Server Error ", getActivity());
                        Constant.hideLoader();
                    }
                } else {
                    Constant.showToast("Server Error ", getActivity());

                    Constant.hideLoader();
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            _Task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String[]) null);
        } else {
            _Task.execute((String[]) null);
        }
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
  /*      xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");
        xAxis.add(" ");*/
        return xAxis;
    }
}