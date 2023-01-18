package com.qmd.jzen.player

import android.content.ComponentName
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.MutableLiveData
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.entity.PlayRecord
import com.qmd.jzen.musicOperator.MusicLyric
import com.qmd.jzen.musicOperator.MusicUrl
import com.qmd.jzen.player.MusicServiceConnection.Companion.getInstance
import com.qmd.jzen.player.PlayList.PlayMode
import com.qmd.jzen.utils.Config.playQuality
import com.qmd.jzen.utils.SystemInfoUtil
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.*

class PlayController {

    private val qmdApi = QMDRepository(ApiSource.getServerApi())
    private var urlLoadListener: OnUrlLoadListener? = null
    private var getMusicUrlJob: Job? = null
    private var isUrlGot = MutableLiveData(true)
    var nowPlayingMusic: MusicEntity? = null

    fun urlGotStatus(): MutableLiveData<Boolean> {
        return isUrlGot
    }

    // 设置循环模式
    fun setRepeatMode(playMode: PlayMode) {
        if (playMode == PlayMode.SingleLoop) {
            transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
        } else {
            transportControls.setRepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
        }
    }

    fun play(music: MusicEntity?) {
        if (music == null) {
            // 恢复播放
            transportControls.play()
            return
        }

        // 暂停播放
        transportControls.pause()

        if (getMusicUrlJob != null) {
            // 如果还在运行的话，就中断掉
            if (getMusicUrlJob!!.isActive) {
                getMusicUrlJob!!.cancel()
            }
        }

        getMusicUrlJob = CoroutineScope(Dispatchers.IO).launch {
            isUrlGot.postValue(false)

            if (music.url.isEmpty()) {
                val musicUrl = MusicUrl(music)
                val url = musicUrl.getURL(playQuality)
                if (url.isNotEmpty()) {
                    music.url = url
                }
            }

            // 缓存歌词
            val lyricManager = MusicLyric(music)
            if (lyricManager.cacheLyric.isEmpty()) {
                lyricManager.getLyric() // 获取歌词
            }

            withContext(Dispatchers.Main) {
                if (music.url.isNotEmpty()) {
                    val uri = Uri.parse(music.url)
                    val bundle = Bundle()
                    bundle.putSerializable("music", music)
                    transportControls.playFromUri(uri, bundle)
                    // 提交播放数据
                    addRecord(music)
                } else {
                    Toaster.out("${music.title} 获取资源失败 ")
                }
                nowPlayingMusic = music
                if (urlLoadListener != null) {
                    urlLoadListener!!.onDone(music)
                }
                isUrlGot.postValue(true)
            }
        }
    }

    fun play(music: MusicEntity?, listener: OnUrlLoadListener?) {
        urlLoadListener = listener
        play(music)
    }

    fun stop() {
        transportControls.stop()
    }

    private suspend fun addRecord(music: MusicEntity) {
        withContext(Dispatchers.IO) {
            try {
                qmdApi.addPlayRecord(PlayRecord(SystemInfoUtil.UID, music.musicId, music.title, music.singer, playQuality.toString()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    interface OnUrlLoadListener {
        fun onDone(music: MusicEntity?)
    }

    companion object {
        private val transportControls: MediaControllerCompat.TransportControls = getInstance(
            QMDApplication.context!!,
            ComponentName(QMDApplication.context!!, MusicService::class.java)
        ).transportControls

        val instance: PlayController by lazy {
            PlayController()
        }
    }

}