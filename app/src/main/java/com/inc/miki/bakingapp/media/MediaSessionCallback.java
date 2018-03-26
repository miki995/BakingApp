package com.inc.miki.bakingapp.media;

import android.support.v4.media.session.MediaSessionCompat;

import com.google.android.exoplayer2.SimpleExoPlayer;


public class MediaSessionCallback extends MediaSessionCompat.Callback {

    private SimpleExoPlayer exoPlayer;

    public MediaSessionCallback(SimpleExoPlayer exoPlayer) {
        this.exoPlayer = exoPlayer;
    }

    @Override
    public void onPlay() {
        exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onPause() {
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    public void onSkipToPrevious() {
        exoPlayer.seekTo(0);
    }

}