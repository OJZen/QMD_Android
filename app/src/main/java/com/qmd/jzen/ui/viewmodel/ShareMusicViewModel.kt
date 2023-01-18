package com.qmd.jzen.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qmd.jzen.api.entities.MusicInfo

/**
 * Create by OJun on 2022/1/21.
 * 各个Fragment共享数据的ViewModel
 */
class ShareMusicViewModel : ViewModel() {
    // 当前选择的音乐，进入MusicInfo页面之前会设置
    val selectedMusic: MutableLiveData<MusicInfo> = MutableLiveData()
    // 当前选择的音乐列表，如果点击了播放之后，这个列表就会变成播放列表
    var selectedMusicList: ArrayList<MusicInfo> = arrayListOf()
}