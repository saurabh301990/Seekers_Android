package com.tv.seekers.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tv.seekers.R;
import com.tv.seekers.utils.FullScreenVideoController;

import java.io.IOException;

/**
 * Created by shoeb on 13/5/16.
 */
public class InstaGramVideo extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, FullScreenVideoController.MediaPlayerControl {

    SurfaceView videoSurface;
    MediaPlayer player;
    FullScreenVideoController controller;


    private String videoUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_video_insta);

        if (getIntent().hasExtra("videoUrl")) {
            videoUrl = getIntent().getStringExtra("videoUrl");
        }

        videoSurface = (SurfaceView) findViewById(R.id.videoSurface);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
        controller.setMediaPlayer(this);
        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
        player.start();
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    public void toggleFullScreen() {


        player.stop();
        player.reset();
        controller.hide();
        controller.onFinishInflate();
        // player.release();
        //   player = null;
        controller.updatePausePlay();
        finish();

        /*if(isFullScreen()){
            DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width =  metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            videoSurface.setLayoutParams(params);
        }else {
            DisplayMetrics metrics = new DisplayMetrics(); getWindowManager().getDefaultDisplay().getMetrics(metrics);
            android.widget.FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width =  1280;
            params.height = 720;
            params.leftMargin = 0;
            videoSurface.setLayoutParams(params);
        }

        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));*/
    }

    @Override
    public void done() {
        player.stop();
        player.reset();
        controller.hide();
        controller.onFinishInflate();
        // player.release();
        //   player = null;
        controller.updatePausePlay();
        finish();
    }
    // End VideoMediaController.MediaPlayerControl

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = metrics.widthPixels;
            params.height = 350;
            params.leftMargin = 0;
            videoSurface.setLayoutParams(params);
        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) videoSurface.getLayoutParams();
            params.width = metrics.widthPixels;
            params.height = metrics.heightPixels;
            params.leftMargin = 0;
            videoSurface.setLayoutParams(params);
        }

        controller.setAnchorView((FrameLayout) findViewById(R.id.videoSurfaceContainer));
    }

    @Override
    protected void onResume() {
        super.onResume();

        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);

        player = new MediaPlayer();
        controller = new FullScreenVideoController(this);

        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, Uri.parse(videoUrl));
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        player.stop();
        player.reset();
        controller.hide();
        controller.onFinishInflate();
        // player.release();
        //   player = null;
        controller.updatePausePlay();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
}