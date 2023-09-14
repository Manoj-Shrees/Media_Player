package com.dreamhunterztech.media_player.searchelements;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import java.util.ArrayList;

/**
 * Created by Dreamer on 30-03-2018.
 */

public class Getdatalist {
    String[] videoProjection  = {MediaStore.Video.Media._ID, MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.VideoColumns.DURATION};
    Cursor videoCursor;
    int videoindexdata,videoColumndataIndex;

    Getdatalist(Activity activity)
    {
      videoCursor = activity.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, null, null, null);
      videoCursor.moveToFirst();

    }


    public int getcount()
    {
        int count=videoCursor.getCount();
        return count;
    }

    public String getdata(int index)
    {
        String data;
            videoindexdata=  videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            videoCursor.moveToPosition(index);
           data= videoCursor.getString(videoindexdata);
        return  data;
    }

    public String getdataurl(int index)
    {
        videoColumndataIndex = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        videoCursor.moveToPosition(index);
        final String str_Data = videoCursor.getString(videoColumndataIndex);
        String  url = videoCursor.getString(videoColumndataIndex);
        return url;
    }

}
