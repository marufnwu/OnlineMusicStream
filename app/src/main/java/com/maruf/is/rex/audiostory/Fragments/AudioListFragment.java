package com.maruf.is.rex.audiostory.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.maruf.is.rex.audiostory.Adapters.ShimmerAdapter;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Common.SpaceItemDecoration;
import com.maruf.is.rex.audiostory.Model.Audio;

import com.maruf.is.rex.audiostory.Paging.ItemAdapter;
import com.maruf.is.rex.audiostory.Paging.ItemViewModel;
import com.maruf.is.rex.audiostory.R;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;
import com.maruf.is.rex.audiostory.Services.PlayerCallback;


public class AudioListFragment extends Fragment {
    private AudioPlayerServices mService;
    private boolean mBound = false;
    private int PREVIOUS_CAT;

    int cat;
    RecyclerView recyclerAudioList;
    private LocalBroadcastManager localBroadcastManager;
    private PlayerCallback playerCallback;
    boolean fragVisible=false;
    public SimpleExoPlayer player;
    private ItemAdapter adapter;
    private ItemViewModel itemViewModel;
    private RecyclerView recyclerShimmerLayout;
    private ShimmerFrameLayout shimmerFrameLayout;
    LinearLayoutManager manager;
    private Runnable runnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audiolist, container, false);

        recyclerAudioList = view.findViewById(R.id.recyclerAudioList);
        recyclerAudioList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerAudioList.setHasFixedSize(true);


        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        recyclerShimmerLayout = view.findViewById(R.id.recyclerShimmerViewHolder);
         manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerShimmerLayout.setLayoutManager(manager);

        recyclerShimmerLayout.hasFixedSize();





        return  view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playerCallback = new PlayerCallback(getContext());
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(playerStateChangeReciever,new IntentFilter("PLAYER_STATE_LISTENER") );
        cat = Common.currentCategoryId;
    }



    private void setRecyclerView(int cat) {


       
        itemViewModel = null;
         itemViewModel = ViewModelProviders.of(this,
                new ViewModelProvider.Factory() {
                    @NonNull
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                        return (T) new ItemViewModel(cat);
                    }
                }).get(ItemViewModel.class);

       adapter = new ItemAdapter(getActivity());



        /*itemViewModel.itemPagedList.observe(this, new Observer<PagedList<Audio>>() {
            @Override
            public void onChanged(PagedList<Audio> audio) {
                Log.d("baaal", "onChangedCalled");

                Log.d("baaal", "");

                adapter.submitList(audio);



            }
        });*/


        MediatorLiveData mediatorLiveData = new ItemViewModel(cat).getdata();
        mediatorLiveData.observe(this, new Observer<PagedList<Audio>>() {
            @Override
            public void onChanged(PagedList<Audio> audio) {


                adapter.submitList(audio);
            }
        });





        runnable = null;
         Handler handler = new Handler();
         runnable = new Runnable() {
            @Override
            public void run() {
                if (manager.findFirstVisibleItemPosition() ==0){
                    recyclerAudioList.setVisibility(View.VISIBLE);
                    recyclerShimmerLayout.setVisibility(View.GONE);
                    handler.removeCallbacks(runnable);
                }
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable, 5000);

        recyclerAudioList.setAdapter(adapter);





    }

    @Override
    public void onStart() {
        super.onStart();
       playerCallback.bindService();

    }

    @Override
    public void onStop() {
        super.onStop();
        playerCallback.unBindService();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d("AudioListLifecycle", "onAttach");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("AudioListLifecycle", "onDetach");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (hidden){
            Log.d("AudioListLifecycle", "OnHidden");
            fragVisible = false;

        }else {

            Log.d("AudioListLifecycle", "onVisible");
            fragVisible = true;


            if (Common.currentCategoryId >0)
                cat = Common.currentCategoryId;
                if (cat != PREVIOUS_CAT) {


                    ShimmerAdapter adapter = new ShimmerAdapter(getContext(), Common.SHIMMER_FOR_AUDIO_LIST);
                    recyclerShimmerLayout.setAdapter(adapter);
                    shimmerFrameLayout.startShimmer();

                    recyclerShimmerLayout.setVisibility(View.VISIBLE);
                    recyclerAudioList.setVisibility(View.GONE);


                    PREVIOUS_CAT = cat;
                    Log.d("cat", "current " + cat);
                    Log.d("cat", "PREVIOUS_CAT " + cat);

                    setRecyclerView(cat);
                }

            }


    }



    private BroadcastReceiver playerStateChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
           if (fragVisible){
               if (playerCallback.ismBound()){
                   player = playerCallback.getmService().getplayerInstance();
                   if (player!=null){
                       playerState(player);
                   }else {
                       Log.d("PlayerIms", " null");

                   }
               }
           }
        }
    };

    private void playerState(SimpleExoPlayer player) {
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
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    Audio audio = Audio.class.cast(player.getCurrentTag());
                    Common.playingId = audio.getId();
                    adapter.notifyDataSetChanged();

                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)

                    if(playbackState==Player.STATE_BUFFERING){

                    }else if (playbackState == Player.STATE_ENDED){
                        Common.playingId = -1;
                        adapter.notifyDataSetChanged();

                    }
                } else {
                    // player paused in any state
                    Common.playingId = -1;
                    adapter.notifyDataSetChanged();

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
    }


}
