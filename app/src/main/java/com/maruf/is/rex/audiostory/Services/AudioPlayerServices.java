package com.maruf.is.rex.audiostory.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Common.DownloadUtil;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Model.MyTaskParam;
import com.maruf.is.rex.audiostory.R;

import java.util.ArrayList;
import java.util.List;


import io.paperdb.Paper;

public class AudioPlayerServices  extends Service implements Player.EventListener {
    private final IBinder binder = new LocalBinder();
    private static final String PLAYBACK_CHANEL_ID = "1001" ;

    private static final int PLAYBACK_NOTIFICATION_ID = 1002;
    private SimpleExoPlayer player;
    private List<Audio> audios = new ArrayList<>();
     Context context;
    private PlayerNotificationManager playerNotificationManager;
    private MediaSessionCompat mediaSession;
    private PlaybackStateCompat.Builder stateBuilder;
    private MediaSessionConnector mediaSessionConnector;
    public LocalBroadcastManager localBroadcastManager;
    private ConcatenatingMediaSource concatenatingMediaSource;
    private boolean mediaSourceStore = false;
    private boolean windowIndexApplicable = false;
    public boolean onStartedService = false;


    @Override
    public void onCreate() {
        super.onCreate();



        Log.d("CallServices", "Success");

        context = this;
        Paper.init(context);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);





    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("CallServices", "OnBind");

        return binder;
    }

   public void initPlayer(){
       player = ExoPlayerFactory.newSimpleInstance(context);
       player.addListener(new Player.EventListener() {
           @Override
           public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

           }

           @Override
           public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

           }

           @Override
           public void onLoadingChanged(boolean isLoading) {

           }

           @Override
           public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

               Intent intent = new Intent("PLAYER_STATE_LISTENER");
               //intent.putExtra("id", uriList.get(position).getId());
               localBroadcastManager.sendBroadcast(intent);



               if(playWhenReady && playbackState== Player.STATE_READY){
                   //playing



                   Log.d("CurrentWIndowIndex", String.valueOf( player.getCurrentWindowIndex()));
                   //playingIndex = (int) player.getCurrentTag();

                   //Object object = player.getCurrentTag();

                   Audio audio = Audio.class.cast(player.getCurrentTag());
                   Common.currentAudioId = audio.getId();

                   Log.d("TagCurrent", audio.getAlbum());
               }else{
                   //not playing


               }



           }

           @Override
           public void onPlayerError(ExoPlaybackException error) {

           }

           @Override
           public void onPositionDiscontinuity(int reason) {

           }

           @Override
           public void onSeekProcessed() {

           }
       });


       DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
               Util.getUserAgent(this, "AudioStory"));


       CacheDataSourceFactory cacheDataSourceFactory =
               new CacheDataSourceFactory(DownloadUtil.getCache(this), dataSourceFactory);

       concatenatingMediaSource = new ConcatenatingMediaSource();
       MediaSource audioSource = null;


       //Log.d("Link", Common.baseURI+audio.getPrimaryUrl());


      if (audios!=null){
          for(Audio audio : audios){
              if (!audio.getPrimaryUrl().isEmpty()){
                  audioSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                          .setTag(audio)
                          .createMediaSource(Uri.parse(Common.baseURI + audio.getPrimaryUrl()));
              }else if (!audio.getSecondaryUrl().isEmpty()){
                  audioSource = new ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                          .setTag(audio)
                          .createMediaSource(Uri.parse(Common.baseURI + audio.getSecondaryUrl()));
              }
              concatenatingMediaSource.addMediaSource(audioSource);
          }
      }






       player.prepare(concatenatingMediaSource); // when using playist


       if(windowIndexApplicable){
           long seekPos = Paper.book().read("LastCurrentPosition");
           player.seekTo(seekPos);
       }


       mediaSession = new MediaSessionCompat(context, Common.MEDIA_SESSION_TAG);

       mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
               MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

       stateBuilder = new PlaybackStateCompat.Builder()
               .setActions(PlaybackStateCompat.ACTION_PLAY |
                       PlaybackStateCompat.ACTION_PAUSE);

       mediaSession.setPlaybackState(stateBuilder.build());

       mediaSession.setCallback(new com.maruf.is.rex.audiostory.Services.MySessionCallback());
       mediaSession.setActive(true);


       mediaSessionConnector = new MediaSessionConnector(mediaSession);
       mediaSessionConnector.setQueueNavigator(new MediaSessionConnector.QueueNavigator() {
           @Override
           public long getSupportedQueueNavigatorActions(Player player) {
               return 0;
           }

           @Override
           public void onTimelineChanged(Player player) {

           }

           @Override
           public void onCurrentWindowIndexChanged(Player player) {

           }

           @Override
           public long getActiveQueueItemId(@Nullable Player player) {
               return 0;
           }

           @Override
           public void onSkipToPrevious(Player player, ControlDispatcher controlDispatcher) {

           }

           @Override
           public void onSkipToQueueItem(Player player, ControlDispatcher controlDispatcher, long id) {

           }

           @Override
           public void onSkipToNext(Player player, ControlDispatcher controlDispatcher) {

           }

           @Override
           public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, Bundle extras, ResultReceiver cb) {
               return false;
           }
       });
       mediaSessionConnector.setPlayer(player);


      if (!audios.isEmpty()){
          playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                  context, PLAYBACK_CHANEL_ID, R.string.PLAYBACK_CHANEL_NAME,
                  R.string.chanel_descrption,
                  PLAYBACK_NOTIFICATION_ID,
                  new PlayerNotificationManager.MediaDescriptionAdapter() {
                      @Override
                      public String getCurrentContentTitle(Player player) {
                          return audios.get(player.getCurrentWindowIndex()).getName();

                      }

                      @Nullable
                      @Override
                      public PendingIntent createCurrentContentIntent(Player player) {

                          return null;
                      }

                      @Nullable
                      @Override
                      public String getCurrentContentText(Player player) {
                          return audios.get(player.getCurrentWindowIndex()).getEpisodeDate() +
                                  audios.get(player.getCurrentWindowIndex()).getArtist();


                      }

                      @Nullable
                      @Override
                      public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
                          return  BitmapFactory.decodeResource(getResources(), R.drawable.banner);
                      }
                  },

                  new PlayerNotificationManager.NotificationListener() {
                      @Override
                      public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
                          stopSelf();
                      }

                      @Override
                      public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
                          startForeground(notificationId, notification);

                      }

                  }
          );
          playerNotificationManager.setMediaSessionToken(mediaSession.getSessionToken());
          playerNotificationManager.setColorized(true);
          playerNotificationManager.setPlayer(player);
      }




   }

    private void play() {
        if (player!=null){
            player.setPlayWhenReady(true);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStartedService = true;
       Log.d("CallServices", "onStartCommand");

        audios  = intent.getParcelableArrayListExtra("AudioList");
        if(audios == null){
            Audio audio = Paper.book().read("audioList");
            audios = new ArrayList<>();
            audios.add(audio);
            if (audios==null){
                stopSelf();
                Log.d("LOST", "null");
            }else {
                windowIndexApplicable = true;
                Log.d("LOST", "not null");

            }
        }else {
            windowIndexApplicable= false;
        }

        if (player!=null){
            player.release();
            player = null;
        }
        initPlayer();
        play();






        return START_NOT_STICKY;
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d("PlayerError", error.getMessage());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();



   if (onStartedService){
       if (player!=null){
           new Worker().execute(new MyTaskParam(Audio.class.cast(player.getCurrentTag()),
                   player.getCurrentWindowIndex(),
                   player.getCurrentPosition(),
                   player.getDuration()));

           mediaSession.release();
           mediaSessionConnector.setPlayer(null);
           if (playerNotificationManager!=null){
               playerNotificationManager.setPlayer(null);
           }
           player.release();
           player=null;

       }
   }


        onStartedService = false;




        Log.d("OnDistoy", "called");


    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        stopSelf();
    }

    public SimpleExoPlayer getplayerInstance() {
        if (player!=null){
            return player;
        }else {
          initPlayer();
          return player;
        }

    }

        public class LocalBinder extends Binder {
        public AudioPlayerServices getService() {
            // Return this instance of LocalService so clients can call public methods
            return AudioPlayerServices.this;
        }
    }




    private class Worker extends AsyncTask<MyTaskParam, Void, String> {

        @Override
        protected String doInBackground(MyTaskParam... arg0) {
            Log.i("SomeTag",
                    "start do in background at " + System.currentTimeMillis());
            String data = null;

            try {

               Audio audioList = arg0[0].audio;

                audioList.currSeekPosition = player.getCurrentPosition();
                audioList.totalDuration = player.getDuration();
                Common.roomDatabase.recentAudioDao().insertAudio(audioList);


                Paper.book().write("audioList", audioList);
                Paper.book().write("LastWindowIndex", arg0[0].currentWindowIndex);
                Paper.book().write("LastCurrentPosition", arg0[0].currentPosition);
                Paper.book().write("LastCurrentDuration", arg0[0].currentDuration);


                Log.i("SomeTag",
                        "doInBackGround done at " + System.currentTimeMillis());
            } catch (Exception e) {
            }
            return "done->('.')";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("SomeTag", System.currentTimeMillis() / 1000L
                    + " post execute \n" + result);
        }
    }




}
