package com.maruf.is.rex.audiostory;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.logging.Logger;

import io.paperdb.Paper;

public class AudioPlayerActivity extends AppCompatActivity {
    private AudioPlayerServices mService;
    private boolean mBound = false;
    public SimpleExoPlayer player;
    private LocalBroadcastManager localBroadcastManager;
    public TextView audioName, audioArtistAlbum;
    public SeekBar audioSeekbar;
    public ImageView audioPlayPause;
    private boolean dragging;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        audioName = findViewById(R.id.playerAudioName);
        audioArtistAlbum = findViewById(R.id.playerAudioArtistAlbum);
        audioSeekbar = findViewById(R.id.playerAudioProgress);
        audioPlayPause = findViewById(R.id.playerAudioPlayPause);




        Paper.init(this);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(playerStateChangeReciever,new IntentFilter("PLAYER_STATE_LISTENER") );


        audioPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAudioPlayPause();
            }
        });


        audioSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int currentProgress =0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("onProgressChanged", " "+i);
                currentProgress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player!=null){
                    player.seekTo((player.getDuration()/100)*currentProgress);
                }
            }
        });

    }

    private void initializePlayer() {
        if (player == null && mBound) {
            player = mService.getplayerInstance();

            Log.d("Yes", "Connected");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudioPlayerServices.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        initializePlayer();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);
        mBound = false;
        localBroadcastManager.unregisterReceiver(playerStateChangeReciever);
    }

    @Override
    protected void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(playerStateChangeReciever);

    }

    private BroadcastReceiver playerStateChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           getPlayerInstence();
           playerStateListener();

        }
    };

    private void getPlayerInstence() {
        if(mBound){
            player = mService.getplayerInstance();

            if (player!=null){
                setPlayerAudioInfo(player);
                Log.d("PlayerCall", "Not null");
            }else {
                Log.d("PlayerCall", " null");

            }

        }
    }
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("yes", "connected");

            AudioPlayerServices.LocalBinder binder = (AudioPlayerServices.LocalBinder) iBinder;
            mService = binder.getService();

            mBound = true;
            getPlayerInstence();
            if (player!=null){
                Log.d("yes", "player not null");
                setPlayerAudioInfo(player);
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;

            Log.d("yes", " not connected");

        }
    };
  public void playerStateListener(){
        if (mBound){
            getPlayerInstence();

            if (player != null){
                Audio audio = Audio.class.cast(player.getCurrentTag());
                player.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                        //audioSeekbar.setMax((int) (player.getDuration()/60));
                        Log.d("PlayBackState", "Timeline change" +"->Reason "+reason);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.d("PlayBackState", "Track change");
                        setPlayerAudioInfo(player);


                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {
                        Log.d("PlayBackState", "Loading "+ isLoading);
                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                        Log.d("PlayBackState", "onPlayerStateChanged");
                        if (playWhenReady && playbackState == Player.STATE_READY) {
                            // media actually playing
                            setPlayerPlayPause(Common.PLAYER_PLAYING);
                        } else if (playWhenReady) {
                            // might be idle (plays after prepare()),
                            // buffering (plays when data available)
                            // or ended (plays when seek away from end)

                            if(playbackState==Player.STATE_BUFFERING){
                                setPlayerPlayPause(Common.PLAYER_BUFFERING);
                            }else if (playbackState == Player.STATE_ENDED){
                                setPlayerPlayPause(Common.PLAYER_PAUSE);
                            }
                        } else {
                            // player paused in any state
                            setPlayerPlayPause(Common.PLAYER_PAUSE);



                        }

                    }

                    @Override
                    public void onRepeatModeChanged(int repeatMode) {

                    }

                    @Override
                    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                        Log.d("ExoplayerError", error.getMessage());

                    }

                    @Override
                    public void onPositionDiscontinuity(int reason) {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }

                    @Override
                    public void onSeekProcessed() {
                    }
                });
                if (audio!=null){
                    Log.d("playerStateChange", audio.getName());
                }
            }else {
                Log.d("playerStateChange", "Player return null");

            }
        }else {
            Log.d("playerStateChange", "Not bound");

        }
    }

    private void btnAudioPlayPause() {



       Audio audioList =  Paper.book().read("audioList");
        if (player == null ){
            Log.d("btnAudioPlayPause", "null");

            if (audioList!=null){
                Intent intent = new Intent(this, AudioPlayerServices.class);

                Util.startForegroundService(this, intent);
            }
        }else {
            Log.d("btnAudioPlayPause", "not null");

            Log.d("btnAudioPlayPause", " "+player.getPlayWhenReady());



            if (mBound){
                if (!mService.onStartedService){
                    Log.d("btnAudioPlayPause", "not service running");
                    if (audioList!=null){
                        Intent intent = new Intent(this, AudioPlayerServices.class);

                        Util.startForegroundService(this, intent);
                    }
                }else {
                    Log.d("btnAudioPlayPause", "service running");
                    if(player.getPlayWhenReady()){

                        player.setPlayWhenReady(false);
                    }else {
                        if (player.getPlaybackState() == Player.STATE_IDLE){
                            player.seekTo(0);
                        }
                        player.setPlayWhenReady(true);
                    }

                }
            }


        }


    }

    public void setPlayerPlayPause(String state) {
        if (state.equals(Common.PLAYER_PLAYING)){
            audioPlayPause.setImageDrawable(getDrawable(R.drawable.pause));

        }else if (state.equals(Common.PLAYER_PAUSE)){
            audioPlayPause.setImageDrawable(getDrawable(R.drawable.play));

        }else if (state.equals(Common.PLAYER_BUFFERING)){
            audioPlayPause.setImageDrawable(getDrawable(R.drawable.buffering));

        }
    }

    private void setPlayerAudioInfo(SimpleExoPlayer player) {

      if (!mService.onStartedService){
          Audio audioList = Paper.book().read("audioList");


          if (audioList!=null){
              int pos =  Paper.book().read("LastWindowIndex");
              long currentPosition = Paper.book().read("LastCurrentPosition");
              long LastCurrentDuration = Paper.book().read("LastCurrentDuration");

              audioName.setText(audioList.getName());
              audioArtistAlbum.setText(audioList.getArtist()+" "+audioList.getArtist());
              audioSeekbar.setProgress((int) ((currentPosition*100)/LastCurrentDuration));
          }else {
              audioName.setText("File not found for play");
              audioArtistAlbum.setText("File not found for play");
          }
      }

        Log.d("TIme", "Duration "+player.getDuration());

        Audio audio = Audio.class.cast(player.getCurrentTag());
        if (audio!=null){

            if (player.getPlayWhenReady() && player.getPlaybackState() == Player.STATE_READY){
                setPlayerPlayPause(Common.PLAYER_PLAYING);
            }else if (player.getPlayWhenReady() && player.getPlaybackState() == Player.STATE_BUFFERING){
                setPlayerPlayPause(Common.PLAYER_BUFFERING);
            }else {
                setPlayerPlayPause(Common.PLAYER_PAUSE);
            }

            Log.d("updateProgressBar", "called");
            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    audioSeekbar.setProgress((int) ((player.getCurrentPosition()*100)/player.getDuration()));
                    Log.d("TIme", "Duration "+player.getContentPosition());

                    audioSeekbar.setSecondaryProgress((int) ((player.getBufferedPosition()*100)/player.getDuration()));
                    handler.postDelayed(runnable, 1000);
                }
            };
            handler.postDelayed(runnable, 0);

            audioName.setText(audio.getName());
            audioArtistAlbum.setText(audio.getArtist()+" "+audio.getArtist());
        }
        
    }







}
