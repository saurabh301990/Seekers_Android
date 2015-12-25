package com.tv.seekers.menu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tv.seekers.R;
import com.tv.seekers.adapter.MyKeywordsAdaptor;
import com.tv.seekers.bean.MyKeywordsBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.swipemenulistview.SwipeMenu;
import com.tv.seekers.swipemenulistview.SwipeMenuCreator;
import com.tv.seekers.swipemenulistview.SwipeMenuItem;
import com.tv.seekers.swipemenulistview.SwipeMenuListView;
import com.tv.seekers.utils.NetworkAvailablity;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeyWords extends android.support.v4.app.Fragment implements View.OnClickListener {
    SwipeMenuListView listview;
    List<MyKeywordsBean> rowItem;
    private SharedPreferences _sPrefs;
    MyKeywordsAdaptor custombaseadapter;
    String user_id;

    @Bind(R.id.et_mk_search)
    EditText _searchET;

    @Bind(R.id.addKeywords)
    ImageView _addIV;

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.my_keywords, container, false);
        ButterKnife.bind(this, v);
        Constant.setFont(getActivity(), _searchET, 0);
        _sPrefs = getActivity().getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
        user_id = _sPrefs.getString("id", "");
        rowItem = new ArrayList<MyKeywordsBean>();
        listview = (SwipeMenuListView) v.findViewById(R.id.mykeywords_list);
        custombaseadapter = new MyKeywordsAdaptor(getActivity(), rowItem);
        listview.setAdapter(custombaseadapter);
        _addIV.setOnClickListener(this);
        if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
            callGetKeywordWS();
        }

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
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
        // set creator
        listview.setMenuCreator(creator);

        // step 2. listener item click event
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getActivity());

                // set dialog message
                alertDialogBuilder
                        .setMessage("Are you sure ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MyKeywordsBean bean = rowItem.get(position);
                                delete_keyword(bean.get_tglID(), position);
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

        ImageView menu;
        menu = (ImageView) getActivity().findViewById(R.id.tgl_menu);
        menu.setVisibility(View.VISIBLE);
        MainActivity.drawerFragment.setDrawerState(true);
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addKeywords:
                Constant.hideKeyBoard(getActivity());
                if (!(_searchET.getText().toString().equalsIgnoreCase(""))) {

                    if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                        callSaveKeywordWS(_searchET.getText().toString());
                    } else {
                        Constant.showToast(getActivity().getResources().getString(R.string.internet), getActivity());
                    }

                    // _searchET.setText("");
                } else {
                    Constant.showToast("Insert text", getActivity());
                }
                break;
            default:
                break;
        }
    }

    private void callGetKeywordWS() {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(getActivity());
                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = builder.build().getEncodedQuery();
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.GET_USER_SAVED_KEYWORDS));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(5000);
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
                            Constant.showToast("Server Error ", getActivity());
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
                        String msg = jo.getString("message");
                        if (msg.equalsIgnoreCase("success")) {
                            JSONArray keyWordArray = jo.getJSONArray("user_keywords");
                            for (int i = 0; i < keyWordArray.length(); i++) {
                                JSONObject kw = keyWordArray.getJSONObject(i);
                                String keyW = kw.getString("keyword");
                                String isActive = kw.getString("is_active");
                                boolean _isA = (!isActive.equalsIgnoreCase("0"));
                                String _ID = kw.getString("id");
                                MyKeywordsBean rowItemC = new MyKeywordsBean(keyW, _isA, _ID);
                                rowItem.add(rowItemC);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    custombaseadapter.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
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

    private void callSaveKeywordWS(final String keyword) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(getActivity());
                builder = new Uri.Builder()
                        .appendQueryParameter("user_id", user_id)
                        .appendQueryParameter("keyword", keyword);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = builder.build().getEncodedQuery();
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.USER_SAVE_KEYWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setReadTimeout(5);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(5000);
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
                            Constant.showToast("Server Error ", getActivity());
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
                        String id = jo.getString("insert_id");
                        MyKeywordsBean rowItemC = new MyKeywordsBean(keyword, false, id);
                        rowItem.add(rowItemC);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                custombaseadapter.notifyDataSetChanged();
                                _searchET.setText("");
                            }
                        });
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

    private void delete_keyword(final String kid, final int posi) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(getActivity());
                builder = new Uri.Builder()
                        .appendQueryParameter("keyword_id", kid);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(getActivity())) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = builder.build().getEncodedQuery();
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.DELETE_SAVE_KEYWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setReadTimeout(5000);
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
                            Constant.showToast("Server Error ", getActivity());
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
                        rowItem.remove(posi);
                        custombaseadapter.notifyDataSetChanged();
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
}
