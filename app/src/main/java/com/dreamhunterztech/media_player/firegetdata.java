package com.dreamhunterztech.media_player;

/**
 * Created by suwas on 30-12-2016.
 */

public class firegetdata
{
    private String videoname;
    private String videothumb;
    private String videourl;

    public firegetdata()
    {

    }


    public firegetdata(String videoname, String videothumb, String videourl,int dmb)
    {
        this.videoname = videoname;
        this.videothumb = videothumb;
        this.videourl = videourl;
    }

    public String getVideoname() {
        return videoname;
    }

    public String getVideothumb() {
        return videothumb;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideoname(String videoname) {
        this.videoname = videoname;
    }

    public void setVideothumb(String videothumb) {
        this.videothumb = videothumb;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }
}
