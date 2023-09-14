package com.dreamhunterztech.media_player;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.dreamhunterztech.media_player.datahandlers.offlinedatahandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dreamer on 19-02-2018.
 */

public class Startscreen  extends AppCompatActivity {

    String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_SYNC_SETTINGS};
    int permission_all = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!checkPermissions(this , permissions) )
        {
            ActivityCompat.requestPermissions(this,permissions,permission_all);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getApplicationContext() != null && permissions != null) {
                if (!Settings.System.canWrite(getApplicationContext()))
                {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 200);
                }
                else
                    startactivity();
            }


        }

        else
            startactivity();
    }



    private void startactivity()
    {
        startActivity(new Intent(Startscreen.this, Mainframe.class));
    }


    private boolean checkPermissions(Context context , String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            if (ActivityCompat.checkSelfPermission(context, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            if (ActivityCompat.checkSelfPermission(context, permissions[1]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            if (ActivityCompat.checkSelfPermission(context, permissions[2]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }

            if (ActivityCompat.checkSelfPermission(context, permissions[3]) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }


        }
        return true;
    }



    private void filechecker()
    {
       offlinedatahandler filedata  = new offlinedatahandler(getApplicationContext().getString(R.string.libfileurl),getApplicationContext().getString(R.string.filename),"name");
       File rootPath = new File(Environment.getExternalStorageDirectory(),getApplicationContext().getString(R.string.libfileurl));
        File datafile = new File(rootPath,getApplicationContext().getString(R.string.filename));
        if(!rootPath.exists()) {
            rootPath.mkdirs();
        }

        if(!datafile.exists())
        {
            try {
                filedata.createfile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


}
