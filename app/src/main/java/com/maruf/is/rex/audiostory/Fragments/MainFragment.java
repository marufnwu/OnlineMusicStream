package com.maruf.is.rex.audiostory.Fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.maruf.is.rex.audiostory.AudioPlayerActivity;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.R;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;

import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class MainFragment extends Fragment {


    public static String HOME_FRAGMENT = "HomeFragment";
    public static String AUDIO_LIST_FRAGMENT = "AudioListFragment";
    public static String OFFLIINE_FRAGMENT = "OfflineFragment";
    public static int PREVIOUS_CATEGORY ;


    FrameLayout frameLayout;
    private AudioPlayerServices mService;
    private boolean mBound = false;
    public SimpleExoPlayer player;
    ImageView playerPlayPause;
    TextView palyerAudioName,  playerAudioArtist;
    LinearLayout mini_player_layout;
    Fragment activeFragment, homeFragment, audioListFragment, offlineFragment;
    com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigationView;




    private LocalBroadcastManager localBroadcastManager;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private HashMap fragmentMap;
    private Fragment prevFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_main, container, false);
        frameLayout = view.findViewById(R.id.framelayout);


        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, audioListFragment, AUDIO_LIST_FRAGMENT).hide(audioListFragment);
        fragmentTransaction.add(R.id.framelayout, offlineFragment, OFFLIINE_FRAGMENT).hide(offlineFragment);
        fragmentTransaction.add(R.id.framelayout, homeFragment, HOME_FRAGMENT).commit();
        activeFragment = homeFragment;

        playerPlayPause = view.findViewById(R.id.mini_player_playPause);
        palyerAudioName = view.findViewById(R.id.mini_player_audio_name);
        playerAudioArtist = view.findViewById(R.id.mini_player_audio_artist);
        mini_player_layout = view.findViewById(R.id.mini_player_lin_layout);
        bottomNavigationView = view.findViewById(R.id.bottomNavigation);

        playerPlayPause.setOnClickListener(view1 -> audioPlayPause());
        mini_player_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AudioPlayerActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.menuHome:
                        hideSowFragment(homeFragment, HOME_FRAGMENT);
                        return true;
                    case R.id.menuPlayer:
                        startActivity(new Intent(getContext(), AudioPlayerActivity.class));
                        break;
                    case R.id.menuOffline:
                        hideSowFragment(offlineFragment, OFFLIINE_FRAGMENT);
                        return true;


                }

                return false;
            }
        });

        showHidePlayerView();
        return view;
    }

    private void hideSowFragment(Fragment selectFragment, String tag) {

        if (selectFragment.getTag()!= null) {
            if (selectFragment.getTag().equals(AUDIO_LIST_FRAGMENT)) {
                if (PREVIOUS_CATEGORY != Common.currentCategoryId){
                   FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.detach(audioListFragment).attach(audioListFragment);
                    ft.commitAllowingStateLoss();
                }
                PREVIOUS_CATEGORY = Common.currentCategoryId;
                prevFragment = activeFragment;
            } else {
                prevFragment = null;
            }
        }

        //Log.d("CurrentFragment", ""+getVisibleFragment().getTag());

        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.hide(activeFragment)
                .show(selectFragment)
                .commit();

        activeFragment = selectFragment;


        if (selectFragment.getTag()!= null) {
            if (selectFragment.getTag().equals(AUDIO_LIST_FRAGMENT)) {
                if (PREVIOUS_CATEGORY != Common.currentCategoryId) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setReorderingAllowed(true);
                    ft.remove(audioListFragment);

                    ft.commitNow();
                }
                PREVIOUS_CATEGORY = Common.currentCategoryId;

            }

        }








    }

    private void initFragmentmanager() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

    }

    private void showHidePlayerView() {

       Audio  audioList =  Paper.book().read("audioList");
        if(player!=null || audioList!=null){
            mini_player_layout.setVisibility(View.VISIBLE);
        }else {
            mini_player_layout.setVisibility(View.GONE);

        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentMap = new HashMap();

        if (savedInstanceState==null){
            homeFragment = new HomeFragment();
            audioListFragment = new AudioListFragment();
            offlineFragment = new OfflineFragment();
        }




        Paper.init(getContext());
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(playerStateChangeReciever,new IntentFilter("PLAYER_STATE_LISTENER") );

        localBroadcastManager.registerReceiver(categoryitemClickReceiver,new IntentFilter("CATEGORY_ITEM_LISTENER") );
        Intent intent = new Intent(getContext(), AudioPlayerServices.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.d("LifeCycle", "onCreate");


    }

    private Fragment getVisibleFragment() {

        List<Fragment> fragments = fragmentManager.getFragments();

        Log.d("CurrentFragment", ""+fragments.size());
        for (Fragment fragment : fragments) {
            if (fragment != null && !fragment.isHidden())
                return fragment;
        }
        return null;
    }

    private void audioPlayPause() {


      Audio audioList =  Paper.book().read("audioList");

        if (player == null ){
            if (audioList!=null){
                Intent intent = new Intent(getContext(), AudioPlayerServices.class);

                Util.startForegroundService(getContext(), intent);
            }
        }else {
            if(player.getPlayWhenReady()){
                player.setPlayWhenReady(false);
            }else {
                player.setPlayWhenReady(true);
            }
        }


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), AudioPlayerServices.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("LifeCycle", "onResume");


        Intent intent = new Intent(getContext(), AudioPlayerServices.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        if(mBound){
            player = mService.getplayerInstance();
        }


        if (getSelectedItem(bottomNavigationView)!=0 && getSelectedItem(bottomNavigationView) == R.id.menuPlayer){
            bottomNavigationView.setSelectedItemId(R.id.menuHome);
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    if (prevFragment!=null){
                        if (activeFragment.getTag().equals(AUDIO_LIST_FRAGMENT)){

                                hideSowFragment(prevFragment, null);

                        }
                    }else {
                        Toast.makeText(getContext(), "prev null", Toast.LENGTH_SHORT).show();
                    }

                    return true;

                }

                return false;
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(serviceConnection);
        mBound= false;
        Log.d("LifeCycle", "onStop");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LifeCycle", "onDestroy");

    }


    private int getSelectedItem(BottomNavigationView bottomNavigationView) {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem.getItemId();
            }
        }
        return 0;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            AudioPlayerServices.LocalBinder binder = (AudioPlayerServices.LocalBinder) iBinder;
            mService = binder.getService();

            mBound = true;

            Log.d("yes", "connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;

            Log.d("yes", " not connected");

        }
    };

    private BroadcastReceiver playerStateChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playerStateListener();
            showHidePlayerView();
        }
    };


    //category item listener
    private BroadcastReceiver categoryitemClickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Fragment fragment = fragmentManager.findFragmentByTag(AUDIO_LIST_FRAGMENT);

            if (fragment!= null) {

                    if (PREVIOUS_CATEGORY != Common.currentCategoryId) {
                        FragmentTransaction ft = fragmentManager.beginTransaction();
                        ft.detach(audioListFragment);
                        ft.attach(audioListFragment);
                        ft.commitNow();
                    }
                    PREVIOUS_CATEGORY = Common.currentCategoryId;

                }


            hideSowFragment(audioListFragment, null);
        }
    };

    private void setPlayerPlayPause(String state){

        if (state.equals(Common.PLAYER_PLAYING)){
            playerPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp));

        }else if (state.equals(Common.PLAYER_PAUSE)){
            playerPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.ic_play_circle_outline_black_24dp));

        }else if (state.equals(Common.PLAYER_BUFFERING)){
            playerPlayPause.setImageDrawable(getContext().getDrawable(R.drawable.buffering));

        }

    }











    public void playerStateListener(){
        if (mBound){
            getPlayerInstence();

            if (player != null){
                Audio audio = Audio.class.cast(player.getCurrentTag());
                player.addListener(new Player.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                        Log.d("PlayBackState", "Timeline change" +"->Reason "+reason);
                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                        Log.d("PlayBackState", "Track change");



                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {
                        Log.d("PlayBackState", "Loading "+ isLoading);
                        setPlayerAudioInfo(player);
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

    private void setPlayerAudioInfo(SimpleExoPlayer player) {
        Audio audio = Audio.class.cast(player.getCurrentTag());
        if (audio!=null){
            palyerAudioName.setText(audio.getName());
            playerAudioArtist.setText(audio.getArtist());
        }
    }

    private void getPlayerInstence() {
        if(mBound){
            player = mService.getplayerInstance();
        }
    }


}
