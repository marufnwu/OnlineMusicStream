package com.maruf.is.rex.audiostory.Paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.maruf.is.rex.audiostory.Adapters.AudioRecentUpdateAdapter;
import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Retrofit.IAudioStoryAPI;
import com.maruf.is.rex.audiostory.Retrofit.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemDataSource extends PageKeyedDataSource<Integer, Audio> {

    public static final int PAGE_SIZE = 3;
    public static final int FIRST_PAGE = 1;
    public int cat;

    public ItemDataSource(int cat) {
        this.cat = cat;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Audio> callback) {

        Log.d("ssssss", ""+cat);
        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);
        audioStoryAPI.getAudio(cat, FIRST_PAGE, PAGE_SIZE)
                .enqueue(new Callback<AudioPaging>() {
                    @Override
                    public void onResponse(Call<AudioPaging> call, Response<AudioPaging> response) {
                        if (response.body() != null){
                            callback.onResult(response.body().audio, null, FIRST_PAGE+1);
                        }
                    }

                    @Override
                    public void onFailure(Call<AudioPaging> call, Throwable t) {
                        Log.d("DataSourceError", t.getMessage());
                    }
                });




    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Audio> callback) {
        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);
        audioStoryAPI.getAudio(cat, params.key, PAGE_SIZE)
                .enqueue(new Callback<AudioPaging>() {
                    @Override
                    public void onResponse(Call<AudioPaging> call, Response<AudioPaging> response) {
                        if (response.body() != null){
                            Integer key = (params.key > 1) ? params.key - 1 : null;
                            callback.onResult(response.body().audio, key);
                        }
                    }

                    @Override
                    public void onFailure(Call<AudioPaging> call, Throwable t) {

                    }
                });

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Audio> callback) {
        IAudioStoryAPI audioStoryAPI = RetrofitClient.getRetrofit().create(IAudioStoryAPI.class);
        audioStoryAPI.getAudio(cat, params.key, PAGE_SIZE)
                .enqueue(new Callback<AudioPaging>() {
                    @Override
                    public void onResponse(Call<AudioPaging> call, Response<AudioPaging> response) {
                        if (response.body() != null){
                            Integer key = response.body().hasMore ? params.key + 1 : null;
                            callback.onResult(response.body().audio, key);
                        }
                    }

                    @Override
                    public void onFailure(Call<AudioPaging> call, Throwable t) {

                    }
                });
    }
}
