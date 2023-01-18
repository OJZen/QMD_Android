package com.qmd.jzen.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.qmd.jzen.entity.MusicEntity;
import com.qmd.jzen.musicOperator.MusicImage;

import java.util.ArrayList;
import java.util.List;

public class MediaDataSource {
    ConcatenatingMediaSource mediaSource;
    DataSource.Factory dataSourceFactory;

    private List<MediaDescriptionCompat> musicList = new ArrayList<>();

    public MediaDataSource(DataSource.Factory df) {
        dataSourceFactory = df;
    }

    public List<MediaDescriptionCompat> load() {
        musicList.clear();
        return musicList;
    }


    public MediaDescriptionCompat buildMediaMetadata(MusicEntity music) {
        return getBuilder(music).build();
    }

    /**
     * 利用QMusic来构建媒体描述
     *
     * @param music
     * @return
     */
    MediaDescriptionCompat.Builder getBuilder(MusicEntity music) {
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        builder.setMediaId(music.getMusicId());
        builder.setMediaUri(Uri.parse(music.getUrl()));
        builder.setTitle(music.getTitle());
        builder.setSubtitle(music.getSinger());
        builder.setIconUri(Uri.parse(new MusicImage(music.getAlbumId(), 300).getImgUrl()));
        Bundle bundle = new Bundle();
        bundle.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, music.getSinger());
        builder.setExtras(bundle);
        return builder;
    }

    /**
     * 利用媒体描述来获取builder，用于重新构建媒体描述
     *
     * @param description
     * @return
     */
    MediaDescriptionCompat.Builder getBuilder(MediaDescriptionCompat description) {
        MediaDescriptionCompat.Builder builder = new MediaDescriptionCompat.Builder();
        builder.setMediaId(description.getMediaId());
        builder.setMediaUri(description.getMediaUri());
        builder.setIconUri(description.getIconUri());
        builder.setTitle(description.getTitle());
        builder.setSubtitle(description.getSubtitle());
        builder.setExtras(description.getExtras());
        return builder;
    }

    /**
     * 给描述添加媒体长度
     *
     * @param mediaDescription 描述
     * @param duration         长度
     * @return 新的描述
     */
    public MediaDescriptionCompat buildMediaMetadataWithDuration(MediaDescriptionCompat mediaDescription, long duration) {
        MediaDescriptionCompat.Builder builder = getBuilder(mediaDescription);
        Bundle bundle = mediaDescription.getExtras();
        bundle.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);
        builder.setExtras(bundle);
        return builder.build();
    }

    public List<MediaDescriptionCompat> getMusicList() {
        return musicList;
    }

    // 建立媒体源
    MediaSource buildMediaSource(MediaDescriptionCompat description, Uri uri) {
        MediaItem.Builder builder = new MediaItem.Builder();
        builder.setUri(uri).setTag(description);
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(builder.build());
    }

    // 建立播放列表的媒体源，初始只有一首音乐//已弃用使用这种播放列表形式
    ConcatenatingMediaSource buildPlayList(MediaDescriptionCompat description) {
        if (mediaSource == null) {
            mediaSource = new ConcatenatingMediaSource();
        } else {
            mediaSource.clear();
        }
        Uri uri = description.getMediaUri();
        mediaSource.addMediaSource(new ProgressiveMediaSource.Factory(dataSourceFactory).setTag(description).createMediaSource(uri));
        return mediaSource;
    }
}
