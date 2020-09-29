package com.maruf.is.rex.audiostory.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Model.Banner;
import com.maruf.is.rex.audiostory.R;
import com.maruf.is.rex.audiostory.Retrofit.IAudioStoryAPI;
import com.maruf.is.rex.audiostory.Retrofit.RetrofitClient;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioRecentUpdateAdapter extends RecyclerView.Adapter<AudioRecentUpdateAdapter.MyViewHolder> {


    public Context context;
    public List<Audio> audioList;

    public AudioRecentUpdateAdapter(Context context, List<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;

    }

    @NonNull
    @Override
    public AudioRecentUpdateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_recent_update, null);

        return new MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final AudioRecentUpdateAdapter.MyViewHolder holder, final int position) {
        holder.txtAudioName.setText(audioList.get(position).name);
        holder.txtEpisodeDate.setText(audioList.get(position).episodeDate);
        holder.txtAudioDuration.setText(Common.getTimesInHMS((int) audioList.get(position).duration)+" sec");
        final List<Banner>[] bannerList = new List[]{new ArrayList<>()};
        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);

        Call<List<Banner>> callBanner = audioStoryAPI.getBannerByCat(audioList.get(position).category);
        callBanner.enqueue(new Callback<List<Banner>>() {
            @Override
            public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {
                bannerList[0] = response.body();
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));

                Glide
                        .with(context)
                        .load(bannerList[0].get(0).link)
                        .apply(requestOptions)
                        .into(holder.imgAudioBanner);


            }

            @Override
            public void onFailure(Call<List<Banner>> call, Throwable t) {
                Log.d("ONRESPONSE", t.getMessage());

            }
        });

        holder.parentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Audio> audioArrayList = new ArrayList<>();
                audioArrayList.add(audioList.get(position));

                Intent intent = new Intent(context, AudioPlayerServices.class);
                intent.putParcelableArrayListExtra("AudioList", audioArrayList);
                Util.startForegroundService(context, intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAudioBanner;
        TextView txtAudioName, txtEpisodeDate, txtAudioDuration, txtRadioName;
        LinearLayout parentLinearLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAudioName = itemView.findViewById(R.id.txtAudioName);
            txtEpisodeDate = itemView.findViewById(R.id.txtEpisodeDate);
            txtAudioDuration = itemView.findViewById(R.id.txtAudioDuration);
            txtRadioName = itemView.findViewById(R.id.txtRadioName);
            imgAudioBanner = itemView.findViewById(R.id.imgAudioBanner);
            parentLinearLayout = itemView.findViewById(R.id.parentLinearLayout);


        }
    }
}
