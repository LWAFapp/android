package com.Zakovskiy.lwaf.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.concurrent.Callable;

public class AudioPlayer {

    private Context context;
    private Callable prepareEvent;
    private Callable endEvent;
    public SimpleExoPlayer mMediaPlayer;

    public AudioPlayer (Context context, Callable prepareEvent, Callable endEvent) {
        this.context = context;
        this.prepareEvent = prepareEvent;
        this.endEvent = endEvent;
    }

    public void stopSong() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    public void playSong(String url) throws Exception {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.release();
        }
        this.mMediaPlayer = new SimpleExoPlayer.Builder(context).build();
        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
        MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(Uri.parse(url)));
        mMediaPlayer.prepare(mediaSource);
        mMediaPlayer.setPlayWhenReady(true);
        mMediaPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // Проверяем, что трек закончился
                if (playbackState == Player.STATE_ENDED) {
                    try {
                        endEvent.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (Player.STATE_READY == playbackState) {
                    try {
                        prepareEvent.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
