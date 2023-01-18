package com.qmd.jzen.player;

import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector;
import com.google.android.exoplayer2.source.MediaSource;
import com.qmd.jzen.entity.MusicEntity;

public class QMDPlaybackPreparer implements MediaSessionConnector.PlaybackPreparer {

    private ExoPlayer exoPlayer;
    private MediaDataSource mediaDataSource;

    public QMDPlaybackPreparer(ExoPlayer player, MediaDataSource source) {
        exoPlayer = player;
        mediaDataSource = source;
    }

    @Override
    public long getSupportedPrepareActions() {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                PlaybackStateCompat.ACTION_PREPARE_FROM_URI |
                PlaybackStateCompat.ACTION_PLAY_FROM_URI;
    }

    @Override
    public void onPrepare(boolean playWhenReady) {
    }

    @Override
    public void onPrepareFromMediaId(String musicId, boolean playWhenReady, Bundle extras) {
        MusicEntity music = (MusicEntity) extras.getSerializable("music");
        Uri uri = Uri.parse(music.getUrl());
        MediaDescriptionCompat description = mediaDataSource.buildMediaMetadata(music);
        MediaSource mediaSource = mediaDataSource.buildMediaSource(description, uri);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(mediaSource);

        /*
        int index = 0;
        // 计算当前播放的音乐在列表的位置（序号）
        List<MediaDescriptionCompat> musicList = mediaDataSource.getMusicList();
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getMediaId().equals(mediaId)) {
                index = i;
            }
        }
        */

        //exoPlayer.prepare(concatenatingMediaSource);
        //exoPlayer.seekTo(index, 0);
        //exoPlayer.setPlayWhenReady(true);
    }

    @Override
    public void onPrepareFromSearch(String query, boolean playWhenReady, Bundle extras) {

    }

    @Override
    public void onPrepareFromUri(Uri uri, boolean playWhenReady, Bundle extras) {
        MusicEntity music = (MusicEntity) extras.getSerializable("music");
        MediaDescriptionCompat description = mediaDataSource.buildMediaMetadata(music);
        MediaSource mediaSource = mediaDataSource.buildMediaSource(description, uri);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.setMediaSource(mediaSource);
        exoPlayer.prepare();
        //exoPlayer.seekTo(0);
    }

    @Override
    public boolean onCommand(Player player, ControlDispatcher controlDispatcher, String command, @Nullable Bundle extras, @Nullable ResultReceiver cb) {
        return false;
    }

}
