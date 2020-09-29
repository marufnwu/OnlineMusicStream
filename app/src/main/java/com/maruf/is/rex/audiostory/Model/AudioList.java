package com.maruf.is.rex.audiostory.Model;


public class AudioList {

    public Audio audio;
    public boolean hasMore;

    public AudioList() {
    }

    public AudioList(Audio audio, boolean hasMore) {
        this.audio = audio;
        this.hasMore = hasMore;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setAudio(Audio audio) {
        this.audio = audio;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }
}
