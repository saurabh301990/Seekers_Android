package com.tv.seekers.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.tv.seekers.R;
import com.tv.seekers.activities.FilterActivity;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.gpsservice.GPSTracker;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 4/11/15.
 */
public class ActivityReport extends Fragment {

    @Bind(R.id.lineChart)
    LineChart lineChart;


    @Bind(R.id.chart)
    BarChart chart;


    private BarData data;
    private SharedPreferences sPref;
    boolean isDateFilter = false;
    boolean isMeetUpFilter = false;
    boolean isTwitterFilter = false;
    boolean isYoutubeFilter = false;
    boolean isInstaFilter = false;
    boolean isFlikerFilter = false;
    boolean isVKFilter = false;

    private ArrayList<Boolean> listOfBooleans = new ArrayList<Boolean>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_report, container, false);
        ButterKnife.bind(this, view);


//        ErrorReporter.getInstance().Init(getActivity());


        lineChart.setDescription(" ");
        lineChart.animateXY(2000, 2000);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setScaleEnabled(false);
        lineChart.invalidate();


        chart.setDescription(" ");
        chart.animateXY(2000, 2000);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setScaleEnabled(false);
        /*chart.setClickable(false);
        chart.setHovered(false);*/
        /*chart.setDrawBarShadow(false);
        chart.setDrawHighlightArrow(false);
        chart.setDrawingCacheEnabled(false);
        chart.setFocusableInTouchMode(false);
        chart.setFocusable(false);*/

        chart.invalidate();
/*
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                int v = entry.getXIndex() + 1;
//                Toast.makeText(getActivity(), " " + v, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });*/

        MainActivity.drawerFragment.setDrawerState(true);
        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);

        ImageView rightIcon = (ImageView) getActivity().findViewById(R.id.hdr_fltr);
        rightIcon.setImageResource(R.drawable.filtr);
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intToFilter = new Intent(getActivity(), FilterActivity.class);
                startActivityForResult(intToFilter, 444);


            }
        });

        callActivityReport();

        return view;
    }

    private void callActivityReport() {
        sPref = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        isMeetUpFilter = sPref.getBoolean("MEETUP", false);
        isTwitterFilter = sPref.getBoolean("TWITTER", false);
        isYoutubeFilter = sPref.getBoolean("YOUTUBE", false);
        isInstaFilter = sPref.getBoolean("INSTA", false);
        isFlikerFilter = sPref.getBoolean("FLICKR", false);
        isVKFilter = sPref.getBoolean("VK", false);


        if (listOfBooleans.size() > 0) {
            listOfBooleans.clear();
        }
        listOfBooleans.add(isMeetUpFilter);
        listOfBooleans.add(isTwitterFilter);
        listOfBooleans.add(isYoutubeFilter);
        listOfBooleans.add(isInstaFilter);
        listOfBooleans.add(isFlikerFilter);
        listOfBooleans.add(isVKFilter);

        int trueCount = 0;

        for (int i = 0; i < listOfBooleans.size(); i++) {

            if (listOfBooleans.get(i)) {
                trueCount = trueCount + 1;
            }
        }

        System.out.println("Final True Count : " + trueCount);


        isDateFilter = sPref.getBoolean("DATE", false);


        if (trueCount == 1) {
            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                chart.setVisibility(View.GONE);
                lineChart.setVisibility(View.VISIBLE);
                callGetActivityReportLineChart();
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }

        } else {
            if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                chart.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.GONE);
                callGetActivityReportBar();
            } else {
                Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 444) {
            if (resultCode == Activity.RESULT_OK) {
                boolean result = data.getBooleanExtra("applied", false);
                if (result) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(this).attach(this).commit();
//                    callActivityReport();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("onDetach");
    }

    private void demoDataLineChart() {
        // creating list of entry
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0));
        entries.add(new Entry(8f, 1));
        entries.add(new Entry(6f, 2));
        entries.add(new Entry(2f, 3));
        entries.add(new Entry(18f, 4));
        entries.add(new Entry(9f, 5));

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");
        // creating labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");
        LineData data = new LineData(labels, dataset);
        lineChart.setData(data);
    }

    private void callGetActivityReportLineChart() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            String sourceString = "";
            JSONObject mJsonObject = new JSONObject();

//            Uri.Builder builder;

            @Override
            protected void onPreExecute() {

                Constant.showLoader(getActivity());

                try {
                    isMeetUpFilter = sPref.getBoolean("MEETUP", false);
                    isTwitterFilter = sPref.getBoolean("TWITTER", false);
                    isYoutubeFilter = sPref.getBoolean("YOUTUBE", false);
                    isInstaFilter = sPref.getBoolean("INSTA", false);
                    isFlikerFilter = sPref.getBoolean("FLICKR", false);
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


                    if (isMeetUpFilter) {
                        mJsonObject.put("source", "MEETUP");
                        sourceString = "MEETUP";
                    } else if (isTwitterFilter) {
                        mJsonObject.put("source", "TWITTER");
                        sourceString = "TWITTER";
                    } else if (isYoutubeFilter) {
                        mJsonObject.put("source", "YOUTUBE");
                        sourceString = "YOUTUBE";
                    } else if (isInstaFilter) {
                        mJsonObject.put("source", "INSTAGRAM");
                        sourceString = "INSTAGRAM";
                    } else if (isFlikerFilter) {
                        mJsonObject.put("source", "FLIKER");
                        sourceString = "FLICKR";
                    } else if (isVKFilter) {
                        mJsonObject.put("source", "VK");
                        sourceString = "VK";
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }

                System.out.println("Request of GET_ACTIVITY_REPORT_SINGLE: " + mJsonObject.toString());

            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {

                    try {

                        HttpURLConnection urlConnection;


                        try {


                            String query = mJsonObject.toString();

                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_ACTIVITY_REPORT_SINGLE));
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
                            System.out.println("Response of GET_ACTIVITY_REPORT_SINGLE : " + _responseMain);


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
                                Constant.hideLoader();
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

                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

                        JSONObject mJsonObject = new JSONObject(_responseMain);

                        int status = mJsonObject.getInt("status");
                        if (status == 1) {

                            JSONObject mJsonObjectdata = mJsonObject.getJSONObject("data");
                            JSONArray mJsonArrayrecords = mJsonObjectdata.getJSONArray("records");
                            int lengthOfArray = mJsonArrayrecords.length();
                            if (lengthOfArray > 0) {

                                ArrayList<Entry> entriesCount = new ArrayList<>();
                                // creating labels
                                ArrayList<String> labelsDate = new ArrayList<String>();
                                for (int i = 0; i < lengthOfArray; i++) {
                                    JSONObject mJsonObjectSub = mJsonArrayrecords.getJSONObject(i);
                                    long timestamp = mJsonObjectSub.getLong("timestamp");
                                    long count = mJsonObjectSub.getLong("count");
                                    entriesCount.add(new Entry(count, i));
                                    labelsDate.add(getDateFromMilliseconds(timestamp * 1000, "dd MMM"));
                                }


                                LineDataSet dataset = new LineDataSet(entriesCount, sourceString);
//                                dataset.setDrawFilled(true);


                                final LineData data = new LineData(labelsDate, dataset);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        lineChart.setData(data);
                                        lineChart.requestFocus();
                                    }
                                });

                                Constant.hideLoader();
                            }

                        } else if (status == 0) {
                            Constant.hideLoader();
                            Constant.showToast("Server Error", getActivity());
                        } else if (status == -1) {
                            Constant.hideLoader();
                            //Redirect to Login
                            Constant.alertForLogin(getActivity());
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

    private String getDateFromMilliseconds(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


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
                    isFlikerFilter = sPref.getBoolean("FLICKR", false);
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
                                Constant.hideLoader();
                                Constant.showToast("Server Error ", getActivity());
                            }
                        });

                    }


                } else {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Constant.hideLoader();
                            // TODO Auto-generated method stub
                            Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                        }
                    });
                }
                return null;

            }

            @Override
            protected void onPostExecute(String result) {

                if (_responseMain != null && !_responseMain.equalsIgnoreCase("")) {

                    try {

                        JSONObject mJsonObject = new JSONObject(_responseMain);

                        int status = mJsonObject.getInt("status");
                        if (status == 1) {

                            JSONObject mJsonObjectdata = mJsonObject.getJSONObject("data");
                            int instagramCount = mJsonObjectdata.getInt("instagramCount");
                            int twitterCount = mJsonObjectdata.getInt("twitterCount");
                            int youtubeCount = mJsonObjectdata.getInt("youtubeCount");
                            int FLICKRCount = mJsonObjectdata.getInt("flickerCount");
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
                            BarEntry v2e4 = new BarEntry(FLICKRCount, 3);
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


                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chart.setData(data);
                                    chart.getData().setHighlightEnabled(false);
                                    chart.requestFocus();
                                }
                            });

                            Constant.hideLoader();
                        } else if (status == 0) {
                            Constant.hideLoader();
                            Constant.showToast("Server Error", getActivity());
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.hideLoader();
                            Constant.alertForLogin(getActivity());
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