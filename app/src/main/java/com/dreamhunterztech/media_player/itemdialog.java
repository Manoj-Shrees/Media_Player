package com.dreamhunterztech.media_player;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

/**
 * Created by Dreamer on 15-03-2018.
 */

public class itemdialog  {
private Context context;
private String url,str_videoname;
private Dialog videviewpannel;
private VideoView videpreview;
private TextView videoname;
private int duration;
ImageButton mediainfo , mediaplay , mediashare , mediadelete;

    public itemdialog(Context context,String url,String str_videoname ) {
        this.context = context;
        this.url = url;
        this.str_videoname = str_videoname;
        inisitlize();
    }


    private void inisitlize()
    {
        videviewpannel = new Dialog(context);
        videviewpannel.requestWindowFeature(Window.FEATURE_NO_TITLE);
        videviewpannel.getWindow().setWindowAnimations(R.style.animateddialog);
        videviewpannel.setCanceledOnTouchOutside(false);
        videviewpannel.setCancelable(false);
        videviewpannel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        videviewpannel.setContentView(R.layout.videoitemdetaillayout);
        ImageView videviewpannelclose = videviewpannel.findViewById(R.id.videdetaildialogclose);
        videoname = videviewpannel.findViewById(R.id.mediavideoname);
        videoname.setText(str_videoname);
        videoname.setSelected(true);
        videpreview = videviewpannel.findViewById(R.id.videopreview);
        videpreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.setVolume(0, 0);
            duration = videpreview.getDuration();
            Log.e(">>test",duration+"");
            videpreview.seekTo(duration);
        }
       });
        videpreview.setVideoURI(Uri.parse(url));
        videpreview.setSoundEffectsEnabled(false);
        videpreview.start();
        videviewpannelclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                videviewpannel.dismiss();
            }
        });
        videviewpannel.show();
        countDownTimer.start();

    }




    CountDownTimer countDownTimer = new CountDownTimer(15000, 1000) {
        public void onTick(long millisUntilFinished) {
        }


        public void onFinish() {
            videpreview.pause();
            videpreview.seekTo(duration);
            videpreview.start();
            countDownTimer.start();
        }
    };



}
