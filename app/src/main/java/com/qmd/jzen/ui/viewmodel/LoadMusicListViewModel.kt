package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.*
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.api.repository.QQMusicRepository
import com.qmd.jzen.api.response.GetDissResponse
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.database.entity.SongListInfo
import com.qmd.jzen.database.repository.MusicRepository
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.utils.Toaster

/**
 * Create by OJun on 2022/1/29.
 *
 */
class LoadMusicListViewModel : ViewModel() {
    val musicList: MutableLiveData<ArrayList<MusicInfo>?> = MutableLiveData()
    private val qqMusicApi = QQMusicRepository(ApiSource.getQMusicApi())

    val repository: MusicRepository = QMDApplication.musicRepository
    private var databaseMusicList: LiveData<List<Music>>? = null

    var isFavorite = false

    private val observer = Observer<List<Music>> {
        val tmpMusicInfoList = arrayListOf<MusicInfo>()
        it.forEach { music ->
            tmpMusicInfoList.add(music.toMusicInfo())
        }
        musicList.postValue(tmpMusicInfoList)
    }

    suspend fun delete(musicList: ArrayList<MusicInfo>) {
        repository.delete(musicList)
    }

    suspend fun insert(musicList: ArrayList<MusicInfo>) {
        repository.insert(musicList)
    }

    // TODO("这里有更好的实现，利用Repository统一获取歌单或者数据库的数据")
    fun getMusicListFromDatabase() {
        isFavorite = true
        musicList.value = null
        databaseMusicList = repository.allMusic.asLiveData()
        databaseMusicList?.observeForever(observer)
    }

    fun getMusicListFromSongList(songList: SongListInfo) {
        isFavorite = false
        musicList.value = null
        databaseMusicList?.removeObserver(observer)

        viewModelScope.launchWebApi<GetDissResponse> {
            method {
                qqMusicApi.getDissMusic(songList.sid)
            }

            success {
                musicList.postValue(it.moduleResponse.data.songList)
            }

            fail {
                Toaster.out("获取歌曲失败:$it")
            }

            exception {
                Toaster.out("获取歌曲异常:$it")
            }
        }
    }
}