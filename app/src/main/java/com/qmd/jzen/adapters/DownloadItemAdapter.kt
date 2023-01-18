package com.qmd.jzen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.qmd.jzen.R
import com.qmd.jzen.entity.Download
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.musicOperator.MusicLyric
import com.qmd.jzen.network.MusicDownload
import com.qmd.jzen.utils.CacheManager
import com.qmd.jzen.utils.FileUtil
import com.qmd.jzen.utils.NameRuleManager
import java.util.*

class DownloadItemAdapter(private val music: MusicEntity, private val mContext: Context) : RecyclerView.Adapter<DownloadItemAdapter.ViewHolder>() {
    private val downloadList: List<Download>

    companion object {
        private const val INFO_LRC = "歌词文件"
        private const val INFO_JPG = "专辑图片"
    }

    private var onItemClickListener: OnItemClickListener? = null

    // 提取下载信息列表
    private fun toDownloadList(music: MusicEntity): List<Download> {
        val list: MutableList<Download> = ArrayList()
        if (music.size128 > 0) list.add(Download(music.title, music.singer, MusicQuality._128Kbps, music.size128))
        if (music.size192 > 0) list.add(Download(music.title, music.singer, MusicQuality._ogg, music.size192))
        if (music.size320 > 0) list.add(Download(music.title, music.singer, MusicQuality._320Kbps, music.size320))
        if (music.sizeFlac > 0) list.add(Download(music.title, music.singer, MusicQuality._flac, music.sizeFlac))
        if (music.sizeHires > 0) list.add(Download(music.title, music.singer, MusicQuality._hires, music.sizeHires))

        val lyricText = MusicLyric(music).cacheLyric
        if (lyricText.isNotEmpty()) {
            val lyrDL = Download(
                music.title, music.singer, null,
                lyricText.toByteArray().size.toLong()
            )
            lyrDL.format = "lrc"
            lyrDL.info = INFO_LRC
            list.add(lyrDL)
        }

        if (CacheManager.getBitmap(music.musicId) != null) {
            val imgDL = Download(
                music.title, music.singer, null,
                CacheManager.getPictureSize(music.musicId)
            )
            imgDL.format = "jpg"
            imgDL.info = INFO_JPG
            list.add(imgDL)
        }
        return list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setText(downloadList[holder.absoluteAdapterPosition])
        holder.buttonDownload.setOnClickListener {
            val download = MusicDownload(mContext)
            if (downloadList[holder.absoluteAdapterPosition].quality != null) {
                download.downloadMusic(music, downloadList[holder.absoluteAdapterPosition].quality!!)
            } else {
                when (downloadList[holder.absoluteAdapterPosition].info) {
                    INFO_LRC -> download.saveLrc(music)
                    INFO_JPG -> download.savePicture(music)
                }
            }
            if (onItemClickListener != null) {
                onItemClickListener!!.onClick(downloadList[holder.absoluteAdapterPosition])
            }
        }
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    class ViewHolder(inflater: LayoutInflater, parent: ViewGroup?) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_download_info, parent, false)) {

        private val textTitle: TextView = itemView.findViewById(R.id.textView_title)
        private val textInfo: TextView = itemView.findViewById(R.id.textView_info)
        val buttonDownload: Button = itemView.findViewById(R.id.button_download)

        fun setText(download: Download) {
            val title = NameRuleManager.getFileName(download.singer, download.title) + "." + download.format
            textTitle.text = title
            val info = String.format("%s  %s  %s", download.info, download.qualityText, FileUtil.convertSize(download.size))
            textInfo.text = info
        }
    }

    interface OnItemClickListener {
        fun onClick(download: Download?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    init {
        downloadList = toDownloadList(music)
    }

}