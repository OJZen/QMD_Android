package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.database.entity.SongListInfo
import com.qmd.jzen.database.repository.SongListInfoRepository
import com.qmd.jzen.entity.SongListCounting
import com.qmd.jzen.musicOperator.MusicSongList
import com.qmd.jzen.musicOperator.MusicSongListWithQQ
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by OJun on 2022/3/6.
 *
 */
class SongListFragmentViewModel : ViewModel() {
    private val database = SongListInfoRepository(QMDApplication.database.songListInfoDao())
    private val qmdApi = QMDRepository(ApiSource.getServerApi())
    val songListInfoArray = database.allSongList
    val showProcess: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * 歌单码处理
     */
    fun processSongListCode(code: String) {
        showProcess.value = true
        val data = code.split(":/").toTypedArray()
        if (data.size != 2) {
            // 歌单码出错
            showError()
            return
        }
        if (!data[0].equals("qmd", ignoreCase = true)) {
            return
        }
        val sid = data[1].toLongOrNull()
        if (sid == null) {
            showError()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            // 添加歌单id
            val songlist = MusicSongList(sid).songList?.also {
                // 提交播放数据
                addSongList(it.sid, it.title)
                database.insert(it)
            }
            if (songlist == null) {
                showError()
            }
            showProcess.postValue(false)
        }
    }

    /**
     * QQ歌单处理
     */
    fun processSongListQQ(qq: String) {
        showProcess.value = true
        val correctQQ = qq.toLongOrNull()
        if (correctQQ == null) {
            showError()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val ids = withContext(Dispatchers.IO) {
                MusicSongListWithQQ(correctQQ).songListID
            }

            // 判断歌单数量
            if (ids.isEmpty()) {
                Toaster.out("QQ号里面没有歌单，请检查QQ是否输入正确，并确保QQ音乐已开启个人主页权限！")
                showProcess.value = false
                return@launch
            }

            withContext(Dispatchers.IO) {
                MusicSongList().getSongListWithIDList(ids)?.let { list ->
                    database.insert(*(list.toTypedArray()))
                    list.forEach { info ->
                        // 提交播放数据
                        addSongList(info.sid, info.title)
                        // 判断加入数据库是否成功
                        database.insert(info)
                    }
                }
            }
            showProcess.value = false
        }
    }

    private fun showError() {
        Toaster.out(R.string.text_input_correct)
        showProcess.value = false
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            database.deleteAll()
        }
    }

    fun delete(vararg songListInfo: SongListInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            database.delete(*songListInfo)
        }
    }

    private suspend fun addSongList(songListId: Long, title: String) {
        try {
            val result = qmdApi.addSongListCounting(SongListCounting(songListId.toString(), title))
            Logger.i(result.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}