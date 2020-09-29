package com.maruf.is.rex.audiostory.Player;

import android.app.Notification;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.offline.Download;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.scheduler.Scheduler;
import com.google.android.exoplayer2.ui.DownloadNotificationHelper;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NotificationUtil;
import com.google.android.exoplayer2.util.Util;
import com.maruf.is.rex.audiostory.R;

import java.util.List;

public class AudioDownloadServices extends DownloadService {

    private static final String CHANNEL_ID = "download_channel";
    private static final int JOB_ID = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 1;

    private static int nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;

    private DownloadNotificationHelper notificationHelper;
    private LocalBroadcastManager localBroadcastManager;

    public AudioDownloadServices() {
        super(
                FOREGROUND_NOTIFICATION_ID,
                DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
                CHANNEL_ID,
                R.string.exo_download_notification_channel_name,
                /* channelDescriptionResourceId= */ 0);
        nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new DownloadNotificationHelper(this, CHANNEL_ID);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

    }

    @Override
    protected DownloadManager getDownloadManager() {
        return DownloadUtil.getDownloadManager(this);
    }

    @Nullable
    @Override
    protected Scheduler getScheduler() {
        return null;
    }

    @Override
    protected Notification getForegroundNotification(List<Download> downloads) {
        return notificationHelper.buildProgressNotification(
                R.drawable.ic_launcher_foreground, /* contentIntent= */ null, /* message= */ null, downloads);
    }

    @Override
    protected void onDownloadChanged(Download download) {
        Log.d("State", String.valueOf(download.state));
        Notification notification;
        if (download.state == Download.STATE_COMPLETED) {



            Intent intent = new Intent("DOWNLOAD_COMPLETE");
            localBroadcastManager.sendBroadcast(intent);

            notification =
                    notificationHelper.buildDownloadCompletedNotification(
                            R.drawable.exo_icon_play,
                            /* contentIntent= */ null,
                            Util.fromUtf8Bytes(download.request.data));
        } else if (download.state == Download.STATE_FAILED) {
            notification =
                    notificationHelper.buildDownloadFailedNotification(
                            R.drawable.exo_icon_stop,
                            /* contentIntent= */ null,
                            Util.fromUtf8Bytes(download.request.data));

            Toast.makeText(this, String.valueOf(download.failureReason), Toast.LENGTH_SHORT).show();
        } else {
            return;
        }
        NotificationUtil.setNotification(this, nextNotificationId++, notification);
    }


}
