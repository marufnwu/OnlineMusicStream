package com.maruf.is.rex.audiostory.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Date;

@Entity(tableName = "Audio")
public class Audio implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int category;
    public String album;
    public String artist;
    public String primaryUrl;
    public String secondaryUrl;
    public String bannerUrl;

    public Date uplaodDate;

    public String episodeDate;
    public long duration;
    public String tag;

    public long currSeekPosition;
    public long totalDuration;

    public Audio() {
    }

    protected Audio(Parcel in) {
        id = in.readInt();
        name = in.readString();
        category = in.readInt();
        album = in.readString();
        artist = in.readString();
        primaryUrl = in.readString();
        secondaryUrl = in.readString();
        bannerUrl = in.readString();
        episodeDate = in.readString();
        duration = in.readLong();
        tag = in.readString();
        currSeekPosition = in.readLong();
        totalDuration = in.readLong();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPrimaryUrl() {
        return primaryUrl;
    }

    public void setPrimaryUrl(String primaryUrl) {
        this.primaryUrl = primaryUrl;
    }

    public String getSecondaryUrl() {
        return secondaryUrl;
    }

    public void setSecondaryUrl(String secondaryUrl) {
        this.secondaryUrl = secondaryUrl;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public Date getUplaodDate() {
        return uplaodDate;
    }

    public void setUplaodDate(Date uplaodDate) {
        this.uplaodDate = uplaodDate;
    }

    public String getEpisodeDate() {
        return episodeDate;
    }

    public void setEpisodeDate(String episodeDate) {
        this.episodeDate = episodeDate;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getCurrSeekPosition() {
        return currSeekPosition;
    }

    public void setCurrSeekPosition(long currSeekPosition) {
        this.currSeekPosition = currSeekPosition;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeInt(category);
        parcel.writeString(album);
        parcel.writeString(artist);
        parcel.writeString(primaryUrl);
        parcel.writeString(secondaryUrl);
        parcel.writeString(bannerUrl);
        parcel.writeString(episodeDate);
        parcel.writeLong(duration);
        parcel.writeString(tag);
        parcel.writeLong(currSeekPosition);
        parcel.writeLong(totalDuration);
    }
}
