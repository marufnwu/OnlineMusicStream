package com.maruf.is.rex.audiostory.Common;

import android.content.Context;

import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class DownloadUtil {

    private static Cache cache;
    private static DownloadManager downloadManager;
    private static DatabaseProvider databaseProvider;


   public static synchronized Cache getCache(Context context) {
        if (cache == null) {
            File cacheDirectory = new File(context.getExternalFilesDir(null), "Offline");
            cache = new SimpleCache(cacheDirectory, new NoOpCacheEvictor());
        }


        return cache;
    }



    public static synchronized DownloadManager getDownloadManager(Context context){

                databaseProvider = new ExoDatabaseProvider(context);

              DefaultDataSourceFactory  dataSourceFactory = new DefaultDataSourceFactory(
                      context,
                      Util.getUserAgent(context, "AudioStory"));

        // Create the download manager.
                downloadManager = new DownloadManager(
                        context,
                        databaseProvider,
                        getCache(context),
                        dataSourceFactory);

        return downloadManager;
    }

}
