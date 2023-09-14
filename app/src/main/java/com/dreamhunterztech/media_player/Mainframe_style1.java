package com.dreamhunterztech.media_player;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import java.io.File;
import it.moondroid.coverflow.components.ui.containers.FeatureCoverFlow;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

/**
 * Created by Dreamer on 21-02-2018.
 */

public class Mainframe_style1 extends Fragment {
    private Cursor videoCursor = null;
    style1listAdapter adapter;
    FeatureCoverFlow coverFlow;
    TextSwitcher mSwitcher;
    private int deviceHeight;
    private int deviceWidth;
    DisplayMetrics metrics;
    AdView mAdView;
    private int videoColumnIndex,videoColumndataIndex;
    int count;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View style1view= inflater.inflate(R.layout.stylelayout1,container,false);
        metrics = new DisplayMetrics();
        mAdView = (AdView) style1view.findViewById(R.id.mainpagebanner);
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION};
        videoCursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);

        if (videoCursor != null && videoCursor.getCount() > 0) {
            count = videoCursor.getCount();
            videoCursor.moveToFirst();
        }

        coverFlow = style1view.findViewById(R.id.coverflow);
        mSwitcher = style1view.findViewById(R.id.videotitle);
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                TextView textView = (TextView) inflater.inflate(R.layout.style1_text_title,null);
                return textView;
            }
        });

        Animation in_anim = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_in_top);
        Animation out_anim = AnimationUtils.loadAnimation(getActivity(),R.anim.slide_out_bottom);

        mSwitcher.setInAnimation(in_anim);
        mSwitcher.setOutAnimation(out_anim);

        coverFlow.setOnScrollPositionListener(new FeatureCoverFlow.OnScrollPositionListener() {
            @Override
            public void onScrolledToPosition(int position) {
                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                videoCursor.moveToPosition(position);
                mSwitcher.setText(videoCursor.getString(videoColumnIndex));
            }

            @Override
            public void onScrolling() {

            }
        });

        adapter = new style1listAdapter(getActivity());
        coverFlow.setAdapter(adapter);
        return style1view;
    }



    public class style1listAdapter extends BaseAdapter {
        private Context context;
        ImageView videoimg ;
        LinearLayout layout;
        private String str_video_name,str_video_path;
        private int videoColumnIndex;

        style1listAdapter(Context context)
        {
            this.context = context;
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.style1elementlayout,null);
            videoimg = convertView.findViewById(R.id.sytlelay1img);
            layout = convertView.findViewById(R.id.style1layout);
            videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            videoCursor.moveToPosition(position);
            try {
                Glide.with(context)
                        .load(new File(videoCursor.getString(videoColumnIndex)))
                .apply(centerCropTransform()
                        .placeholder(R.drawable.thumnail_load)
                        .error(R.drawable.no_thumbnail)
                        .priority(Priority.HIGH))
                        .into(videoimg);
            } catch (Exception e) {
                e.printStackTrace();
            }


            videoColumndataIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(position);
            final String str_name = videoCursor.getString(videoColumndataIndex);
            str_video_name = videoCursor.getString(videoColumndataIndex);
            videoColumndataIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            final String str_Data = videoCursor.getString(videoColumndataIndex);
            str_video_path = videoCursor.getString(videoColumndataIndex);

            final String get_video_data = str_video_name + ",>" + str_video_path;

            layout.setTag(get_video_data);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemData = (String) v.getTag();
                    startActivity(new Intent(context, testplayer.class).putExtra("video", itemData));
                }
            });
            return convertView;
        }
    }



    CountDownTimer countDownTimer = new CountDownTimer(30000, 1000) {
        public void onTick(long millisUntilFinished) {

        }

        public void onFinish() {
            AdRequest madRequest = new AdRequest.Builder().build();
            mAdView.loadAd(madRequest);
            countDownTimer.start();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        countDownTimer.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        countDownTimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        countDownTimer.cancel();
    }

    @Override
    public void onStop() {
        super.onStop();
        countDownTimer.cancel();
    }

}
