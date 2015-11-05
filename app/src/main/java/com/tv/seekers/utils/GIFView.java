package com.tv.seekers.utils;

import android.content.Context;
import android.webkit.WebView;

/**
 * Created by admin1 on 4/11/15.
 */
public class GIFView extends WebView{
    public GIFView(Context context, String path) {
        super(context);
        loadUrl(path);
    }
}
