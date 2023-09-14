package com.dreamhunterztech.media_player;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRouter;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.mediarouter.media.MediaControlIntent;
import androidx.mediarouter.media.MediaRouteSelector;
import androidx.mediarouter.media.RemotePlaybackClient;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dreamer on 28-01-2018.
 */

public class Video_off_player_activity  extends AppCompatActivity {

    private AspectRatioFrameLayout mAspectRatioLayout;
    View decorview;
    int uioptions;
    private SimpleExoPlayer mPlayer;
     private VideoView player;
    private Uri videourl;
    private TextView mEndTime, mCurrentTime;
    private SeekBar mProgress;
    private Handler mainHandler;
    public ControlsMode controlsMode;

    public enum ControlsMode {LOCK, FULLCONTORLS}

    private ControlsMode controlsState;
    Window window;
    private int sWidth, sHeight;
    private double seekSpeed;
    private String seekDur;
    AudioManager audioManager;
    public float baseX, baseY;
    public long diffX, diffY;
    public int calculatedTime;
    public Boolean tested_ok = false, seek_ok = false;
    public Boolean screen_swipe_move = false;
    public boolean immersiveMode, intLeft, intRight, intTop, intBottom, finLeft, finRight, finTop, finBottom;
    public int brightness, mediavolume;
    public float device_height, device_width;
    public static final int MIN_DISTANCE = 150;
    Runnable hideControls, updatePlayer;
    public ContentResolver cResolver;
    private TextView swipemediatime, swipemediatimeadd;
    private ImageButton play_pausebtn, lockbtn, unlockbtn, castbtn, rotationbtn,nightbtn;
    private ImageView volimage, brightnessimage;
    public ProgressBar volpbar, brightnessbar;
    public LinearLayout vollayout, brightnesslayout, swipemediatimelayout, unlocklayout, controllerlayout, playerheadlayout , modelayout;
    public RelativeLayout rootlayout, centerlayout,nightmodelayout;
    public boolean checklandscape = false, rotationbtnmode = false;
    private boolean startchk = false;


    //Implementing Chromecast
    public MediaRouteButton mMediaRouteButton;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private PlaybackState mPlaybackState;
    private SessionManager mSessionManager;
    private MediaItem mSelectedMedia;



    private final SessionManagerListener<CastSession> mSessionManagerListener = new SessionManagerListenerImpl();
    private class SessionManagerListenerImpl implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionStarting(CastSession session) {

        }

        @Override
        public void onSessionStarted(CastSession session, String sessionId) {
            onApplicationConnected(session);
        }

        @Override
        public void onSessionStartFailed(CastSession session, int i) {

        }

        @Override
        public void onSessionEnding(CastSession session) {

        }

        @Override
        public void onSessionResumed(CastSession session, boolean wasSuspended) {
            onApplicationConnected(session);
        }

        @Override
        public void onSessionResumeFailed(CastSession session, int i) {

        }

        @Override
        public void onSessionSuspended(CastSession session, int i) {

        }

        @Override
        public void onSessionEnded(CastSession session, int error) {
            finish();
        }

        @Override
        public void onSessionResuming(CastSession session, String s) {

        }
    }
    private void onApplicationConnected(CastSession castSession) {
        mCastSession = castSession;
        loadRemoteMedia(0,true);
    }
    private MediaInfo buildMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);


        mSelectedMedia = new MediaItem();
        /*mSelectedMedia.setUrl(video_url[currentTrackIndex]);
        mSelectedMedia.setContentType(video_type[currentTrackIndex]);*/
        mSelectedMedia.setTitle("video player .....");

        movieMetadata.putString(MediaMetadata.KEY_TITLE, mSelectedMedia.getTitle());

        return new MediaInfo.Builder(mSelectedMedia.getUrl())
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("hls")
                .setMetadata(movieMetadata)
                .setStreamDuration(mSelectedMedia.getDuration() * 1000)
                .build();
    }
    private void loadRemoteMedia(int position, boolean autoPlay) {
        if (mCastSession == null) {
            return;
        }
        final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
        if (remoteMediaClient == null) {
            return;
        }
        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
                Intent intent = new Intent(Video_off_player_activity.this, ExpandedControlsActivity.class);
                startActivityForResult(intent,200);
                remoteMediaClient.removeListener(this);
                if(player.isPlaying()){
                    player.pause();
                }
            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
        remoteMediaClient.load(buildMediaInfo(), autoPlay, position);
    }


    {
        updatePlayer = new Runnable() {
            @Override
            public void run() {
            /*switch (player.getPlaybackState()) {
                case player.STATE_BUFFERING:
                    loadingPanel.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_ENDED:
                    finish();
                    break;
                case ExoPlayer.STATE_IDLE:
                    loadingPanel.setVisibility(View.GONE);
                    break;
                case ExoPlayer.STATE_PREPARING:
                    loadingPanel.setVisibility(View.VISIBLE);
                    break;
                case ExoPlayer.STATE_READY:
                    loadingPanel.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }*/

                String totDur = String.format("%02d.%02d.%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getDuration()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getDuration()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getDuration())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getDuration())));
                String curDur = String.format("%02d.%02d.%02d",
                        TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition())));
                mCurrentTime.setText(curDur);
                mEndTime.setText(totDur);
                mProgress.setMax((int) player.getDuration());
                mProgress.setProgress((int) player.getCurrentPosition());
                mainHandler.postDelayed(updatePlayer, 100);
            }
        };
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        uioptions = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.LAYER_TYPE_HARDWARE
        );


        hideControls = new Runnable() {
            @Override
            public void run() {
                hideAllControls();
            }
        };


        decorview = getWindow().getDecorView();
        decorview.setSystemUiVisibility(uioptions);
        setContentView(R.layout.activity_videoplayeractivity);
        mEndTime = (TextView) findViewById(R.id.totaltimer);
        mCurrentTime = (TextView) findViewById(R.id.currenttimer);
        mProgress = (SeekBar) findViewById(R.id.mediaprogress);
        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress());
            }
        });

        swipemediatime = findViewById(R.id.swipeforwardtimetext);
        swipemediatimeadd = findViewById(R.id.swipeforwardaddtimetext);
        brightnessimage = findViewById(R.id.brightnessimageview);
        volimage = findViewById(R.id.volimageview);
        rotationbtn = findViewById(R.id.rotation_btn);

        volpbar = findViewById(R.id.volume_slider);
        brightnessbar = findViewById(R.id.brightness_slider);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);


        player = findViewById(R.id.Video_view);
        mAspectRatioLayout = findViewById(R.id.aspectedratio);

        play_pausebtn = findViewById(R.id.play_pause_btn);
        lockbtn = findViewById(R.id.lock_btn);
        unlockbtn = findViewById(R.id.unlock_btn);
        nightbtn = findViewById(R.id.nightmodebtn);
        mainHandler = new Handler();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        sWidth = size.x;
        sHeight = size.y;

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        device_height = displaymetrics.heightPixels;
        device_width = displaymetrics.widthPixels;


        cResolver = getContentResolver();
        window = getWindow();

        String video_data = getIntent().getStringExtra("video");
        videourl = Uri.parse(video_data.toString().split(",>")[1]);

        TextView setmedianame = findViewById(R.id.medianame);
        setmedianame.setText(new File(String.valueOf(videourl)).getName());


        vollayout = findViewById(R.id.vollayout);
        swipemediatimelayout = findViewById(R.id.swipetimelayout);
        brightnesslayout = findViewById(R.id.brightnesslayout);
        unlocklayout = findViewById(R.id.unlocklayout);
        rootlayout = findViewById(R.id.playerroot);
        centerlayout = findViewById(R.id.centerlayout);
        controllerlayout = findViewById(R.id.playercontrols);
        playerheadlayout = findViewById(R.id.playerhead);
        modelayout = findViewById(R.id.modelayout);
        nightmodelayout = findViewById(R.id.nightmodevisionlayout);


        player.setVideoURI(videourl);
        player.setTag(videourl);
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });


        LinearLayout cast_container = (LinearLayout) findViewById(R.id.cast_container);
        mMediaRouteButton = new MediaRouteButton(this);
        cast_container.addView(mMediaRouteButton);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);
        mCastContext = CastContext.getSharedInstance(this);
        mCastContext.getSessionManager().addSessionManagerListener(
                mSessionManagerListener, CastSession.class);

        rotationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotationbtnmode = true;
                if (checklandscape == false) {
                    landscapemode();
                    checklandscape = true;
                } else {
                    potraitmode();
                    checklandscape = true;
                }
            }
        });

        lockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlsState = ControlsMode.LOCK;
                rootlayout.setVisibility(View.GONE);
                unlocklayout.setVisibility(View.VISIBLE);
            }
        });

        unlockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlsState = ControlsMode.FULLCONTORLS;
                rootlayout.setVisibility(View.VISIBLE);
                unlocklayout.setVisibility(View.GONE);
            }
        });

        nightbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nightmodelayout.getVisibility()==View.GONE)
                {
                    nightmodelayout.setVisibility(View.VISIBLE);
                    nightbtn.setImageResource(R.drawable.ic_day);
                }

                else
                {
                    nightmodelayout.setVisibility(View.GONE);
                    nightbtn.setImageResource(R.drawable.ic_night);
                }
            }
        });

        play_pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                    play_pausebtn.setImageResource(R.drawable.ic_media_play);
                } else {
                    player.start();
                    play_pausebtn.setImageResource(R.drawable.ic_media_pause);
                }
            }
        });


    }


    private void hideAllControls() {
        if (controlsState == ControlsMode.FULLCONTORLS) {
            if (rootlayout.getVisibility() == View.VISIBLE) {
                rootlayout.setVisibility(View.GONE);
            }
        } else if (controlsState == ControlsMode.LOCK) {
            if (unlocklayout.getVisibility() == View.VISIBLE) {
                unlocklayout.setVisibility(View.GONE);
            }
        }
        decorview.setSystemUiVisibility(uioptions);
    }

    private void showControls() {
        if (controlsState == ControlsMode.FULLCONTORLS) {
            if (rootlayout.getVisibility() == View.GONE) {
                rootlayout.setVisibility(View.VISIBLE);
            }
        } else if (controlsState == ControlsMode.LOCK) {
            if (unlocklayout.getVisibility() == View.GONE) {
                unlocklayout.setVisibility(View.VISIBLE);
            }
        }
        mainHandler.removeCallbacks(hideControls);
        mainHandler.postDelayed(hideControls, 3000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                tested_ok = false;
                if (event.getX() < (sWidth / 2)) {
                    intLeft = true;
                    intRight = false;
                } else if (event.getX() > (sWidth / 2)) {
                    intLeft = false;
                    intRight = true;
                }
                int upperLimit = (sHeight / 4) + 100;
                int lowerLimit = ((sHeight / 4) * 3) - 150;
                if (event.getY() < upperLimit) {
                    intBottom = false;
                    intTop = true;
                } else if (event.getY() > lowerLimit) {
                    intBottom = true;
                    intTop = false;
                } else {
                    intBottom = false;
                    intTop = false;
                }
                seekSpeed = (TimeUnit.MILLISECONDS.toSeconds(player.getDuration()) * 0.1);
                diffX = 0;
                calculatedTime = 0;
                seekDur = String.format("%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(diffX) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffX)),
                        TimeUnit.MILLISECONDS.toSeconds(diffX) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffX)));

                //TOUCH STARTED
                baseX = event.getX();
                baseY = event.getY();
                break;


            case MotionEvent.ACTION_MOVE:
                screen_swipe_move = true;
                if (controlsState == ControlsMode.FULLCONTORLS) {
                    centerlayout.setVisibility(View.GONE);
                    diffX = (long) (Math.ceil(event.getX() - baseX));
                    diffY = (long) Math.ceil(event.getY() - baseY);
                    double brightnessSpeed = 0.05;
                    if (Math.abs(diffY) > MIN_DISTANCE) {
                        tested_ok = true;
                    }
                    if (Math.abs(diffY) > Math.abs(diffX)) {
                        if (intLeft) {
                            cResolver = getContentResolver();
                            window = getWindow();
                            try {
                                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                                brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
                            } catch (Settings.SettingNotFoundException e) {
                                e.printStackTrace();
                            }
                            int new_brightness = (int) (brightness - (diffY * brightnessSpeed));
                            if (new_brightness > 250) {
                                new_brightness = 250;
                            } else if (new_brightness < 1) {
                                new_brightness = 1;
                            }
                            double brightPerc = Math.ceil((((double) new_brightness / (double) 250) * (double) 100));
                            centerlayout.setVisibility(View.VISIBLE);
                            brightnesslayout.setVisibility(View.VISIBLE);
                            brightnessbar.setProgress((int) brightPerc);
                            if (brightPerc < 30) {
                                brightnessimage.setImageResource(R.drawable.hplib_brightness_minimum);
                            } else if (brightPerc > 30 && brightPerc < 80) {
                                brightnessimage.setImageResource(R.drawable.hplib_brightness_medium);
                            } else if (brightPerc > 80) {
                                brightnessimage.setImageResource(R.drawable.hplib_brightness_maximum);

                            }
                            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, (new_brightness));
                            WindowManager.LayoutParams layoutpars = window.getAttributes();
                            layoutpars.screenBrightness = brightness / (float) 255;
                            window.setAttributes(layoutpars);
                        } else if (intRight) {
                            centerlayout.setVisibility(View.VISIBLE);
                            mediavolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                            int maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                            double cal = (double) diffY * ((double) maxVol / (double) (device_height * 4));
                            int newMediaVolume = mediavolume - (int) cal;
                            if (newMediaVolume > maxVol) {
                                newMediaVolume = maxVol;
                            } else if (newMediaVolume < 1) {
                                newMediaVolume = 0;
                            }
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newMediaVolume, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
                            double volPerc = Math.ceil((((double) newMediaVolume / (double) maxVol) * (double) 100));
                            if (volPerc < 1) {
                                volimage.setImageResource(R.drawable.hplib_volume_mute);
                            } else if (volPerc >= 1) {
                                volimage.setImageResource(R.drawable.hplib_volume);
                            }

                            vollayout.setVisibility(View.VISIBLE);
                            volpbar.setProgress((int) volPerc);
                        }
                    } else if (Math.abs(diffX) > Math.abs(diffY)) {

                        if (Math.abs(diffX) > (MIN_DISTANCE + 100)) {
                            tested_ok = true;
                            centerlayout.setVisibility(View.VISIBLE);
                            swipemediatimelayout.setVisibility(View.VISIBLE);
                            mProgress.setVisibility(View.VISIBLE);
                            playerheadlayout.setVisibility(View.GONE);
                            controllerlayout.setVisibility(View.GONE);
                            modelayout.setVisibility(View.GONE);
                            String totime = "";
                            seek_ok = true;
                            calculatedTime = (int) ((diffX) * seekSpeed);
                            if (calculatedTime > 0) {
                                seekDur = String.format("[ + %02d:%02d ]",
                                        TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime)),
                                        TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime)));
                            } else if (calculatedTime < 0) {
                                seekDur = String.format("[ - %02d:%02d ]",
                                        Math.abs(TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime))),
                                        Math.abs(TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime))));
                            }
                            totime = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition() + (calculatedTime)),
                                    TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition() + (calculatedTime)) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(player.getCurrentPosition() + (calculatedTime))), // The change is in this line
                                    TimeUnit.MILLISECONDS.toSeconds(player.getCurrentPosition() + (calculatedTime)) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(player.getCurrentPosition() + (calculatedTime))));
                            swipemediatimeadd.setText(seekDur);
                            swipemediatime.setText(totime);
                            mProgress.setProgress((int) (player.getCurrentPosition() + (calculatedTime)));
                        }

                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                screen_swipe_move = false;
                tested_ok = false;

                swipemediatimelayout.setVisibility(View.GONE);
                brightnesslayout.setVisibility(View.GONE);
                vollayout.setVisibility(View.GONE);
             /*   brightnessBarContainer.setVisibility(View.GONE);
                volumeBarContainer.setVisibility(View.GONE);
                onlySeekbar.setVisibility(View.VISIBLE);*/
                playerheadlayout.setVisibility(View.VISIBLE);
                controllerlayout.setVisibility(View.VISIBLE);
                modelayout.setVisibility(View.VISIBLE);
                centerlayout.setVisibility(View.VISIBLE);
                if (seek_ok == true) {
                    calculatedTime = (int) (player.getCurrentPosition() + (calculatedTime));
                    player.seekTo(calculatedTime);
                    seek_ok = false;
                }
                showControls();
                break;

        }
        return super.onTouchEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onStart() {
        super.onStart();

 if(startchk == false) {
     MediaMetadataRetriever retriever = new MediaMetadataRetriever();
     Bitmap bmp = null;

     retriever.setDataSource(this, videourl);
     bmp = retriever.getFrameAtTime();

     if (rotationbtnmode == false) {
         if (bmp.getWidth() > bmp.getHeight()) {
             landscapemode();
         } else {
             potraitmode();
         }
     }

     startchk = true;
 }


        if (player != null) {
    /*        loadingPanel.setVisibility(View.VISIBLE);*/
            mainHandler.postDelayed(updatePlayer, 100);
            mainHandler.postDelayed(hideControls, 3000);
            controlsState = ControlsMode.FULLCONTORLS;
        }



    }

    private void landscapemode() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void potraitmode() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }


    // Define the Callback object and its methods, save the object in a class variable
    private final MediaRouter.Callback mMediaRouterCallback =
            new MediaRouter.Callback() {

                @Override
                public void onRouteSelected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
                    /*if (route.supportsControlCategory(
                            MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {
                        // Stop local playback (if necessary)
                        // ...

                        // Save the new route
                        mRoute = route;

                        // Attach a new playback client
                        mRemotePlaybackClient = new RemotePlaybackClient(this, mRoute);

                        // Start remote playback (if necessary)
                        // ...
                    }*/
                }

                @Override
                public void onRouteUnselected(MediaRouter router, int type, MediaRouter.RouteInfo info) {
/*
                    if (route.supportsControlCategory(
                            MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)) {

                        // Changed route: tear down previous client
                        if (mRoute != null && mRemotePlaybackClient != null) {
                            mRemotePlaybackClient.release();
                            mRemotePlaybackClient = null;
                        }

                        // Save the new route
                        mRoute = route;

                        if (reason != MediaRouter.UNSELECT_REASON_ROUTE_CHANGED) {
                            // Resume local playback  (if necessary)
                            // ...
                        }
                    }*/
                }

                @Override
                public void onRouteAdded(MediaRouter router, MediaRouter.RouteInfo info) {

                }

                @Override
                public void onRouteRemoved(MediaRouter router, MediaRouter.RouteInfo info) {

                }

                @Override
                public void onRouteChanged(MediaRouter router, MediaRouter.RouteInfo info) {

                }

                @Override
                public void onRouteGrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group, int index) {

                }

                @Override
                public void onRouteUngrouped(MediaRouter router, MediaRouter.RouteInfo info, MediaRouter.RouteGroup group) {

                }

                @Override
                public void onRouteVolumeChanged(MediaRouter router, MediaRouter.RouteInfo info) {

                }

};


}
