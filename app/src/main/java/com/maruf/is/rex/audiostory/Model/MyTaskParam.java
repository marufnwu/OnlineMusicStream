package com.maruf.is.rex.audiostory.Model;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;

import java.util.ArrayList;
import java.util.List;

public class MyTaskParam {
    public Audio audio ;
    public int currentWindowIndex;
    public long currentPosition;
    public long currentDuration;

    public MyTaskParam(Audio audio, int currentWindowIndex, long currentPosition, long currentDuration) {
        this.audio = audio;
        this.currentWindowIndex = currentWindowIndex;
        this.currentPosition = currentPosition;
        this.currentDuration = currentDuration;
    }
}
