package com.maruf.is.rex.audiostory.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.maruf.is.rex.audiostory.Model.Audio;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface RecentPlayedAudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAudio(Audio...audios);

    @Query("SELECT * FROM audio")
    Flowable<List<Audio>> getAllAudio();

    @Query("SELECT * FROM audio WHERE id=:audioId")
     List<Audio> getAudioById(int audioId);
}
