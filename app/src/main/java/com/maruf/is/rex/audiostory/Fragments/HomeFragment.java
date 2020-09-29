package com.maruf.is.rex.audiostory.Fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.MediaBrowserCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.maruf.is.rex.audiostory.Adapters.AudioRecentUpdateAdapter;
import com.maruf.is.rex.audiostory.Adapters.CategoryAdapter;
import com.maruf.is.rex.audiostory.Adapters.RecentPlayedAdapter;
import com.maruf.is.rex.audiostory.Adapters.ShimmerAdapter;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Common.SpaceItemDecoration;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Model.Categories;
import com.maruf.is.rex.audiostory.R;
import com.maruf.is.rex.audiostory.Retrofit.IAudioStoryAPI;
import com.maruf.is.rex.audiostory.Retrofit.RetrofitClient;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;
import com.maruf.is.rex.audiostory.Services.MySessionCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment{

    RecyclerView recyclerRecentAudio;
    RecyclerView recyclerRecentPlayedAudio;
    RecyclerView recyclerShimmerLayout;
    RecyclerView recyclerCetegory;
    ShimmerFrameLayout shimmerFrameLayout;
    private MediaBrowserCompat mediaBrowser;
    MediaSessionConnector.PlaybackPreparer playbackPreparer;

    private AudioPlayerServices mService;
    private boolean mBound = false;
    private SimpleExoPlayer player;
    private LocalBroadcastManager localBroadcastManager;
    private MySessionCallback mySessionCallback;
    private CompositeDisposable compositeDisposable;
    private View view;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d("HomeFragmentLifeCycle", "onCreateView");

        if (view != null) {
            Log.d("TAGGGG", "onCreateView: reusing view");
        }else {
            Log.d("TAGGGG", "onCreateView: not reusing view");

        }

        if (view==null){

           view =  inflater.inflate(R.layout.fragment_home, container, false);
        }

        recyclerRecentAudio = view.findViewById(R.id.recyclerRecentAudio);
        recyclerRecentAudio.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentAudio.addItemDecoration(new SpaceItemDecoration(2));
        recyclerRecentAudio.hasFixedSize();

        recyclerRecentPlayedAudio = view.findViewById(R.id.recyclerRecentPlayedAudio);
        recyclerRecentPlayedAudio.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentPlayedAudio.addItemDecoration(new SpaceItemDecoration(2));
        recyclerRecentPlayedAudio.hasFixedSize();

        recyclerShimmerLayout = view.findViewById(R.id.recyclerShimmerViewHolder);
        recyclerShimmerLayout.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerShimmerLayout.addItemDecoration(new SpaceItemDecoration(2));
        recyclerShimmerLayout.hasFixedSize();

        recyclerCetegory = view.findViewById(R.id.recyclerCetegory);
        recyclerCetegory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerCetegory.addItemDecoration(new SpaceItemDecoration(2));
        recyclerCetegory.hasFixedSize();

        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);

        mySessionCallback = new MySessionCallback();

        try {
            getRecentAudio();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getRecentPlayed();
        getAllCategories();


        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("HomeFragmentLifeCycle", "onCreate");

        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(playerStateChangeReciever,new IntentFilter("PLAYER_STATE_LISTENER") );

        compositeDisposable = new CompositeDisposable();

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("HomeFragmentLifeCycle", "onStart");

        shimmerFrameLayout.startShimmer();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("HomeFragmentLifeCycle", "onResume");

        Intent intent = new Intent(getContext(), AudioPlayerServices.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if(mBound){
            player = mService.getplayerInstance();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("HomeFragmentLifeCycle", "onStop");

        getActivity().unbindService(serviceConnection);
        mBound= false;
        shimmerFrameLayout.stopShimmer();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putAll(outState);

    }

    private void getRecentAudio() throws InterruptedException {

        recyclerRecentAudio.setVisibility(View.GONE);
        ShimmerAdapter adapter = new ShimmerAdapter(getContext(), Common.SHIMMER_FOR_LATEST_UPDATED);
        recyclerShimmerLayout.setAdapter(adapter);



        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);

        Call<List<Audio>> callRecentAudio = audioStoryAPI.getRecentAudio(1, 8);
        callRecentAudio.enqueue(new Callback<List<Audio>>() {
            @Override
            public void onResponse(Call<List<Audio>> call, Response<List<Audio>> response) {
                if (response.isSuccessful()){

                    recyclerShimmerLayout.setVisibility(View.GONE);
                    recyclerRecentAudio.setVisibility(View.VISIBLE);
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);

                    List<Audio> audioList = response.body();


                    AudioRecentUpdateAdapter adapter = new AudioRecentUpdateAdapter(getContext(), audioList);
                    recyclerRecentAudio.setAdapter(adapter);
                    Log.d("Result", String.valueOf(audioList));
                }
            }

            @Override
            public void onFailure(Call<List<Audio>> call, Throwable t) {
                Log.d("Result", t.getMessage());
            }
        });

    }



    private void getRecentPlayed() {
        compositeDisposable.add(Common.roomDatabase.recentAudioDao().getAllAudio()
        .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        .subscribe(new Consumer<List<Audio>>() {
            @Override
            public void accept(List<Audio> audio) throws Exception {


             if (!audio.isEmpty()){
                 Log.d("RecentPlayed", audio.get(0).getName());
             }else {
                 Log.d("RecentPlayed", "Empty");
             }

                RecentPlayedAdapter adapter = new RecentPlayedAdapter(getActivity(),audio);
                recyclerRecentPlayedAudio.setAdapter(adapter);

            }
        }));
    }



    private void getAllCategories() {
        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);
        Call<List<Categories>> getAllCategory = audioStoryAPI.getAllCategories();

        getAllCategory.enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {

                List<Categories> categories = response.body();

                CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
                recyclerCetegory.setAdapter(adapter);


            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                Log.d("", t.getMessage());
            }
        });



    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
       @Override
       public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
           AudioPlayerServices.LocalBinder binder = (AudioPlayerServices.LocalBinder) iBinder;
           mService = binder.getService();

           mBound = true;
       }

       @Override
       public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
       }
   };

    BroadcastReceiver playerStateChangeReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //int id = intent.getIntExtra("id",0);
            //Toast.makeText(context, String.valueOf(id), Toast.LENGTH_SHORT).show();
            if (mBound){
                player = mService.getplayerInstance();

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

                       }

                       @Override
                       public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                           Log.d("PlayBackState", "onPlayerStateChanged");

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
    };




}
