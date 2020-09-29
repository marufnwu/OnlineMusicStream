package com.maruf.is.rex.audiostory.Paging;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloadService;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.util.Util;
import com.maruf.is.rex.audiostory.Adapters.AudioRecentUpdateAdapter;
import com.maruf.is.rex.audiostory.Common.Common;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Model.Banner;
import com.maruf.is.rex.audiostory.Player.AudioDownloadServices;
import com.maruf.is.rex.audiostory.R;
import com.maruf.is.rex.audiostory.Retrofit.IAudioStoryAPI;
import com.maruf.is.rex.audiostory.Retrofit.RetrofitClient;
import com.maruf.is.rex.audiostory.Services.AudioPlayerServices;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemAdapter extends PagedListAdapter<Audio, ItemAdapter.ItemViewHolder> {

    private Context context;

    public ItemAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;

    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_audio_list, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Audio audio = getItem(position);

        if (audio != null){

            holder.txtAudioName.setText(audio.name);

            if (!audio.episodeDate.isEmpty()){
                holder.txtEpisodeDate.setText(audio.episodeDate);
            }else {
                holder.txtEpisodeDate.setText("N/A");

            }

            if (audio.duration >0){
                holder.txtAudioDuration.setText(Common.getTimesInHMS((int) audio.duration)+" sec");

            }else {
                holder.txtAudioDuration.setText("N/A");

            }

            if (Common.playingId > -1){
                if (audio.id == Common.playingId){
                    holder.imgPlaying.setVisibility(View.VISIBLE);
                }else {
                    holder.imgPlaying.setVisibility(View.GONE);
                }
            }

            if (!audio.bannerUrl.isEmpty()){
                Log.d("linkkk", "not null"+audio.category);


                Glide.with(context)
                        .load(audio.bannerUrl)
                        .placeholder(R.drawable.image_loading)
                        .apply(requestOptions)
                        .into(holder.imgAudioBanner);
            }else {
                final List<Banner>[] bannerList = new List[]{new ArrayList<>()};
                IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);
                Log.d("linkkk", ""+audio.category);

                Call<List<Banner>> callBanner = audioStoryAPI.getBannerByCat(audio.category);
                callBanner.enqueue(new Callback<List<Banner>>() {
                    @Override
                    public void onResponse(Call<List<Banner>> call, Response<List<Banner>> response) {


                        bannerList[0] = response.body();
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                        Log.d("linkkk", ""+bannerList[0].get(0).link);
                        Glide
                                .with(context)
                                .load(bannerList[0].get(0).link)
                                .placeholder(R.drawable.image_loading)
                                .apply(requestOptions)
                                .into(holder.imgAudioBanner);


                    }

                    @Override
                    public void onFailure(Call<List<Banner>> call, Throwable t) {
                        Log.d("ONRESPONSE", t.getMessage());

                    }
                });
            }

            String uri;


            if (!audio.getSecondaryUrl().isEmpty()){
                uri = audio.getPrimaryUrl();
            }else {
                uri = audio.getSecondaryUrl();
            }


            holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (uri!=null && !uri.isEmpty() ){

                        DownloadRequest downloadRequest = new DownloadRequest(
                                String.valueOf(audio.getId()),
                                DownloadRequest.TYPE_PROGRESSIVE,
                                Uri.parse(uri),
                                /* streamKeys= */ null,
                                /* customCacheKey= */ null,
                                null);


                        DownloadService.sendAddDownload(
                                context,
                                AudioDownloadServices.class,
                                downloadRequest,
                                /* foreground= */ false);
                    }
                }
            });





        }else {
            Toast.makeText(context, "Item is null", Toast.LENGTH_LONG).show();

        }


        holder.parentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<Audio> audioArrayList = new ArrayList<>();
                audioArrayList.add(audio);

                Intent intent = new Intent(context, AudioPlayerServices.class);
                intent.putParcelableArrayListExtra("AudioList", audioArrayList);
                Util.startForegroundService(context, intent);
            }
        });





    }

    private static DiffUtil.ItemCallback<Audio> DIFF_CALLBACK = new DiffUtil.ItemCallback<Audio>() {
        @Override
        public boolean areItemsTheSame(@NonNull Audio oldItem, @NonNull Audio newItem) {
            return oldItem.id == newItem.id;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Audio oldItem, @NonNull Audio newItem) {
            return oldItem.equals(newItem);
        }
    };

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView imgAudioBanner, imgDownload, imgMore, imgPlaying;
        TextView txtAudioName, txtEpisodeDate, txtAudioDuration, txtRadioName;
        LinearLayout parentLinearLayout;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAudioName = itemView.findViewById(R.id.txtAudioName);
            txtEpisodeDate = itemView.findViewById(R.id.txtEpisodeDate);
            txtAudioDuration = itemView.findViewById(R.id.txtAudioDuration);
            txtRadioName = itemView.findViewById(R.id.txtRadioName);
            imgAudioBanner = itemView.findViewById(R.id.imgAudioBanner);
            parentLinearLayout = itemView.findViewById(R.id.parentLinearLayout);
            imgDownload = itemView.findViewById(R.id.imgDownload);
            imgMore = itemView.findViewById(R.id.imgMore);
            imgPlaying = itemView.findViewById(R.id.imgPlaying);


        }
    }
}
