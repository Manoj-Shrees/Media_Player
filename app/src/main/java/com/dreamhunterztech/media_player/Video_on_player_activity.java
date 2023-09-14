package com.dreamhunterztech.media_player;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Dreamer on 20-01-2018.
 */

public class Video_on_player_activity extends AppCompatActivity implements  SimpleExoPlayer.VideoListener, SurfaceHolder.Callback ,VideoControllerView.MediaPlayerControl{

ImageButton playbtn,pausebtn;

    private VideoView mSurfaceView;
    private SimpleExoPlayer mPlayer;
    private AspectRatioFrameLayout mAspectRatioLayout;
    String videoUrl="https://cgcstudentportal.000webhostapp.com/test.mp4";
    Handler handler;;
    View decorview;
    int uioptions;
    VideoControllerView controller;
    private  Runnable updatemediadata;

    {
        updatemediadata = new Runnable() {
            @Override
            public void run() {
                switch (mPlayer.getPlaybackState())
                {
                    case ExoPlayer.STATE_BUFFERING :
                     break;

                    case ExoPlayer.STATE_IDLE :
                        break;

                    case ExoPlayer.STATE_READY :
                        break;

                    case ExoPlayer.STATE_ENDED:
                        break;

                        default:
                        break;
                }

               String totdur = String.format("%02d.%02d.%02d", TimeUnit.MILLISECONDS.toHours(mPlayer.getDuration())
                     ,TimeUnit.MILLISECONDS.toMinutes(mPlayer.getDuration())-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mPlayer.getDuration()))
                     ,TimeUnit.MILLISECONDS.toSeconds(mPlayer.getDuration())-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer.getDuration())));

                String curdur = String.format("%02d.%02d.%02d",TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition())
                        ,TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition())-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition()))
                , TimeUnit.MILLISECONDS.toSeconds(mPlayer.getCurrentPosition())-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition())));

            handler.postDelayed(updatemediadata,200);
            }
        };
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        uioptions=  (
                 View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(uioptions);
        setContentView(R.layout.activity_videoplayeractivity);
        controller = new VideoControllerView(this);

        mSurfaceView = findViewById(R.id.Video_view);
        SurfaceHolder videoHolder = mSurfaceView.getHolder();
        videoHolder.addCallback(this);
        mAspectRatioLayout = findViewById(R.id.aspectedratio);
        mAspectRatioLayout.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        mSurfaceView.getHolder().addCallback(this);



        // initialize player
        handler = new Handler();
        ExtractorsFactory extractor = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        mPlayer = ExoPlayerFactory.newSimpleInstance(
                this,
                trackSelector,
                new DefaultLoadControl()
        );

        videoUrl = getIntent().getStringExtra("video");

        // initialize source

        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(videoUrl),
                dataSourceFactory,
                extractor,
                null,
                null
        );
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);
        mPlayer.setVideoListener(this);
        controller.setMediaPlayer(mSurfaceView,"");
        controller.setAnchorView((ViewGroup) findViewById(R.id.aspectedratio));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        controller.show();
        return false;
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mPlayer != null) {
            mPlayer.setVideoSurfaceHolder(surfaceHolder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mPlayer != null) {
            mPlayer.setVideoSurfaceHolder(null);
        }

    }


    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame() {

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onStop(){
        super.onStop();

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }



    @Override
    public void start() {
      mPlayer.setPlayWhenReady(true);
    }

    @Override
    public void pause() {
        mPlayer.setPlayWhenReady(false);
    }

    @Override
    public int getDuration() {
        return (int) mPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return (int) mPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {

        mPlayer.seekTo(pos);

    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isLoading();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {

    }
}
