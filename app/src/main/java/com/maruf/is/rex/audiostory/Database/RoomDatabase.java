package com.maruf.is.rex.audiostory.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;

import com.maruf.is.rex.audiostory.Common.DateConverter;
import com.maruf.is.rex.audiostory.Dao.RecentPlayedAudioDao;
import com.maruf.is.rex.audiostory.Model.Audio;

@Database(entities = {Audio.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public  abstract class RoomDatabase extends androidx.room.RoomDatabase {
    public abstract RecentPlayedAudioDao recentAudioDao();

    public static androidx.room.RoomDatabase instance;

    public static RoomDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context, RoomDatabase.class, "AudioDB")
                    .allowMainThreadQueries()
                    .build();
        }

        return (RoomDatabase) instance;
    }
}
