package com.tv.seekers.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tv.seekers.R;
import com.tv.seekers.bean.MyKeywordsBean;
import com.tv.seekers.constant.Constant;
import com.tv.seekers.constant.WebServiceConstants;
import com.tv.seekers.utils.NetworkAvailablity;

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

/**
 * Created by admin1 on 3/11/15.
 */
public class MyKeywordsAdaptor extends BaseAdapter {
    Activity context;
    private ArrayList<MyKeywordsBean> rowItem;
    private SharedPreferences sPref;

    public MyKeywordsAdaptor(Activity context, ArrayList<MyKeywordsBean> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
        sPref = context.getSharedPreferences("LOGINPREF", Context.MODE_PRIVATE);
    }

    public class ViewHolder {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final MyKeywordsBean rowItems = rowItem.get(position);

//        System.out.println("GetView Keywords with Pos : " + position+"ID "+ rowItems.get_tglID() );
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mykeywords_row, null);
            holder = new ViewHolder();
            holder._title = (TextView) convertView.findViewById(R.id.mykeywords_title);
            Constant.setFont(context, holder._title, 0);
            holder._tglBtn = (ToggleButton) convertView.findViewById(R.id.mykeywords_tgl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final boolean isChked = rowItems.get_tglState();
        holder._tglBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position + " <<<" + isChked);
                if (isChked) {
                    tglSaveKeywordWS(false, rowItems.get_tglID(), rowItems.getCreatedOn(), rowItems.get_title());
                } else {
                    tglSaveKeywordWS(true, rowItems.get_tglID(), rowItems.getCreatedOn(), rowItems.get_title());
                }
            }
        });

        holder._title.setText(rowItems.get_title());
        holder._tglBtn.setChecked(rowItems.get_tglState());

        return convertView;
    }

    private void tglSaveKeywordWS(final boolean isActive, final String id, final long mCreatedOn, final String title) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>() {
            String _responseMain = "";
            JSONObject mJsonObject;


            @Override
            protected void onPreExecute() {
                Constant.showLoader(context);
                mJsonObject = new JSONObject();
                try {
                    mJsonObject.put("id", id);
                    mJsonObject.put("createdOn", mCreatedOn);
                    mJsonObject.put("keyword", title);
                    mJsonObject.put("isActive", isActive);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(context)) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = mJsonObject.toString();
                            System.out.println("Request of Update Keyword : " + query);
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.TGL_SAVE_KEYWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
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
                                //System.out.println("Uploading............");
                                sb.append(line);
                            }

                            bufferedReader.close();
                            _responseMain = sb.toString();
                            System.out.println("Response of toggle KeyWord : " + _responseMain);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                        Constant.showToast("Server Error ", context);
                    }
                } else {
                    Constant.showToast("Server Error ", context);
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

                        } else if (status == -1) {
                            //Redirect to Login
                            Constant.alertForLogin(context);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
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
