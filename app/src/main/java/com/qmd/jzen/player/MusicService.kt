package com.qmd.jzen.player

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector.MediaButtonEventHandler
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.utils.Toaster

/**
 * Create by OJun on 2021/5/8.
 *
 */

open class MusicService : MediaBrowserServiceCompat() {

    private lateinit var notificationManager: QMDPlayerNotificationManager
    private lateinit var mediaSource: MediaDataSource
    protected lateinit var playList: PlayList
    protected lateinit var mediaSession: MediaSessionCompat
    protected lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var mediaController: MediaControllerCompat

    private var isForegroundService = false

    private val uAmpAudioAttributes = AudioAttributes.Builder()
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    private val playerListener = PlayerEventListener()

    /**
     * Configure ExoPlayer to handle audio focus for us.
     * See [Player.AudioComponent.setAudioAttributes] for details.
     */
    private val exoPlayer: ExoPlayer by lazy {
        SimpleExoPlayer.Builder(this).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
    }

    private val dataSourceFactory: DefaultDataSourceFactory by lazy {
        DefaultDataSourceFactory(
                /* context= */ this,
                Util.getUserAgent(/* context= */ this, "QMD"), /* listener= */
                null
        )
    }

    override fun onCreate() {
        super.onCreate()

        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
                packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                    PendingIntent.getActivity(this,
                            0, sessionIntent, 0)
                }

        // Create a new MediaSession.
        mediaSession = MediaSessionCompat(this, "MusicService")
                .apply {
                    setSessionActivity(sessionActivityPendingIntent)
                    isActive = true
                }

        /**
         * In order for [MediaBrowserCompat.ConnectionCallback.onConnected] to be called,
         * a [MediaSessionCompat.Token] needs to be set on the [MediaBrowserServiceCompat].
         *
         * It is possible to wait to set the session token, if required for a specific use-case.
         * However, the token *must* be set by the time [MediaBrowserServiceCompat.onGetRoot]
         * returns, or the connection will fail silently. (The system will not even call
         * [MediaBrowserCompat.ConnectionCallback.onConnectionFailed].)
         */
        sessionToken = mediaSession.sessionToken

        /**
         * The notification manager will use our player and media session to decide when to post
         * notifications. When notifications are posted or removed our listener will be called, this
         * allows us to promote the service to foreground (required so that we're not killed if
         * the main UI is not visible).
         */
        notificationManager = QMDPlayerNotificationManager(
                this,
                mediaSession.sessionToken,
                PlayerNotificationListener()
        )

        mediaSource = MediaDataSource(dataSourceFactory)

        // ExoPlayer will manage the MediaSession for us.
        mediaSessionConnector = MediaSessionConnector(mediaSession).apply {
            setPlaybackPreparer(QMDPlaybackPreparer(exoPlayer, mediaSource))
            setQueueNavigator(QMDQueueNavigator(mediaSession))
            setMediaButtonEventHandler(PlayerMediaButtonEventHandler())
            setPlayer(exoPlayer)
        }

        mediaController = MediaControllerCompat(this, mediaSession)
        mediaController.registerCallback(MediaControllerCallback())

        playList = PlayList.instance

        notificationManager.showNotificationForPlayer(exoPlayer)
    }

    /**
     * This is the code that causes UAMP to stop playing when swiping the activity away from
     * recents. The choice to do this is app specific. Some apps stop playback, while others allow
     * playback to continue and allow users to stop it with the notification.
     */
    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        /**
         * By stopping playback, the player will transition to [Player.STATE_IDLE] triggering
         * [Player.EventListener.onPlayerStateChanged] to be called. This will cause the
         * notification to be hidden and trigger
         * [PlayerNotificationManager.NotificationListener.onNotificationCancelled] to be called.
         * The service will then remove itself as a foreground service, and will call
         * [stopSelf].
         */
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }
        // Free ExoPlayer resources.
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
            clientPackageName: String,
            clientUid: Int,
            rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("/", null)
    }

    override fun onLoadChildren(
            parentMediaId: String,
            result: Result<List<MediaItem>>
    ) {
        //val list: List<MediaItem> = ArrayList()
        result.sendResult(null)
    }

    private inner class QMDQueueNavigator(
            mediaSession: MediaSessionCompat
    ) : TimelineQueueNavigator(mediaSession) {

        private val window = Timeline.Window()
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            val mediaItem = player.currentTimeline.getWindow(windowIndex, window).mediaItem
            val description = mediaItem.playbackProperties?.tag as MediaDescriptionCompat
            return mediaSource.buildMediaMetadataWithDuration(description, player.duration)
        }

    }

    /**
     * Listen for notification events.
     */
    private inner class PlayerNotificationListener :
            PlayerNotificationManager.NotificationListener {
        override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
        ) {
            if (ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                        applicationContext,
                        Intent(applicationContext, this@MusicService.javaClass)
                )
                startForeground(notificationId, notification)
                isForegroundService = true
            }
        }

        override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    /**
     * Listen for events from ExoPlayer.
     */
    private inner class PlayerEventListener : Player.EventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(exoPlayer)
                    if (playbackState == Player.STATE_READY) {
                        if (!playWhenReady) {
                            // If playback is paused we remove the foreground state which allows the
                            // notification to be dismissed. An alternative would be to provide a
                            // "close" button in the notification which stops playback and clears
                            // the notification.
                            stopForeground(false)
                            isForegroundService = false
                        }
                    }

                }
                Player.STATE_ENDED -> {
                    if (playWhenReady) {
                        exoPlayer.seekTo(0)
                        Logger.e("自动下一曲")
                        playList.autoNext()
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }

        override fun onPlayerError(error: ExoPlaybackException) {
            var message = R.string.text_exception_error;
            when (error.type) {
                // If the data from MediaSource object could not be loaded the Exoplayer raises
                // a type_source error.
                // An error message is printed to UI via Toast message to inform the user.
                ExoPlaybackException.TYPE_SOURCE -> {
                    message = R.string.text_exception_error_media_not_found;
                    Logger.e(TAG, "TYPE_SOURCE: " + error.sourceException.message)
                }
                // If the error occurs in a render component, Exoplayer raises a type_remote error.
                ExoPlaybackException.TYPE_RENDERER -> {
                    Logger.e(TAG, "TYPE_RENDERER: " + error.rendererException.message)
                }
                // If occurs an unexpected RuntimeException Exoplayer raises a type_unexpected error.
                ExoPlaybackException.TYPE_UNEXPECTED -> {
                    Logger.e(TAG, "TYPE_UNEXPECTED: " + error.unexpectedException.message)
                }
                // If the error occurs in a remote component, Exoplayer raises a type_remote error.
                ExoPlaybackException.TYPE_REMOTE -> {
                    Logger.e(TAG, "TYPE_REMOTE: " + error.message)
                }
            }
            Toaster.out(message)
        }
    }

    private inner class PlayerMediaButtonEventHandler : MediaButtonEventHandler {
        override fun onMediaButtonEvent(player: Player, controlDispatcher: ControlDispatcher, mediaButtonEvent: Intent): Boolean {
            val keyEvent = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    ?: return false
            when (keyEvent.keyCode) {
                KeyEvent.KEYCODE_MEDIA_NEXT -> playList.next()
                KeyEvent.KEYCODE_MEDIA_PREVIOUS -> playList.previous(null)
                KeyEvent.KEYCODE_MEDIA_STOP -> PlayController.instance.stop()
                KeyEvent.KEYCODE_MEDIA_PLAY -> mediaController.getTransportControls().play()
                KeyEvent.KEYCODE_MEDIA_PAUSE -> mediaController.getTransportControls().pause()
            }
            return true
        }
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            super.onMetadataChanged(metadata)
            metadata.description.mediaId?.let { playList.setNowPlayingId(it) }
        }
    }
}

private const val TAG = "MusicService"
