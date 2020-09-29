package com.maruf.is.rex.audiostory.Player;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class CacheItem {



    public static List<String> getCachedItem(Context context){

        List<String> cacheKey = new ArrayList<>();
        for (String key : DownloadUtil.getCache(context).getKeys()){
            cacheKey.add(key);

        }

        return cacheKey;
    }
}
