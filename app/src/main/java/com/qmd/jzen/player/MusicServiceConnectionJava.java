package com.qmd.jzen.player;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.MutableLiveData;

import com.orhanobut.logger.Logger;

public class MusicServiceConnectionJava {
    Context mContext;
    public MutableLiveData<PlaybackStateCompat> playbackState = new MutableLiveData<>();
    public MutableLiveData<MediaMetadataCompat> nowPlaying = new MutableLiveData<>();
    public MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    MediaControllerCompat mediaController;
    MediaBrowserCompat mediaBrowser;
    MediaBrowserConnectionCallback mediaBrowserConnectionCallback;

    public PlaybackStateCompat EMPTY_PLAYBACK_STATE;
    public MediaMetadataCompat NOTHING_PLAYING;

    public MusicServiceConnectionJava(Context context, ComponentName serviceComponent) {
        mContext = context;
        EMPTY_PLAYBACK_STATE = new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, 0, 0)
                .build();
        NOTHING_PLAYING = new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, "")
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, 0)
                .build();

        playbackState.postValue(EMPTY_PLAYBACK_STATE);
        nowPlaying.postValue(NOTHING_PLAYING);

        mediaBrowserConnectionCallback = new MediaBrowserConnectionCallback(mContext);
        mediaBrowser = new MediaBrowserCompat(mContext, serviceComponent, mediaBrowserConnectionCallback, null);
        mediaBrowser.connect();

    }


    static MusicServiceConnectionJava instance = null;

    public static MusicServiceConnectionJava getInstance(Context context, ComponentName serviceComponent) {
        if (instance == null) {
            synchronized (instance) {
            instance = new MusicServiceConnectionJava(context, serviceComponent);
            }
        }
        return instance;
    }

    public void subscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.subscribe(parentId, callback);
    }

    public void unsubscribe(String parentId, MediaBrowserCompat.SubscriptionCallback callback) {
        mediaBrowser.unsubscribe(parentId, callback);
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return mediaController.getTransportControls();
    }

    public class MediaBrowserConnectionCallback extends MediaBrowserCompat.ConnectionCallback {
        Context context;

        MediaBrowserConnectionCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onConnected() {
            Logger.d("M", "Connected!");
            try {
                mediaController = new MediaControllerCompat(context, mediaBrowser.getSessionToken());
                mediaController.registerCallback(new MediaControllerCallback());
                isConnected.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionSuspended() {
            isConnected.postValue(false);
        }

        @Override
        public void onConnectionFailed() {
            isConnected.postValue(false);
        }
    }

    private class MediaControllerCallback extends MediaControllerCompat.Callback {
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            if (state != null) {
                playbackState.postValue(state);
            } else {
                playbackState.postValue(EMPTY_PLAYBACK_STATE);
            }
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata != null) {
                nowPlaying.postValue(metadata);
            } else {
                nowPlaying.postValue(NOTHING_PLAYING);
            }
        }

        @Override
        public void onSessionDestroyed() {
            mediaBrowserConnectionCallback.onConnectionSuspended();
        }
    }

    public boolean isPlaying() {
        if (playbackState.getValue() != null) {
            int state = playbackState.getValue().getState();
            return state == PlaybackStateCompat.STATE_PLAYING || state == PlaybackStateCompat.STATE_BUFFERING;
        }
        return false;
    }

    public boolean hasData() {
        if (nowPlaying.getValue() != null) {
            MediaMetadataCompat data = nowPlaying.getValue();
            return data.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID) != null;
        }
        return false;
    }
}

