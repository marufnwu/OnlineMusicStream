package com.maruf.is.rex.audiostory.Retrofit;

import com.maruf.is.rex.audiostory.Model.Audio;
import com.maruf.is.rex.audiostory.Model.Banner;
import com.maruf.is.rex.audiostory.Model.Categories;
import com.maruf.is.rex.audiostory.Paging.AudioPaging;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IAudioStoryAPI {
    @GET("getRecentAudio.php")
    Call<List<Audio>> getRecentAudio(
            @Query("page") int page,
            @Query("items") int items
    );

    @GET("getAudioByCategory.php")
    Call<AudioPaging> getAudio(
            @Query("cat") int cat,
            @Query("page") int page,
            @Query("items") int items
    );

    @GET("getCategories.php")
    Call<List<Categories>> getAllCategories();


    

    @GET("getBanner.php")
    Call<List<Banner>> getBannerByCat(@Query("cat") int cat);

}
