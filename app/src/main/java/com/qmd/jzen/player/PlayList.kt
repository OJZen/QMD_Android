package com.qmd.jzen.player

import androidx.lifecycle.MutableLiveData
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.player.PlayController.OnUrlLoadListener

class PlayList internal constructor() {
    // 正在播放的音乐ID
    val nowPlayingId = MutableLiveData<String>()

    /**
     * 获取当前播放id所在的位置序号
     *
     * @return
     */
    var nowPlayingIdPosition = 0

    // 用于正在播放的列表
    var musicList: ArrayList<MusicEntity> = ArrayList()

    // 随机播放的音乐列表
    private var shuffleList: MutableList<MusicEntity> = ArrayList()

    // 当前播放模式
    var playMode = PlayMode.ListLoop

    enum class PlayMode {
        SingleLoop, ListLoop, Random
    }

    // 确认将当前音乐列表替换为正在播放的列表
    fun confirm(newMusicList: ArrayList<MusicInfo>) {
        if (musicList == newMusicList) {
            return
        }

        val mList = ArrayList<MusicEntity>()
        newMusicList.forEach {
            mList.add(it.toMusicEntity())
        }
        musicList = mList
        shuffle()

    }

    // 打乱随机播放的列表
    private fun shuffle() {
        shuffleList.clear()
        shuffleList.addAll(musicList)
        shuffleList.shuffle()
    }

    fun setNowPlayingId(id: String) {
        nowPlayingId.postValue(id)
    }

    fun changePlayMode() {
        if (playMode == PlayMode.SingleLoop) {
            playMode = PlayMode.ListLoop
        } else if (playMode == PlayMode.ListLoop) {
            playMode = PlayMode.Random
        } else {
            playMode = PlayMode.SingleLoop
        }
        PlayController.instance.setRepeatMode(playMode)
    }

    /**
     * 获取正在播放的列表
     *
     * @return 正在播放的音乐列表
     */
    val playingList: List<MusicEntity?>
        get() = if (playMode == PlayMode.Random) {
            shuffleList
        } else musicList

    operator fun hasNext(): Boolean {
        if (playingList.isEmpty()) {
            return false
        }
        val pos = nowPlayingIdPosition
        // 判断这个id有没有下一首
        return pos < playingList.size - 1
    }

    fun hasPrevious(): Boolean {
        return playingList.isNotEmpty()
    }// 返回第一个

    // 判断这个id是不是最后一个
    val next: MusicEntity?
        get() {
            if (playingList.isEmpty()) {
                return null
            }
            val pos = nowPlayingIdPosition
            // 判断这个id是不是最后一个
            return if (pos >= playingList.size - 1) {
                // 返回第一个
                playingList[0]
            } else playingList[pos + 1]
        }// 返回最后一个

    // 判断这个id是不是第一个
    val previous: MusicEntity?
        get() {
            if (playingList.size == 0) {
                return null
            }
            val pos = nowPlayingIdPosition

            // 判断这个id是不是第一个
            return if (pos <= 0) {
                // 返回最后一个
                playingList[playingList.size - 1]
            } else playingList[pos - 1]
        }

    fun setMusicUrl(music: MusicEntity) {
        for (i in playingList.indices) {
            if (playingList[i]!!.musicId == music.musicId) {
                playingList[i]!!.url = music.url
            }
        }
    }

    var skipTimes = 0

    @JvmOverloads
    fun next(callback: LoadMusicCallback? = null) {
        playMusic(next, object : LoadMusicCallback {
            override fun onLoadDone(music: MusicEntity?) {
                callback?.onLoadDone(music)
                if (music != null) {
                    // 如果为空，自动跳过这一首。但自动跳过不会连续超过十次
                    if (music.url.isEmpty() && skipTimes < 10) {
                        skipTimes++
                        if (callback != null) {
                            next(callback)
                        } else {
                            next()
                        }
                    } else {
                        skipTimes = 0
                    }
                }
            }
        })
    }

    /**
     * 自动下一首
     */
    fun autoNext() {
        if (playMode == PlayMode.SingleLoop) {
            return
        }
        next()
    }

    fun previous(callback: LoadMusicCallback?) {
        playMusic(previous, callback)
    }

    private fun playMusic(music: MusicEntity?, callback: LoadMusicCallback?) {
        PlayController.instance.play(music, object : OnUrlLoadListener {
            override fun onDone(music: MusicEntity?) {
                callback?.onLoadDone(music)
                if (music != null) {
                    // 在播放列表里将当前音乐加上url
                    setMusicUrl(music)
                    setNowPlayingId(music.musicId)
                }
            }
        })
    }

    interface LoadMusicCallback {
        fun onLoadDone(music: MusicEntity?)
    }

    companion object {
        val instance: PlayList by lazy {
            PlayList()
        }
    }

    init {
        // 当前播放的id，设置id所在的序号
        nowPlayingId.observeForever { id: String ->
            for (i in playingList.indices) {
                // 找到了当前的id
                if (playingList[i]!!.musicId == id) {
                    nowPlayingIdPosition = i
                }
            }
        }
    }
}