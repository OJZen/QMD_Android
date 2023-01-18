package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.api.repository.QQMusicRepository
import com.qmd.jzen.api.response.GetDissResponse
import com.qmd.jzen.entity.SongList
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.utils.Toaster

/**
 * Create by OJun on 2022/1/26.
 *
 */
class MusicListFragmentViewModel : ViewModel() {

    val musicList: MutableLiveData<ArrayList<MusicInfo>> = MutableLiveData()
    private val qqMusicApi = QQMusicRepository(ApiSource.getQMusicApi())

    fun getMusicList(songList: SongList) {
        musicList.value?.let {
            if (it.isNotEmpty()) {
                return
            }
        }

        viewModelScope.launchWebApi<GetDissResponse> {
            method {
                qqMusicApi.getDissMusic(songList.id)
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