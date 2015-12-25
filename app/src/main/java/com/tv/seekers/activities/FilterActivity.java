package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.date.DateTime;
import com.tv.seekers.date.DateTimePicker;
import com.tv.seekers.date.SimpleDateTimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Saurabh on 4/11/15.
 */
public class FilterActivity extends FragmentActivity
        implements View.OnClickListener,
        DateTimePicker.OnDateTimeSetListener {
    //Textview
    @Bind(R.id.txtcancel)
    TextView txtheadercancel;

    @Bind(R.id.txtfilter)
    TextView txtheaderfilter;

    @Bind(R.id.txtapply)
    TextView txtheaderapply;

    @Bind(R.id.txtfilterbydate)
    TextView filterbydatetxt;

    @Bind(R.id.txtstartdate)
    TextView startdatetext;

    @Bind(R.id.txtSdatetime)
    TextView Sdatetime;

    @Bind(R.id.txtenddate)
    TextView enddatetext;

    @Bind(R.id.txtEdatetime)
    TextView Edatetime;

    @Bind(R.id.fbtext)
    TextView fbtext;

    @Bind(R.id.twittertxt)
    TextView twittertext;

    @Bind(R.id.youtubetxt)
    TextView youtubetxt;

    @Bind(R.id.instatext)
    TextView instatext;

    @Bind(R.id.flickrtext)
    TextView flickertxt;

    @Bind(R.id.tumblrtext)
    TextView tumblrtxt;

    @Bind(R.id.vinetxt)
    TextView vinetxt;

    @Bind(R.id.vktext)
    TextView vktext;

    @Bind(R.id.disquxtext)
    TextView disquxtext;

    @Bind(R.id.txtfilterbykeyword)
    TextView filterbykeywordtxt;

    @Bind(R.id.txtdate)
    TextView datetxt;

    @Bind(R.id.txtnetwork)
    TextView networktxt;

    @Bind(R.id.txtkeyword)
    TextView keywordtxt;


    @Bind(R.id.filterdatetoggle)
    ToggleButton filterbydatetgl;

  /*  @Bind(R.id.fbtoggle)
    ToggleButton fbtgl;*/

    @Bind(R.id.meetUpTgl)
    ToggleButton meetUpTgl;

    @Bind(R.id.twittertoggle)
    ToggleButton twittertoggle;

    @Bind(R.id.youtubetoggle)
    ToggleButton youtubetoggle;

    @Bind(R.id.instatoggle)
    ToggleButton instatgl;

    @Bind(R.id.filkertoggle)
    ToggleButton flickertgl;

    @Bind(R.id.tumbletoogle)
    ToggleButton tumblertgl;

    @Bind(R.id.vinetoggle)
    ToggleButton vinetgl;

    @Bind(R.id.vktoggle)
    ToggleButton vktgl;


    @Bind(R.id.disquxtoogle)
    ToggleButton disquxtgl;


    @Bind(R.id.filterbykeywordtoggle)
    ToggleButton filterbykeywordtgl;

    private String user_id = "";
    private SharedPreferences sPref;
    private boolean isDate = false;
    private boolean isTime = false;
    private String start_date = "";
    private String end_date = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = sPref.getString("id", "");

        setfont();
        setonclick();

        if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
            callGetUserFilterWS();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
        }


    }

    private void callGetUserFilterWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(FilterActivity.this);

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id);
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(
                                    WebServiceConstants.GET_USER_FILTER));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setReadTimeout(5000);


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
                            System.out.println("Response of Get User Filter : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", FilterActivity.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", FilterActivity.this);
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

                        JSONObject jsonObject = new JSONObject(_responseMain);
                        if (jsonObject.has("status")) {
                            int _status = jsonObject.getInt("status");
                            if (_status == 1) {
                                JSONArray jsonArray = jsonObject.getJSONArray("user_filter");
                                if (jsonArray.length() > 0) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                    String id = jsonObject1.getString("id");
                                    String user_id = jsonObject1.getString("user_id");
//                                    String facebook = jsonObject1.getString("facebook");
                                    String twitter = jsonObject1.getString("twitter");
                                    String instagram = jsonObject1.getString("instagram");
                                    String youtube = jsonObject1.getString("youtube");
                                    String meetup = jsonObject1.getString("meetup");
                                    String vk = jsonObject1.getString("vk");
                                    String start_date = jsonObject1.getString("start_date");
                                    String end_date = jsonObject1.getString("end_date");
                                    String filter_by_date = jsonObject1.getString("filter_by_date");
                                    String filter_by_keyword = jsonObject1.getString("filter_by_keyword");


                                    String inputPattern = "yyyy-MM-dd hh:mm:ss";

                                    String outputPattern = "dd MMMM yyyy hh:mm a";
                                    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
                                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

                                    Date mdate = null;
                                    String str = null;

                                    try {
                                        mdate = inputFormat.parse(end_date);
                                        String mend_date = outputFormat.format(mdate);
                                        System.out.println("Final DAte : in New format : " + mend_date);
                                        Edatetime.setText(mend_date);


                                        mdate = inputFormat.parse(start_date);
                                        String mStartDate = outputFormat.format(mdate);
                                        System.out.println("Final DAte : in New format : " + mStartDate);
                                        Sdatetime.setText(mStartDate);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }


                                    if (!filter_by_keyword.equalsIgnoreCase("0") &&
                                            !filter_by_keyword.equalsIgnoreCase("")) {
                                        filterbykeywordtgl.setChecked(true);
                                    } else {
                                        filterbykeywordtgl.setChecked(false);
                                    }

                                    if (!filter_by_date.equalsIgnoreCase("0") &&
                                            !filter_by_date.equalsIgnoreCase("")) {
                                        filterbydatetgl.setChecked(true);
                                    } else {
                                        filterbydatetgl.setChecked(false);
                                    }

                                    if (!vk.equalsIgnoreCase("0") &&
                                            !vk.equalsIgnoreCase("")) {
                                        vktgl.setChecked(true);
                                    } else {
                                        vktgl.setChecked(false);
                                    }


                                    if (!meetup.equalsIgnoreCase("0") &&
                                            !meetup.equalsIgnoreCase("")) {
                                        meetUpTgl.setChecked(true);
                                    } else {
                                        meetUpTgl.setChecked(false);
                                    }

                                    if (!twitter.equalsIgnoreCase("0") && !twitter.equalsIgnoreCase("")) {

                                        twittertoggle.setChecked(true);
                                    } else {
                                        twittertoggle.setChecked(false);
                                    }

                                    if (!instagram.equalsIgnoreCase("0") && !instagram.equalsIgnoreCase("")) {

                                        instatgl.setChecked(true);
                                    } else {
                                        instatgl.setChecked(false);
                                    }

                                    if (!youtube.equalsIgnoreCase("0") && !youtube.equalsIgnoreCase("")) {

                                        youtubetoggle.setChecked(true);
                                    } else {
                                        youtubetoggle.setChecked(false);
                                    }


                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {

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

    public void setonclick() {
        txtheadercancel.setOnClickListener(this);
        txtheaderapply.setOnClickListener(this);
        Sdatetime.setOnClickListener(this);
        Edatetime.setOnClickListener(this);
    }

    public void setfont() {
        Constant.setFont(FilterActivity.this, txtheadercancel, 0);
        Constant.setFont(FilterActivity.this, txtheaderfilter, 0);
        Constant.setFont(FilterActivity.this, txtheaderapply, 0);
        Constant.setFont(FilterActivity.this, filterbydatetxt, 0);
        Constant.setFont(FilterActivity.this, startdatetext, 0);
        Constant.setFont(FilterActivity.this, enddatetext, 0);
        Constant.setFont(FilterActivity.this, Sdatetime, 0);
        Constant.setFont(FilterActivity.this, Edatetime, 0);
        Constant.setFont(FilterActivity.this, fbtext, 0);
        Constant.setFont(FilterActivity.this, twittertext, 0);
        Constant.setFont(FilterActivity.this, youtubetxt, 0);
        Constant.setFont(FilterActivity.this, instatext, 0);
        Constant.setFont(FilterActivity.this, tumblrtxt, 0);
        Constant.setFont(FilterActivity.this, vinetxt, 0);
        Constant.setFont(FilterActivity.this, vktext, 0);
        Constant.setFont(FilterActivity.this, disquxtext, 0);
        Constant.setFont(FilterActivity.this, filterbykeywordtxt, 0);
        Constant.setFont(FilterActivity.this, datetxt, 0);
        Constant.setFont(FilterActivity.this, networktxt, 0);
        Constant.setFont(FilterActivity.this, keywordtxt, 0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.filterdatetoggle:

                break;

          /*  case R.id.fbtoggle:

                break;*/
            case R.id.twittertoggle:

                break;
            case R.id.youtubetoggle:

                break;
            case R.id.instatoggle:

                break;
            case R.id.filkertoggle:

                break;
            case R.id.tumbletoogle:

                break;
            case R.id.vinetoggle:

                break;
            case R.id.vktoggle:

                break;
            case R.id.disquxtoogle:

                break;

            case R.id.filterbykeywordtoggle:

                break;
            case R.id.txtcancel:
                finish();

                break;
            case R.id.txtapply:

                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    callupdateUserFilterWS();
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
                }


                break;

            case R.id.txtSdatetime:
                isDate = true;
                isTime = false;
                showDateTimePicker("Choose Start Date");
                break;
            case R.id.txtEdatetime:
                isDate = false;
                isTime = true;
                showDateTimePicker("Choose End Date");
                break;
            default:
                break;


        }
    }

    private void callupdateUserFilterWS() {


        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()

        {
            String _responseMain = "";
            String fb = "0";
            String tw = "";
            String yt = "";
            String insta = "";
            String meetUp = "";
            String vk = "";
            String filter_by_keyword = "";
            String filter_by_date = "";

            Uri.Builder builder;

            @Override
            protected void onPreExecute() {


                Constant.showLoader(FilterActivity.this);

               /* if (fbtgl.isChecked()) {
                    fb = "1";
                } else {
                    fb = "0";
                }*/
                if (twittertoggle.isChecked()) {
                    tw = "2";
                } else {
                    tw = "0";
                }
                if (instatgl.isChecked()) {
                    insta = "3";
                } else {
                    insta = "0";
                }
                if (youtubetoggle.isChecked()) {
                    yt = "4";
                } else {
                    yt = "0";
                }
                if (meetUpTgl.isChecked()) {
                    meetUp = "5";
                } else {
                    meetUp = "0";
                }

                if (vktgl.isChecked()) {
                    vk = "6";
                } else {
                    vk = "0";
                }

                if (filterbykeywordtgl.isChecked()) {
                    filter_by_keyword = "1";
                } else {
                    filter_by_keyword = "0";
                }

                if (filterbydatetgl.isChecked()) {
                    filter_by_date = "1";
                } else {
                    filter_by_date = "0";
                }

                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("facebook", fb)
                        .appendQueryParameter("twitter", tw)
                        .appendQueryParameter("instagram", insta)
                        .appendQueryParameter("youtube", yt)
                        .appendQueryParameter("meetup", meetUp)
                        .appendQueryParameter("vk", vk)
                        .appendQueryParameter("filter_by_keyword", filter_by_keyword)
                        .appendQueryParameter("filter_by_date", filter_by_date)
                        .appendQueryParameter("start_date", start_date)
                        .appendQueryParameter("end_date", end_date)
                ;
            }

            @Override
            protected String doInBackground(String... arg0) {

                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {

                    try {

                        HttpURLConnection urlConnection;

                        try {

                            String query = builder.build().getEncodedQuery();
                            //			String temp=URLEncoder.encode(uri, "UTF-8");
                            URL url = new URL(WebServiceConstants.getMethodUrl(
                                    WebServiceConstants.UPDATE_USER_FILTER));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setReadTimeout(5000);


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
                            System.out.println("Response of UPDATE_USER_FILTER : " + _responseMain);


                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Constant.showToast("Server Error ", FilterActivity.this);
                            }
                        });

                    }


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            Constant.showToast("Server Error ", FilterActivity.this);
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

                        JSONObject jsonObject = new JSONObject(_responseMain);
                        if (jsonObject.has("status")) {
                            int _status = jsonObject.getInt("status");
                            if (_status == 1) {

                                String message = jsonObject.getString("message");
                                Constant.showToast(message, FilterActivity.this);
                                finish();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();

                        Constant.hideLoader();
                    }
                } else {

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

    public void showDateTimePicker(String mTitle) {
// Create a SimpleDateTimePicker and Show it
        SimpleDateTimePicker simpleDateTimePicker = SimpleDateTimePicker.make(
                mTitle,
                new Date(),
                this, getSupportFragmentManager()
        );
        // Show It baby!
        simpleDateTimePicker.show();

      /*  // Or we can chain it to simplify
        SimpleDateTimePicker.make(
                "Set Date & Time Title",
                new Date(),
                this,
                getSupportFragmentManager()
        ).show();*/

    }

    @Override
    public void DateTimeSet(Date date) {

        // This is the DateTime class we created earlier to handle the conversion
        // of Date to String Format of Date String Format to Date object
        DateTime mDateTime = new DateTime(date);
        // Show in the LOGCAT the selected Date and Time
        Log.v("FILTER ACTIVITY: ", "Date and Time selected: " + mDateTime.getDateString());

        if (isDate) {


            Sdatetime.setText(mDateTime.getDateString());

            String inputPattern = "dd MMMM yyyy hh:mm a";
            String outputPattern = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date mdate = null;
            String str = null;

            try {
                mdate = inputFormat.parse(mDateTime.getDateString());
                start_date = outputFormat.format(mdate);
                System.out.println("Final DAte : in New format : " + start_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (isTime) {

            Edatetime.setText(mDateTime.getDateString());
            String inputPattern = "dd MMMM yyyy hh:mm a";
            String outputPattern = "yyyy-MM-dd hh:mm:ss";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

            Date mdate = null;
            String str = null;

            try {
                mdate = inputFormat.parse(mDateTime.getDateString());
                end_date = outputFormat.format(mdate);
                System.out.println("Final DAte : in New format : " + end_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}