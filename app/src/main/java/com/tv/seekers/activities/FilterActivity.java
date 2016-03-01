package com.tv.seekers.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.adapter.MyKeywordsAdaptor;
import com.tv.seekers.bean.MyKeywordsBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.date.DateTime;
import com.tv.seekers.date.DateTimePicker;
import com.tv.seekers.date.SimpleDateTimePicker;
import com.tv.seekers.swipemenulistview.SwipeMenu;
import com.tv.seekers.swipemenulistview.SwipeMenuCreator;
import com.tv.seekers.swipemenulistview.SwipeMenuItem;
import com.tv.seekers.swipemenulistview.SwipeMenuListView;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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

    @Bind(R.id.clear_filter)
    FloatingActionButton clear_filter;

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

    @Bind(R.id.flikertoggle)
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

    @Bind(R.id.add_keyword)
    ImageView add_keyword;

    /*@Bind(R.id.clr_filter)
    TextView clr_filter;*/

    @Bind(R.id.keywords_listview)
    SwipeMenuListView keywords_listview;
    private ArrayList<MyKeywordsBean> rowItem;
    MyKeywordsAdaptor custombaseadapter;

    private String user_id = "";
    private SharedPreferences sPref;
    private SharedPreferences.Editor editor;
    private boolean isDate = false;
    private boolean isTime = false;
    private String start_date = "";
    private String end_date = "";


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter);
        ButterKnife.bind(this);
        rowItem = new ArrayList<MyKeywordsBean>();
        sPref = getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        editor = sPref.edit();
        user_id = sPref.getString("id", "");
//        ErrorReporter.getInstance().Init(FilterActivity.this);
        setfont();
        setonclick();
        setDefaultDate();
        setDefaultFilters();

       /* if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
            callGetUserFilterWS();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
        }*/

        if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
            callGetKewordsWS();
        } else {
            Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
        }
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        FilterActivity.this);
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(255,
                        255, 255)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.mipmap.delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        keywords_listview.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

//        setListViewHeightBasedOnChildren(keywords_listview);
//        setListViewHeightBasedOnChildrenNew(keywords_listview);
//        setListViewHeightBasedOnChildrenNew1(keywords_listview);
        setListViewHeightBasedOnItems(keywords_listview);

        // set creator

        keywords_listview.setMenuCreator(creator);

        // step 2. listener item click event
        keywords_listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        FilterActivity.this);

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MyKeywordsBean bean = rowItem.get(position);
                                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                                    delete_keyword(bean.get_tglID(), position, false);
                                } else {
                                    Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
                                }

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
                return false;
            }
        });


    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    private boolean isDateFilter = false;
    private boolean isMeetUpFilter = false;
    private boolean isTwitterFilter = false;
    private boolean isYoutubeFilter = false;
    private boolean isInstaFilter = false;
    private boolean isFlikerFilter = false;
    private boolean isVKFilter = false;


    private void setDefaultFilters() {
        isDateFilter = sPref.getBoolean("DATE", false);
        if (isDateFilter) {
            long STARTDATE = sPref.getLong("STARTDATE", 0);
            Sdatetime.setText(getDate(STARTDATE, DateTime.DATE_FORMAT));
            long ENDDATE = sPref.getLong("ENDDATE", 0);
            Edatetime.setText(getDate(ENDDATE, DateTime.DATE_FORMAT));
        }
        isMeetUpFilter = sPref.getBoolean("MEETUP", false);
        isTwitterFilter = sPref.getBoolean("TWITTER", false);
        isYoutubeFilter = sPref.getBoolean("YOUTUBE", false);
        isInstaFilter = sPref.getBoolean("INSTA", false);
        isFlikerFilter = sPref.getBoolean("FLICKER", false);
        isVKFilter = sPref.getBoolean("VK", false);

        meetUpTgl.setChecked(isMeetUpFilter);
        twittertoggle.setChecked(isTwitterFilter);
        youtubetoggle.setChecked(isYoutubeFilter);
        instatgl.setChecked(isInstaFilter);
        flickertgl.setChecked(isFlikerFilter);
        vktgl.setChecked(isVKFilter);
        filterbydatetgl.setChecked(isDateFilter);

    }

    private void setDefaultDate() {

        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat(DateTime.DATE_FORMAT);
        String mCurrentDate = df.format(c.getTime());
        System.out.println("Current Date  => " + mCurrentDate);
        Edatetime.setText(mCurrentDate);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date oneMonthAgoDate = cal.getTime();

        DateTime mDateTime = new DateTime(oneMonthAgoDate);
        System.out.println("1 month ago Date  => " + mDateTime.getDateString());
        Sdatetime.setText(mDateTime.getDateString());
    }

    private void callGetKewordsWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {
                Constant.showLoader(FilterActivity.this);

            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_KEYWORDS));
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of GET_USER_SAVED_KEYWORDS : " + _responseMain);

                            } else {
                                Log.v("Filter : GET_USER_SAVED_KEYWORDS : ", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            FilterActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Constant.showToast("Server Error ", FilterActivity.this);
                                }
                            });
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        FilterActivity.this.runOnUiThread(new Runnable() {
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
                        JSONObject jo = new JSONObject(_responseMain);
                        int status = jo.getInt("status");


                        if (status == 1) {

                            JSONArray mJsonArray = jo.getJSONArray("data");
                            if (mJsonArray.length() > 0) {
                                if (rowItem.size() > 0) {
                                    rowItem.clear();
                                }
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject kw = mJsonArray.getJSONObject(i);
                                    String keyW = kw.getString("keyword");
                                    long createdOn = kw.getLong("createdOn");
                                    boolean isActive = kw.getBoolean("isActive");
                                   /* String isActive = kw.getString("is_active");
                                    boolean _isA = (!isActive.equalsIgnoreCase("0"));*/
                                    String _ID = kw.getString("id");
                                    MyKeywordsBean rowItemC = new MyKeywordsBean(keyW, isActive, _ID);
                                    rowItemC.setCreatedOn(createdOn);
                                    rowItem.add(rowItemC);
                                }
                                if (custombaseadapter == null) {
                                    custombaseadapter = new MyKeywordsAdaptor(FilterActivity.this, rowItem);
                                    keywords_listview.setAdapter(custombaseadapter);
                                } else {
                                    custombaseadapter.notifyDataSetChanged();
                                }
                            }


                        } else if (status == 0) {
//                            Constant.showToast("Server Error", FilterActivity.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(FilterActivity.this);
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

    //    **** Method for Setting the Height of the ListView dynamically.
//    **** Hack to fix the issue of not showing all the items of the ListView
//    **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * Sets ListView height dynamically based on the height of the items.
     *
     * @param listView to be resized
     * @return true if the listView is successfully resized, false otherwise
     */
    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

    public void setListViewHeightBasedOnChildrenNew1(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if (listItem != null) {
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setListViewHeightBasedOnChildrenNew(SwipeMenuListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void delete_keyword(final String kid, final int posi, final boolean allClear) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";


            @Override
            protected void onPreExecute() {
                Constant.showLoader(FilterActivity.this);

            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    try {

                        URL url;
                        HttpURLConnection urlConnection = null;


                        try {
                            System.out.println("Request of Delete Keyword : " + WebServiceConstants.getMethodUrl(WebServiceConstants.DELETE_SAVE_KEYWORD + "?id=" + kid));
                            url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.DELETE_SAVE_KEYWORD + "?id=" + kid));
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            int responseCode = urlConnection.getResponseCode();

                            if (responseCode == 200) {
                                _responseMain = readStream(urlConnection.getInputStream());
                                System.out.println("Response of DELETE_SAVE_KEYWORD : " + _responseMain);

                            } else {
                                Log.v("My area", "Response code:" + responseCode);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null)
                                urlConnection.disconnect();
                        }


                        //						makeRequest(WebServiceConstants.getMethodUrl(WebServiceConstants.METHOD_UPDATEVENDER), jsonObj.toString());
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();

                        FilterActivity.this.runOnUiThread(new Runnable() {
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
                        JSONObject jo = new JSONObject(_responseMain);
                        int status = jo.getInt("status");
                        if (status == 1) {


                            if (allClear) {
                                rowItem.clear();
                                if (custombaseadapter != null) {
                                    custombaseadapter.notifyDataSetChanged();
                                }
                            } else {
                                rowItem.remove(posi);
                                custombaseadapter.notifyDataSetChanged();
                            }

                             /*Key words*/

                            ArrayList<String> tempListOfActiveKeywords = new ArrayList<String>();

                            if (rowItem.size() > 0) {
                                for (int i = 0; i < rowItem.size(); i++) {
                                    MyKeywordsBean bean = rowItem.get(i);
                                    if (bean.get_tglState()) {
                                        tempListOfActiveKeywords.add(bean.get_title());
                                    }


                                }
                            }

                            if (tempListOfActiveKeywords.size() > 0) {
                                editor.putInt("KEYWORDSIZE", tempListOfActiveKeywords.size());
                                for (int j = 0; j < tempListOfActiveKeywords.size(); j++) {
                                    editor.putString("KEYWORD_" + j, tempListOfActiveKeywords.get(j));
                                }
                            } else {
                                editor.putInt("KEYWORDSIZE", 0);
                            }

                            editor.commit();

                        } else if (status == 0) {
                            Constant.showToast("Server Error", FilterActivity.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(FilterActivity.this);
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

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
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
                                    String flickr = jsonObject1.getString("flickr");
                                    start_date = jsonObject1.getString("start_date");
                                    end_date = jsonObject1.getString("end_date");
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

                                    if (!flickr.equalsIgnoreCase("0") &&
                                            !flickr.equalsIgnoreCase("")) {
                                        flickertgl.setChecked(true);
                                    } else {
                                        flickertgl.setChecked(false);
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
                        Constant.showToast("Server Error ", FilterActivity.this);
                    }
                } else {

                    Constant.hideLoader();
                    Constant.showToast("Server Error ", FilterActivity.this);
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
        clear_filter.setOnClickListener(this);
        add_keyword.setOnClickListener(this);
        /*add_keywords_btn.setOnClickListener(this);
        clr_filter.setOnClickListener(this);*/
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
        Constant.setFont(FilterActivity.this, flickertxt, 0);
        Constant.setFont(FilterActivity.this, keywordtxt, 0);
//        Constant.setFont(FilterActivity.this, clr_filter, 0);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.filterdatetoggle:

                break;
            case R.id.clear_filter:

                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            FilterActivity.this);

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Are you sure want to clear filters?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // TODO: 12/1/16 Turn Off all the toggles
                                    clearFilter();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.setCancelable(true);
                    alertDialog.show();

                } else {
                    Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
                }


                break;
         /*   case R.id.clr_filter:

                break;*/

          /*  case R.id.fbtoggle:

                break;*/
            case R.id.twittertoggle:

                break;
            case R.id.youtubetoggle:

                break;
            case R.id.instatoggle:

                break;
            case R.id.flikertoggle:

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

                if (twittertoggle.isChecked()) {
                    editor.putBoolean("TWITTER", true);
                } else {
                    editor.putBoolean("TWITTER", false);
                }
                if (instatgl.isChecked()) {
                    editor.putBoolean("INSTA", true);
                } else {
                    editor.putBoolean("INSTA", false);
                }
                if (youtubetoggle.isChecked()) {
                    editor.putBoolean("YOUTUBE", true);
                } else {
                    editor.putBoolean("YOUTUBE", false);
                }
                if (meetUpTgl.isChecked()) {
                    editor.putBoolean("MEETUP", true);
                } else {
                    editor.putBoolean("MEETUP", false);
                }
                if (vktgl.isChecked()) {
                    editor.putBoolean("VK", true);
                } else {
                    editor.putBoolean("VK", false);
                }

                if (flickertgl.isChecked()) {
                    editor.putBoolean("FLICKER", true);
                } else {
                    editor.putBoolean("FLICKER", false);
                }


                if (filterbydatetgl.isChecked()) {
                    editor.putBoolean("DATE", true);
                    start_date = Sdatetime.getText().toString().trim();
                    SimpleDateFormat sdf = new SimpleDateFormat(DateTime.DATE_FORMAT);
                    long startDateMillisec = 0;
                    try {
                        Date mDate = sdf.parse(start_date);
                        startDateMillisec = mDate.getTime();
                        System.out.println("startDateMillisec :: " + startDateMillisec);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    editor.putLong("STARTDATE", startDateMillisec);


                    end_date = Edatetime.getText().toString().trim();

                    SimpleDateFormat sdfEndDate = new SimpleDateFormat(DateTime.DATE_FORMAT);
                    long endDateMillisec = 0;
                    try {
                        Date mDate = sdfEndDate.parse(end_date);
                        endDateMillisec = mDate.getTime();
                        System.out.println("startDateMillisec :: " + endDateMillisec);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    editor.putLong("ENDDATE", endDateMillisec);
                } else {
                    editor.putBoolean("DATE", false);

                }



                /*Key words*/

                ArrayList<String> tempListOfActiveKeywords = new ArrayList<String>();

                if (rowItem.size() > 0) {
                    for (int i = 0; i < rowItem.size(); i++) {
                        MyKeywordsBean bean = rowItem.get(i);
                        if (bean.get_tglState()) {
                            tempListOfActiveKeywords.add(bean.get_title());
                        }


                    }
                }

                if (tempListOfActiveKeywords.size() > 0) {
                    editor.putInt("KEYWORDSIZE", tempListOfActiveKeywords.size());
                    for (int j = 0; j < tempListOfActiveKeywords.size(); j++) {
                        editor.putString("KEYWORD_" + j, tempListOfActiveKeywords.get(j));
                    }
                } else {
                    editor.putInt("KEYWORDSIZE", 0);
                }


                editor.commit();


                Intent returnIntent = new Intent();
                returnIntent.putExtra("applied", true);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

/*

                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    callupdateUserFilterWS();
                } else {
                    Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
                }
*/


                break;

            case R.id.txtSdatetime:

                if (filterbydatetgl.isChecked()) {
                    isDate = true;
                    isTime = false;
                    showDateTimePicker("Choose Start Date");
                }

                break;
            case R.id.txtEdatetime:

                if (filterbydatetgl.isChecked()) {
                    isDate = false;
                    isTime = true;
                    showDateTimePicker("Choose End Date");
                }

                break;
            case R.id.add_keyword:
                showAddKeywordDialog();
                break;
            default:
                break;


        }
    }

    private void clearFilter() {
        twittertoggle.setChecked(false);
        instatgl.setChecked(false);
        youtubetoggle.setChecked(false);
        meetUpTgl.setChecked(false);
        vktgl.setChecked(false);
        flickertgl.setChecked(false);
        filterbydatetgl.setChecked(false);


        editor.putBoolean("DATE", false);
        editor.putBoolean("MEETUP", false);
        editor.putBoolean("TWITTER", false);
        editor.putBoolean("YOUTUBE", false);
        editor.putBoolean("INSTA", false);
        editor.putBoolean("FLICKER", false);
        editor.putBoolean("VK", false);
        editor.putLong("STARTDATE", 0);
        editor.putLong("ENDDATE", 0);
        editor.putInt("KEYWORDSIZE", 0);

        editor.commit();

        if (rowItem.size() > 0) {

            String id = "";
            String prefix = "";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rowItem.size(); i++) {
                MyKeywordsBean bean = rowItem.get(i);
                id = bean.get_tglID();
                sb.append(prefix);
                prefix = "&id=";
                sb.append(id);

            }
            id = sb.toString();
            delete_keyword(id, 0, true);


        }


    }

    private void showAddKeywordDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(FilterActivity.this);


        LayoutInflater inflater = LayoutInflater.from(FilterActivity.this);
        View view = inflater.inflate(R.layout.dialog_add_keywords, null);

        final EditText etKeywordName = (EditText) view.findViewById(R.id.etKeywordName);

        alert.setView(view);


        alert.setTitle("Add Keywords");
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etKeywordName.getText().toString().trim().isEmpty()) {
                    Constant.showToast("Please enter keyword", FilterActivity.this);
                } else {

                    dialog.dismiss();
                    if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                        callSaveKeywordWS(etKeywordName.getText().toString().trim());
                    } else {
                        Constant.showToast(getResources().getString(R.string.internet), FilterActivity.this);
                    }



                  /*  MyKeywordsBean rowItemC = new MyKeywordsBean(etKeywordName.getText().toString().trim(), true, "1");
                    rowItem.add(rowItemC);

                    if (custombaseadapter == null) {
                        custombaseadapter = new MyKeywordsAdaptor(FilterActivity.this, rowItem);
                        keywords_listview.setAdapter(custombaseadapter);
                    } else {
                        custombaseadapter.notifyDataSetChanged();
                    }*/


                    // TODO: 11/1/16 calling keyword service here


                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void callSaveKeywordWS(final String keyword) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(FilterActivity.this);
                mJsonObject = new JSONObject();
                try {
                    mJsonObject.put("keyword", keyword);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(FilterActivity.this)) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = mJsonObject.toString();
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.USER_SAVE_KEYWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setReadTimeout(5);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setRequestProperty("Content-Type", "application/json");
                            urlConnection.setRequestProperty(Constant.Cookie, sPref.getString(Constant.Cookie, ""));
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(50000);
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
                                //System.out.println("Uploading............");
                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();

                            System.out.println("Response of Add keyword : " + _responseMain);

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
                        JSONObject jo = new JSONObject(_responseMain);
                        int status = jo.getInt("status");
                        if (status == 1) {
                            JSONObject dataJsonObject = jo.getJSONObject("data");
                            String id = dataJsonObject.getString("id");
                            MyKeywordsBean rowItemC = new MyKeywordsBean(keyword, true, id);
                            rowItem.add(rowItemC);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {


                                    if (custombaseadapter == null) {
                                        custombaseadapter = new MyKeywordsAdaptor(FilterActivity.this, rowItem);
                                        keywords_listview.setAdapter(custombaseadapter);
                                    } else {
                                        custombaseadapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        } else if (status == 0) {
                            Constant.showToast("Server Error", FilterActivity.this);
                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(FilterActivity.this);
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
            String flicker = "";
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

                if (flickertgl.isChecked()) {
                    flicker = "7";
                } else {
                    flicker = "0";
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
                        .appendQueryParameter("flickr", flicker)
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
                long milliseconds = date.getTime();
                System.out.println("Final DAte : Millisecond : " + milliseconds);
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

                long milliseconds = date.getTime();
                System.out.println("Final DAte : Millisecond : " + milliseconds);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}