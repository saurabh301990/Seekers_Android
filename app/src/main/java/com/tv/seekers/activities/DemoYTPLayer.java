package com.tv.seekers.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.tv.seekers.constant.Constant;

/**
 * Created by shoeb on 4/5/16.
 */
public class DemoYTPLayer extends Activity {
    String VIDEO_ID = "NGrvt9yZny0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = YouTubeStandalonePlayer.createVideoIntent(DemoYTPLayer.this, Constant.YOUTUBE_API_KEY, VIDEO_ID);
        startActivity(intent);
    }
}
