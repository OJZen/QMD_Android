package com.qmd.jzen.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.core.view.get
import androidx.navigation.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.qmd.jzen.R
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.network.MusicDownload
import com.qmd.jzen.ui.viewmodel.ShareMusicViewModel
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Create by OJun on 2021/3/5.
 * 提供一个接口给音乐项实现点击监听，包括单击，按钮点击，长按
 *
 */
interface OnMusicItemClickListener {
    fun onItemClick(view: View, music: MusicInfo)
    fun onItemMenuClick(view: View, music: MusicInfo)
    fun onItemLongClick(view: View, music: MusicInfo): Boolean
}

/**
 * 实现类，一共有三个地方要调用这个类，搜索的音乐，歌单的音乐，收藏的音乐
 */
class MusicItemClickListener(
    private val context: Context,
    private val shareViewModel: ShareMusicViewModel
) : OnMusicItemClickListener {

    private fun showPopupMenu(view: View, musicInfo: MusicInfo) {
        val music = musicInfo.toMusic()
        val menu = PopupMenu(context, view)
        menu.menuInflater.inflate(R.menu.popup_music_menu, menu.menu)
        val repository = QMDApplication.musicRepository
        var isFav = false
        CoroutineScope(Dispatchers.Default).launch {
            withContext(Dispatchers.IO) {
                isFav = repository.existMusic(musicInfo.musicId)
            }
            withContext(Dispatchers.Main) {
                if (isFav) {
                    menu.menu[0].title = "取消收藏"
                } else {
                    menu.menu[0].title = "收藏"
                }
                menu.show()
            }
        }

        // 菜单点击
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_favorite -> CoroutineScope(Dispatchers.IO).launch {
                    if (isFav) repository.delete(music)
                    else repository.insert(music)
                }
                R.id.menu_download -> {
                    MusicDownload(context).downloadMusic(musicInfo.toMusicEntity())
                }
                R.id.menu_info -> {
                    var info = "歌曲名：%s\n歌手：%s\n专辑：%s\n发布时间：%s"
                    info = String.format(
                        info,
                        musicInfo.title,
                        musicInfo.singer[0].name,
                        musicInfo.album.name,
                        musicInfo.publishDate
                    )
                    MaterialDialog(context).show {
                        title(text = "歌曲信息")
                        message(text = info)
                        positiveButton(text = "关闭")
                        negativeButton(text = "复制") {
                            val cm = QMDApplication.context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val data = ClipData.newPlainText("QMD 歌曲信息", info)
                            cm.setPrimaryClip(data)
                            Toaster.out("已将歌曲信息复制到剪切板")
                        }
                    }

                }
                else -> {
                }
            }
            false
        }
    }

    override fun onItemClick(view: View, music: MusicInfo) {
        shareViewModel.selectedMusic.postValue(music)
        view.findNavController().navigate(R.id.musicInfoFragment)
    }

    override fun onItemMenuClick(view: View, music: MusicInfo) {
        showPopupMenu(view, music)
    }

    override fun onItemLongClick(view: View, music: MusicInfo): Boolean {
        return false
    }
}