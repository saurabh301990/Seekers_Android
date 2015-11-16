package com.tv.seekers.adapter;

import android.app.Activity;
import android.content.Context;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        final MyKeywordsBean rowItems = (MyKeywordsBean) rowItem
                .get(position);
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
        final boolean isChked = rowItems.get_tglState();
        holder._tglBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(position+" <<<" +isChked);
                if(isChked){
                    tglSaveKeywordWS("0",rowItems.get_tglID());
                }else{
                    tglSaveKeywordWS("1",rowItems.get_tglID());
                }
            }
        });

        holder._title.setText(rowItems.get_title());
        holder._tglBtn.setChecked(rowItems.get_tglState());

        return convertView;
    }

    private void tglSaveKeywordWS(final String isActive, final String id) {
        AsyncTask<String, String, String> _Task = new AsyncTask<String, String, String>()
        {
            String _responseMain = "";
            Uri.Builder builder;

            @Override
            protected void onPreExecute() {
                Constant.showLoader(context);
                builder = new Uri.Builder()
                        .appendQueryParameter("keyword_id", id)
                        .appendQueryParameter("is_active", isActive);
                System.out.println("BUI: "+builder);
            }

            @Override
            protected String doInBackground(String... arg0) {
                if (NetworkAvailablity.checkNetworkStatus(context)) {
                    try {
                        HttpURLConnection urlConnection;
                        try {
                            String query = builder.build().getEncodedQuery();
                            URL url = new URL(WebServiceConstants.getMethodUrl(WebServiceConstants.TGL_SAVE_KEYWORD));
                            urlConnection = (HttpURLConnection) ((url.openConnection()));
                            urlConnection.setDoInput(true);
                            urlConnection.setDoOutput(true);
                            urlConnection.setUseCaches(false);
                            urlConnection.setChunkedStreamingMode(1024);
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
