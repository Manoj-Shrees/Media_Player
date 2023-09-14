package com.dreamhunterztech.media_player;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.bumptech.glide.request.RequestOptions.centerCropTransform;

/**
 * Created by Dreamer on 26-01-2018.
 */

public class Mainframe_style3 extends Fragment {

    ListView videolist;
    int count;
    String thumbPath;
    Context context;
    String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA, MediaStore.Video.Thumbnails.VIDEO_ID};
    DisplayMetrics metrics;
    VideoListAdapter videoListAdapter;
    private Cursor videoCursor = null;
    private int deviceHeight;
    private int deviceWidth;
    TextView no_video_txt;


    public static String convertBytes(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View style3view = inflater.inflate(R.layout.stylelayout3,container,false);
        context = getActivity();
        metrics = new DisplayMetrics();
        getScreenSize();
        videolist = (ListView) style3view.findViewById(R.id.medialist);
        no_video_txt = (TextView) style3view.findViewById(R.id.no_video_txt);

        return style3view;
    }


    private void initialization() {
        String[] videoProjection = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION};
        videoCursor = getActivity().managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);

        if (videoCursor != null && videoCursor.getCount() > 0) {
            count = videoCursor.getCount();
            for(int i=0; i<= count;i++)
            {
                if(i%4==0)
                count+=1;
            }
            videoCursor.moveToFirst();
            no_video_txt.setVisibility(View.GONE);
        }

        else {
            count = 0;
        }

        if (count == 0)
        {
            no_video_txt.setVisibility(View.VISIBLE);
            videolist.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initialization();
    }

    public class VideoListAdapter extends BaseAdapter {
        ContentResolver contentResolver;
        private Context vContext;
        private int videoColumnIndex;
        ArrayList<String> arrayList;
        int AD_TYPE =0 , CONTENT_TYPE=1;

        public VideoListAdapter(Context vContext) {
            this.vContext = vContext;
            this.arrayList = new ArrayList<>();
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            metrics = new DisplayMetrics();

            if(position % 4 == 0)
            {
                AdView adView = new AdView(vContext);
                adView.setAdSize(AdSize.BANNER);
                adView.setAdUnitId(vContext.getString(R.string.admob_banner_id));

                float density = vContext.getResources().getDisplayMetrics().density;
                int height = Math.round(AdSize.BANNER.getHeight() * density);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                        AbsListView.LayoutParams.FILL_PARENT,
                        height);
                adView.setLayoutParams(params);
                AdRequest madRequest = new AdRequest.Builder().build();
                adView.loadAd(madRequest);
                return adView;
            }

                convertView = LayoutInflater.from(vContext).inflate(R.layout.style3elementlayout, parent, false);
                final LinearLayout video_ll = (LinearLayout) convertView.findViewById(R.id.mediaroot);
                RelativeLayout main_name_ll = (RelativeLayout) convertView.findViewById(R.id.layout1);
                TextView video_name = (TextView) convertView.findViewById(R.id.medianame);
                TextView video_path = (TextView) convertView.findViewById(R.id.medialoc);
                TextView video_size = (TextView) convertView.findViewById(R.id.mediasize);
                TextView video_date = (TextView) convertView.findViewById(R.id.mediadate);
                TextView video_duration = (TextView) convertView.findViewById(R.id.mediaduration);
                ImageView thumbImage = (ImageView) convertView.findViewById(R.id.mediathumnail);
                final String str_video_name, str_video_path;
                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);
                video_name.setText(videoCursor.getString(videoColumnIndex));
                str_video_name = videoCursor.getString(videoColumnIndex);
                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);
                video_path.setText(videoCursor.getString(videoColumnIndex));
                str_video_path = videoCursor.getString(videoColumnIndex);

                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);
                video_size.setText("" + convertBytes(Long.parseLong(videoCursor.getString(videoColumnIndex))) + "");

                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);

                SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy");
                long ddd = Long.parseLong(videoCursor.getString(videoColumnIndex));
                String result = df.format(ddd);
                final String str_date = result;
                video_date.setText("" + result);

                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DURATION);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);
                int duration = Integer.parseInt(videoCursor.getString(videoColumnIndex));
                int timeInmillisec = (duration);
                long duration1 = timeInmillisec / 1000;
                long hours = duration1 / 3600;
                long minutes = (duration1 - hours * 3600) / 60;
                long seconds = duration1 - (hours * 3600 + minutes * 60);
                final String str_duration;
                if (hours == 00) {
                    str_duration = "" + twoDigitString((int) minutes) + " : " + twoDigitString((int) seconds);
                } else {
                    str_duration = "" + twoDigitString((int) hours) + " : " + twoDigitString((int) minutes) + " : " + twoDigitString((int) seconds);
                }

                video_duration.setText(str_duration);

                videoColumnIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
                videoCursor.moveToPosition(position - (int) Math.ceil(position / 4) - 1);

                int videoId = videoCursor.getInt(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                Cursor videoThumbnailCursor = getActivity().managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                        thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + videoId, null, null);

                if (videoThumbnailCursor.moveToFirst()) {
                    thumbPath = videoThumbnailCursor.getString(videoThumbnailCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                }

                video_path.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_path.getTypeface(), (float) (deviceHeight * 2 / 100)));
                video_duration.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_duration.getTypeface(), deviceHeight * 3 / 100));
                video_size.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_size.getTypeface(), (float) (deviceHeight * 2.5 / 100)));
                video_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, DetermineTextSize.determineTextSize(video_date.getTypeface(), (float) (deviceHeight * 2.2 / 100)));

                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (deviceWidth / 2.5), deviceHeight / 6);
                thumbImage.setLayoutParams(params);


                try {
                    Glide.with(vContext)
                            .load(new File(videoCursor.getString(videoColumnIndex)))
                            .apply(centerCropTransform()
                                    .placeholder(R.drawable.thumnail_load)
                                    .error(R.drawable.no_thumbnail)
                                    .priority(Priority.HIGH))
                            .into(thumbImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final String get_video_data = str_video_name + ",>" + str_video_path;

                video_ll.setTag(get_video_data);

                video_ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String itemData = (String) v.getTag();
                        startActivity(new Intent(vContext, testplayer.class).putExtra("video", itemData));
                    }
                });

                video_ll.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.e(">>item_pos",":"+position);
                        return false;
                    }
                });
                return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if(position % 3 == 0)
                return AD_TYPE;
            return CONTENT_TYPE;
        }

        private String twoDigitString(int number) {
            if (number == 0) {
                return "00";
            }
            if (number / 10 == 0) {
                return "0" + number;
            }
            return String.valueOf(number);
        }
    }

    public void getScreenSize() {
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        deviceHeight = metrics.heightPixels;
        deviceWidth = metrics.widthPixels;
    }

    @Override
    public void onStart() {
        super.onStart();
        new setdatalist().execute();
    }




    public class setdatalist extends AsyncTask
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object[] params) {
          videoListAdapter = new VideoListAdapter(context);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
   /*         feedrefreshlayout.setRefreshing(false);*/
            videolist.setAdapter(videoListAdapter);
            videoListAdapter.notifyDataSetChanged();
        }
    }






}

