package com.dreamhunterztech.media_player;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;

import static com.androidquery.util.AQUtility.getContext;
import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

/**
 * Created by Dreamer on 26-02-2018.
 */

public class Mainframe_style2 extends Fragment {

    private Cursor videoCursor = null,videothumb=null,videoname=null;
    RecyclerView gridView;
    private style2adapter adapter;
    int count;
    AdView mAdView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View style2view = inflater.inflate(R.layout.stylelayout2,container,false);
        mAdView = (AdView) style2view.findViewById(R.id.mainpagebanner);
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION};
        videoname =  videothumb = videoCursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
        if (videoCursor != null && videoCursor.getCount() > 0) {
            count = videoCursor.getCount();
            videoCursor.moveToFirst();
        }

        videothumb.moveToFirst();

        gridView = (RecyclerView) style2view.findViewById(R.id.style2gridview);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.COLUMN);
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);
        gridView.setLayoutManager(layoutManager);
        adapter = new style2adapter();
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return style2view;
    }


    public class style2adapter extends RecyclerView.Adapter<style2holder>
    {
        String str_name,str_video_path;
        private int videoColumnIndex,videoColumndataIndex,videonameindex;

        @Override
        public style2holder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.style2elementlayout, parent, false);

            return  new style2holder(itemView);
        }

        @Override
        public void onBindViewHolder(final style2holder holder, int position) {

            videoColumnIndex = videothumb.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
            videothumb.moveToPosition(position);
            try {
                Glide.with(getActivity())
                        .load(new File(videothumb.getString(videoColumnIndex)))
                        .apply(centerCropTransform()
                                .placeholder(R.drawable.thumnail_load)
                                .error(R.drawable.no_thumbnail)
                                .priority(Priority.HIGH))
                        .into(holder.videoimg);
            } catch (Exception e) {
                e.printStackTrace();
            }


            videoColumndataIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            videoCursor.moveToPosition(position);
            str_video_path = videoCursor.getString(videoColumndataIndex);

            holder.layout.setTag(str_video_path);

            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemData = (String) v.getTag();
                    new itemdialog(getContext(),itemData,new File(String.valueOf(itemData)).getName());
                }
            });

        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return count;
        }

    }


    private class style2holder extends RecyclerView
    {
     ImageView videoimg;
     LinearLayout layout;
        public style2holder(View itemView) {

            videoimg = itemView.findViewById(R.id.style2img);
            layout = itemView.findViewById(R.id.style2element);
            layout.setTag(null);
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
