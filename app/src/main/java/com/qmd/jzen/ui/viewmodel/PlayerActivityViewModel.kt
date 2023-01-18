package com.qmd.jzen.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.qmd.jzen.R
import com.qmd.jzen.player.*

class PlayerActivityViewModel(musicServiceConnection: MusicServiceConnection) :
    ViewModel() {
    private val handler = Handler(Looper.getMainLooper())
    private var updatePosition = true
    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE

    var mediaPosition = MutableLiveData(0L)
    var playingMetadata = MutableLiveData<NowPlayingMetadata>()
    var buttonRes = MutableLiveData<Int>()

//    init {
//        musicServiceConnection.subscribe("歌单", object : MediaBrowserCompat.SubscriptionCallback() {
//            override fun onChildrenLoaded(
//                parentId: String,
//                children: List<MediaBrowserCompat.MediaItem>
//            ) {
//                super.onChildrenLoaded(parentId, children)
//                Log.d("M", parentId)
//            }
//        })
//    }

    /**
     * When the session's [MediaMetadataCompat] changes, the [mediaItems] need to be updated
     * as it means the currently active item has changed. As a result, the new, and potentially
     * old item (if there was one), both need to have their [MediaItemData.playbackRes]
     * changed. (i.e.: play/pause button or blank)
     */
    private val mediaMetadataObserver = Observer<MediaMetadataCompat> {
        updateState(playbackState, it)
    }

    /**
     * When the session's [PlaybackStateCompat] changes, the [mediaItems] need to be updated
     * so the correct [MediaItemData.playbackRes] is displayed on the active item.
     * (i.e.: play/pause button or blank)
     */
    private val playbackStateObserver = Observer<PlaybackStateCompat> {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = musicServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, metadata)
    }

    /**
     * Because there's a complex dance between this [ViewModel] and the [MusicServiceConnection]
     * (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using
     * [Transformations] doesn't quite work.
     *
     * Specifically there's three things that are watched that will cause the single piece of
     * [LiveData] exposed from this class to be updated.
     *
     * [MusicServiceConnection.playbackState] changes state based on the playback state of
     * the player, which can change the [MediaItemData.playbackRes]s in the list.
     *
     * [MusicServiceConnection.nowPlaying] changes based on the item that's being played,
     * which can also change the [MediaItemData.playbackRes]s in the list.
     */
    private val musicServiceConnection = musicServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        // Only update media item once we have duration available
        if (mediaMetadata.duration != 0L && mediaMetadata.id != null) {
            val nowPlayingMetadata = NowPlayingMetadata(
                mediaMetadata.id!!,
                mediaMetadata.mediaUri,
                mediaMetadata.title?.trim(),
                mediaMetadata.displaySubtitle?.trim(),
                mediaMetadata.displayIconUri,
                mediaMetadata.duration
            )
            playingMetadata.postValue(nowPlayingMetadata)
        }

        // Update the media button resource ID
        buttonRes.postValue(
            when (playbackState.isPlaying) {
                true -> R.drawable.ic_pause_black_24dp
                else -> R.drawable.ic_play_arrow_black_24dp
            }
        )

    }

    // 进度位置检查更新
    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.currentPlayBackPosition
        if (mediaPosition.value != currPosition)
            mediaPosition.postValue(currPosition)
        if (updatePosition)
            checkPlaybackPosition()
    }, 100L)


    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)
        updatePosition = false
    }

    class Factory(private val msc: MusicServiceConnection) : NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlayerActivityViewModel(msc) as T
        }
    }


}