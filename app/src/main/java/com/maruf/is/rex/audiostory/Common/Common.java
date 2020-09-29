package com.maruf.is.rex.audiostory.Common;

import android.app.ActivityManager;
import android.content.Context;

import com.maruf.is.rex.audiostory.Database.RoomDatabase;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Retrofit.RetrofitClient;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;

import java.util.List;


public class Common {

    public static final String SHIMMER_FOR_LATEST_UPDATED = "recentUpdateShimmer";
    public static final String SHIMMER_FOR_AUDIO_LIST = "audioListShimmer";
    public static String baseURI = RetrofitClient.BASEURI;
    public static String MEDIA_SESSION_TAG ="audio_story_media_session";

    public static String PLAYER_PLAYING ="playing";
    public static String PLAYER_PAUSE ="pause";
    public static String PLAYER_BUFFERING ="buffering";
    public static RoomDatabase roomDatabase;



    public static int currentAudioId;
    public static int currentCategoryId = -1;
    public static int playingId = -1;

    public static String getTimesInHMS(int seconds){
        int p1 = seconds % 60;
        int p2 = seconds / 60;
        int p3 = p2 % 60;
        p2 = p2 / 60;

        return p2+":"+p3+":"+p1;
    }

    public static boolean isMyServiceRunning(Context mContext) {
        ActivityManager manager = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (AudioPlayerServices.class.getName().equals(
            service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
