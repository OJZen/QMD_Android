package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.api.repository.QQMusicRepository
import com.qmd.jzen.api.request.SearchReqBody
import com.qmd.jzen.api.response.MusicSearchResponse
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.database.entity.SearchHistory
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.utils.SystemInfoUtil
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.internal.notify

/**
 * Create by OJun on 2022/1/18.
 *
 */
class SearchFragmentViewModel : ViewModel() {
    private val qmdApi = QMDRepository(ApiSource.getServerApi())
    private val qqMusicApi = QQMusicRepository(ApiSource.getQMusicApi())

    private val searchRepository = QMDApplication.searchRepository

    // 搜索历史
    val searchHistory = searchRepository.allHistory

    private var searchJob: Job? = null

    val musicList: MutableLiveData<ArrayList<MusicInfo>> = MutableLiveData()
    val errorInfo: MutableLiveData<String> = MutableLiveData()

    var isShowHistory: Boolean = true

    fun search(keyWord: String) {
        searchJob?.let {
            if (!it.isCancelled) {
                it.cancel()
            }
        }

        searchJob = viewModelScope.launchWebApi<MusicSearchResponse> {
            method {
                qqMusicApi.searchMusic(keyWord)
            }

            success {
                if (it.code == 0) {
                    musicList.postValue(it.moduleResponse.data.body.song.list)
                } else {
                    errorInfo.postValue("搜索结果异常")
                }

                // 提交信息
                viewModelScope.launchWebApi<Unit> {
                    method {
                        qmdApi.addSearch(SearchReqBody(SystemInfoUtil.UID, keyWord))
                    }
                }
            }

            fail {
                errorInfo.postValue("搜索失败: $it")
            }

            exception {
                if (it is CancellationException) {
                    return@exception
                }
                errorInfo.postValue("搜索出现异常: $it")
            }
        }
    }

    fun deleteHistory(item: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.delete(item)
        }
    }

    fun insertHistory(item: SearchHistory) {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.insert(item)
        }
    }

    fun deleteAllHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            searchRepository.deleteAll()
        }
    }

}