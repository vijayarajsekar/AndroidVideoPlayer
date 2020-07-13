package com.demo.videoplayer.listeners;

import android.support.v4.media.session.PlaybackStateCompat;

public interface VideoPlaybackStateListener {

    void updatePbState(PlaybackStateCompat state);

}
