package com.dreamhunterztech.media_player;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.mingle.widget.LoadingView;

import java.io.File;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dreamer on 19-03-2018.
 */

public class testplayer  extends AppCompatActivity {

    private AspectRatioFrameLayout mAspectRatioLayout;
    View decorview;
    int uioptions;
    private SimpleExoPlayer mPlayer;
    private SimpleExoPlayerView player;
    private Uri videourl;
    private TextView mEndTime, mCurrentTime;
    private SeekBar mProgress;
    private Handler mainHandler;
    private Boolean databuffered=false;
    public Video_off_player_activity.ControlsMode controlsMode;
    private BandwidthMeter bandwidthMeter;
    private ExtractorsFactory extractorsFactory;
    private TrackSelection.Factory trackSelectionFactory;
    private DefaultTrackSelector trackSelector;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;
    private MediaSource mediaSource;
    private Button speed_btn,res_btn;
    public static final String DRM_SCHEME_EXTRA = "drm_scheme";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String DRM_MULTI_SESSION = "drm_multi_session";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";

    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";

    public static final String ACTION_VIEW_LIST =
            "com.google.android.exoplayer.demo.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";

    private EventLogger eventLogger;
    private DataSource.Factory mediaDataSourceFactory;

    // For backwards compatibility.
    private static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }


    public int speedmode =0;
    public int resmode =0;
    public enum ControlsMode {LOCK, FULLCONTORLS}

    private Video_off_player_activity.ControlsMode controlsState;
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
    private ImageButton play_pausebtn, lockbtn, unlockbtn,back_btn, castbtn, rotationbtn,nightbtn,sizemodebtn,audio_btn,sub_btn;
    private ImageView volimage, brightnessimage;
    public ProgressBar volpbar, brightnessbar;
    private LoadingView medialoading;
    public LinearLayout vollayout, brightnesslayout, swipemediatimelayout, unlocklayout, controllerlayout, playerheadlayout , modelayout;
    public RelativeLayout rootlayout, centerlayout,nightmodelayout;
    public boolean checklandscape = false, rotationbtnmode = false;
    private boolean startchk = false;
    public  MappingTrackSelector.MappedTrackInfo mappedTrackInfo;


    {
        updatePlayer = new Runnable() {
            @Override
            public void run() {
            switch (mPlayer.getPlaybackState()) {
                case ExoPlayer.STATE_BUFFERING:
                    medialoading.setVisibility(View.VISIBLE);

                    break;
               /* case ExoPlayer.STATE_ENDED:
                    finish();
                    break;*/
                case ExoPlayer.STATE_IDLE:
                    medialoading.setVisibility(View.GONE);
                    break;
                case ExoPlayer.STATE_READY:
                    databuffered=true;
                    medialoading.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }

            if(databuffered==true) {

                String totDur = String.format("%02d.%02d.%02d",
                        TimeUnit.MILLISECONDS.toHours(mPlayer.getDuration()),
                        TimeUnit.MILLISECONDS.toMinutes(mPlayer.getDuration()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mPlayer.getDuration())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(mPlayer.getDuration()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer.getDuration())));
                String curDur = String.format("%02d.%02d.%02d",
                        TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition()),
                        TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition()) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition())), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(mPlayer.getCurrentPosition()) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition())));

                mCurrentTime.setText(curDur);
                mEndTime.setText(totDur);
            }
            else
            {
                mCurrentTime.setText("00:00:00");
                mEndTime.setText("00:00:00");
            }
                mProgress.setMax((int) mPlayer.getDuration());
                mProgress.setProgress((int) mPlayer.getCurrentPosition());
                mainHandler.postDelayed(updatePlayer, 100);
            }
        };
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        speed_btn = findViewById(R.id.button_speed);
        res_btn = findViewById(R.id.button_res);
        back_btn = findViewById(R.id.back_btn);
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
                mPlayer.seekTo(seekBar.getProgress());
            }
        });

        swipemediatime = findViewById(R.id.swipeforwardtimetext);
        swipemediatimeadd = findViewById(R.id.swipeforwardaddtimetext);
        brightnessimage = findViewById(R.id.brightnessimageview);
        medialoading = findViewById(R.id.pbarmedialoading);
        volimage = findViewById(R.id.volimageview);
        rotationbtn = findViewById(R.id.rotation_btn);

        volpbar = findViewById(R.id.volume_slider);
        brightnessbar = findViewById(R.id.brightness_slider);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        mAspectRatioLayout = findViewById(R.id.aspectedratio);

        play_pausebtn = findViewById(R.id.play_pause_btn);
        lockbtn = findViewById(R.id.lock_btn);
        unlockbtn = findViewById(R.id.unlock_btn);
        nightbtn = findViewById(R.id.nightmodebtn);
        sizemodebtn = findViewById(R.id.screen_size);
        mainHandler = new Handler();

        audio_btn = findViewById(R.id.audio_btn);
        sub_btn = findViewById(R.id.sub_btn);

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
        if(getIntent().getData() != null) {
            videourl = getIntent().getData();
        }

         else
        {
             String video_data = getIntent().getStringExtra("video");
             videourl = Uri.parse(video_data.toString().split(",>")[1]);
         }
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


        player = (SimpleExoPlayerView) findViewById(R.id.Video_view);
        player.requestFocus();
        player.setUseController(false);
        bandwidthMeter = new DefaultBandwidthMeter();

        Intent intent = getIntent();
        mediaDataSourceFactory = buildDataSourceFactory(true);
        TrackSelection.Factory adaptiveTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
        trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
        trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);
        eventLogger = new EventLogger(trackSelector);

        DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
        if (intent.hasExtra(DRM_SCHEME_EXTRA) || intent.hasExtra(DRM_SCHEME_UUID_EXTRA)) {
            String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
            String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
            boolean multiSession = intent.getBooleanExtra(DRM_MULTI_SESSION, false);
            int errorStringId = R.string.error_drm_unknown;
            if (Util.SDK_INT < 18) {
                errorStringId = R.string.error_drm_not_supported;
            } else {
                try {
                    String drmSchemeExtra = intent.hasExtra(DRM_SCHEME_EXTRA) ? DRM_SCHEME_EXTRA
                            : DRM_SCHEME_UUID_EXTRA;
                    UUID drmSchemeUuid = Util.getDrmUuid(intent.getStringExtra(drmSchemeExtra));
                    if (drmSchemeUuid == null) {
                        errorStringId = R.string.error_drm_unsupported_scheme;
                    } else {
                        drmSessionManager =
                                buildDrmSessionManagerV18(
                                        drmSchemeUuid, drmLicenseUrl, keyRequestPropertiesArray, multiSession);
                    }
                } catch (UnsupportedDrmException e) {
                    errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;
                }
            }
            if (drmSessionManager == null) {
                Toast.makeText(testplayer.this ,errorStringId,Toast.LENGTH_SHORT).show();
                return;
            }
        }

        boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);
        @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =
                ((Applicationinstance) getApplication()).useExtensionRenderers()
                        ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)
                        : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,
                drmSessionManager, extensionRendererMode);
        mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);
        player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player.setPlayer(mPlayer);


        mediaSource = createMediaSource(videourl);

        mPlayer.prepare(mediaSource);




        rotationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checklandscape == false) {
                    landscapemode();
                    checklandscape = true;
                }
                else {
                    potraitmode();
                    checklandscape = false;
                }
            }
        });

        lockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlsState = Video_off_player_activity.ControlsMode.LOCK;
                rootlayout.setVisibility(View.GONE);
                unlocklayout.setVisibility(View.VISIBLE);
            }
        });

        unlockbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controlsState = Video_off_player_activity.ControlsMode.FULLCONTORLS;
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                releaseplayer();
            }
        });

        play_pausebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayer.getPlayWhenReady()) {
                    mPlayer.setPlayWhenReady(false);
                    play_pausebtn.setImageResource(R.drawable.ic_media_play);
                }
                else {
                    mPlayer.setPlayWhenReady(true);
                    play_pausebtn.setImageResource(R.drawable.ic_media_pause);
                }
            }
        });

        speed_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (speedmode == 0)
                {
                    PlaybackParameters playbackParameters = new PlaybackParameters(1.1f, 1.1f);
                    mPlayer.setPlaybackParameters(playbackParameters);
                    speed_btn.setText("2X");
                    speedmode =1;

                }

                if (speedmode == 1)
                {
                    PlaybackParameters playbackParameters = new PlaybackParameters(1.3f, 1.3f);
                    mPlayer.setPlaybackParameters(playbackParameters);
                    speed_btn.setText("3X");
                    speedmode =2;
                }

                if (speedmode == 2)
                {
                    PlaybackParameters playbackParameters = new PlaybackParameters(0.5f, 0.9f);

                    mPlayer.setPlaybackParameters(playbackParameters);
                    speed_btn.setText("0.5X");
                    speedmode =3;

                }

                if (speedmode == 3)
                {
                    PlaybackParameters playbackParameters = new PlaybackParameters(1f, 1f);
                    mPlayer.setPlaybackParameters(playbackParameters);

                    speed_btn.setText("1X");
                    speedmode =0;


                }



            }
        });


        sizemodebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resmode == 0)
                {
                    player.setResizeMode(AspectRatioFrameLayout.LAYOUT_MODE_OPTICAL_BOUNDS);
                    resmode=1;
                }

                if (resmode == 1)
                {
                    player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                    resmode=2;
                }

                if(resmode == 2)
                {
                    player.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    resmode=0;
                }
            }
        });


        res_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    trackSelectionHelper.showSelectionDialog(
                            testplayer.this," Select Video Track", mappedTrackInfo,0);
                }
            }
        });

        audio_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    trackSelectionHelper.showSelectionDialog(
                            testplayer.this," Select Audio Track", mappedTrackInfo,1);
                }
            }
        });

        sub_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    trackSelectionHelper.showSelectionDialog(
                            testplayer.this," Select Subtitle Track", mappedTrackInfo,2);
                }

            }
        });

    }

    private TrackSelectionHelper trackSelectionHelper;

    public MediaSource createMediaSource(Uri uri) {
        return buildMediaSource(uri, null);
    }



    private void hideAllControls() {
        if (controlsState == Video_off_player_activity.ControlsMode.FULLCONTORLS) {
            if (rootlayout.getVisibility() == View.VISIBLE) {
                rootlayout.setVisibility(View.GONE);
            }
        } else if (controlsState == Video_off_player_activity.ControlsMode.LOCK) {
            if (unlocklayout.getVisibility() == View.VISIBLE) {
                unlocklayout.setVisibility(View.GONE);
            }
        }
        decorview.setSystemUiVisibility(uioptions);
    }

    private void showControls() {
        if (controlsState == Video_off_player_activity.ControlsMode.FULLCONTORLS) {
            if (rootlayout.getVisibility() == View.GONE) {
                rootlayout.setVisibility(View.VISIBLE);
            }
        } else if (controlsState == Video_off_player_activity.ControlsMode.LOCK) {
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
                seekSpeed = (TimeUnit.MILLISECONDS.toSeconds(mPlayer.getDuration()) * 0.1);
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
                if (controlsState == Video_off_player_activity.ControlsMode.FULLCONTORLS) {
                    centerlayout.setVisibility(View.GONE);
                    diffX = (long) (Math.ceil(event.getX() - baseX));
                    diffY = (long) Math.ceil(event.getY() - baseY);
                    double brightnessSpeed = 0.03;
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
                            }

                            else if (calculatedTime < 0) {
                                seekDur = String.format("[ - %02d:%02d ]",
                                        Math.abs(TimeUnit.MILLISECONDS.toMinutes(calculatedTime) -
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(calculatedTime))),
                                        Math.abs(TimeUnit.MILLISECONDS.toSeconds(calculatedTime) -
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(calculatedTime))));
                            }
                            totime = String.format("%02d:%02d:%02d",
                                    TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition() + (calculatedTime)),
                                    TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition() + (calculatedTime)) -
                                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mPlayer.getCurrentPosition() + (calculatedTime))), // The change is in this line
                                    TimeUnit.MILLISECONDS.toSeconds(mPlayer.getCurrentPosition() + (calculatedTime)) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mPlayer.getCurrentPosition() + (calculatedTime))));
                            swipemediatimeadd.setText(seekDur);
                            swipemediatime.setText(totime);
                            mProgress.setProgress((int) (mPlayer.getCurrentPosition() + (calculatedTime)));
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
                    calculatedTime = (int) (mPlayer.getCurrentPosition() + (calculatedTime));
                    mPlayer.seekTo(calculatedTime);
                    seek_ok = false;
                }
                showControls();
                break;

        }
        return super.onTouchEvent(event);
    }

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        return ((Applicationinstance) getApplication())
                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }


    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        @C.ContentType int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)
                : Util.inferContentType("." + overrideExtension);
        switch (type) {
            case C.TYPE_DASH:
                return new DashMediaSource.Factory(
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri);
            case C.TYPE_SS:
                return new SsMediaSource.Factory(
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                        buildDataSourceFactory(false))
                        .createMediaSource(uri);
            case C.TYPE_HLS:
                return new HlsMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource.Factory(mediaDataSourceFactory)
                        .createMediaSource(uri);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid,
                                                                              String licenseUrl, String[] keyRequestPropertiesArray, boolean multiSession)
            throws UnsupportedDrmException {
        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,
                buildHttpDataSourceFactory(false));
        if (keyRequestPropertiesArray != null) {
            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],
                        keyRequestPropertiesArray[i + 1]);
            }
        }
        return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback,
                null, mainHandler, eventLogger, multiSession);
    }

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((Applicationinstance) getApplication())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onStart() {
        super.onStart();




        if (mPlayer != null){
            medialoading.setVisibility(View.VISIBLE);
 /*           medialoading.upThrow();*/
            mainHandler.postDelayed(updatePlayer, 100);
            mainHandler.postDelayed(hideControls, 3000);
            controlsState = Video_off_player_activity.ControlsMode.FULLCONTORLS;
        }



    }

    private void landscapemode() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    private void potraitmode() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }


    public void enablingWiFiDisplay() {
        try {
            startActivity(new Intent("android.settings.WIFI_DISPLAY_SETTINGS"));
            return;
        } catch (ActivityNotFoundException activitynotfoundexception) {
            activitynotfoundexception.printStackTrace();
        }

        try {
            startActivity(new Intent("android.settings.CAST_SETTINGS"));
            return;
        } catch (Exception exception1) {
            Toast.makeText(getApplicationContext(), "Device not supported", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseplayer();
    }


    private void releaseplayer()
    {
        if (player != null) {
            mPlayer.setPlayWhenReady(false);
            mPlayer.release();
            player = null;
            trackSelector = null;
            trackSelectionHelper = null;
            eventLogger = null;
        }
        finish();
    }


}
