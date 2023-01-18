package com.qmd.jzen.network

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.arialyy.aria.core.Aria
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.adapters.DownloadItemAdapter
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.api.request.DownloadReqBody
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.entity.Download
import com.qmd.jzen.entity.LoadUrlEntity
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.musicOperator.MusicLyric
import com.qmd.jzen.musicOperator.MusicUrl
import com.qmd.jzen.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 下载类
 */
class MusicDownload(val mContext: Context) {

    private val qmdApi = QMDRepository(ApiSource.getServerApi())

    private fun existLoadUrl(filename: String): Boolean {
        for (entity in loadUrlEntityList) {
            if (entity.filenameWithFormat == filename) {
                // 这两种状态是已经停止资源获取的状态，除此之外，都还在队列工作
                if (entity.loadState != LoadUrlEntity.FAILURE &&
                    entity.loadState != LoadUrlEntity.SUCCESS
                ) {
                    return true
                }
            }
        }
        return false
    }

    // 开始下载音乐，单曲
    fun downloadMusic(music: MusicEntity, quality: MusicQuality = Config.downloadQuality) {
        if (quality == MusicQuality.manual) {
            showDownloadDialog(music)
            return
        }
        if (Config.downloadPath.isNullOrEmpty()) {
            Toaster.out(R.string.text_downloadpath_notfound)
            return
        }
        val loadUrlEntity = LoadUrlEntity(music, quality)
        val musicPath = getFilePath(loadUrlEntity.filenameWithFormat)
        if (FileUtil.fileExists(musicPath)) {
            MaterialDialog(mContext).show {
                title(text = "提示：")
                message(text = "文件已存在，是否删除重新下载？")
                positiveButton {
                    FileUtil.deleteFile(musicPath)
                    val entityList = Aria.download(QMDApplication.context).allCompleteTask
                        ?: return@positiveButton
                    for (entity in entityList) {
                        if (entity.filePath == musicPath) {
                            // 删除
                            Aria.download(QMDApplication.context).load(entity.id).cancel(true)
                        }
                    }
                    Toaster.out("操作成功")
                    beginTask(loadUrlEntity)
                }
                negativeButton { }
            }
        } else {
            beginTask(loadUrlEntity)
        }
    }

    private fun beginTask(entity: LoadUrlEntity) {
        val filename = entity.filenameWithFormat
        if (existLoadUrl(filename)) {
            Toaster.out("资源获取任务已存在")
            Logger.e("下载任务已存在")
            return
        }

        // 设置文件名
        loadUrlEntityList.add(entity)

        CoroutineScope(Dispatchers.IO).launch {
            val music = entity.music
            var quality = entity.quality
            var isChange = false
            // 获取url
            val musicUrl = MusicUrl(music)
            var url = musicUrl.getURL(quality)

            // 判断是否要下载歌词
            if (Config.autoDownloadLrc) {
                val musicLyric = MusicLyric(music)
                musicLyric.cacheLyric()
                musicLyric.saveLyric()
            }
            // 是否为自动获取低音质
            if (TextUtils.isEmpty(url)) {
                if (Config.autoDownLoadLower) {
                    while (true) {
                        quality = changeLowerQuality(quality)
                        if (quality == null) break
                        // 利用低音质重新获取资源
                        url = musicUrl.getURL(quality)
                        if (!TextUtils.isEmpty(url)) {
                            entity.quality = quality
                            isChange = true
                            break
                        }
                    }
                }
            }

            withContext(Dispatchers.Main) {
                Logger.e("下载地址：$url")
                // 判断地址是否有效
                if (TextUtils.isEmpty(url)) {
                    entity.loadState = LoadUrlEntity.FAILURE
                    Toaster.out(music.title + "：获取音乐资源失败！")
                    return@withContext
                }
                if (isChange) {
                    Toaster.out("您选择的音质获取资源失败，已自动切换成低音质进行下载。")
                }
                val musicPath = getFilePath(entity.filenameWithFormat)

                // 提交数据
                addDownload(music, quality)

                if (!Aria.download(QMDApplication.context).taskExists(url)) {
                    Aria.download(QMDApplication.context).load(url).setFilePath(musicPath)
                        .create()
                    Toaster.out("获取资源成功，正在下载:" + entity.filenameWithFormat)
                } else {
                    Toaster.out("下载任务已存在")
                }
                entity.loadState = LoadUrlEntity.SUCCESS
            }
        }
    }

    // 更改更低一级音质的资源
    private fun changeLowerQuality(quality: MusicQuality?): MusicQuality? {
        return when (quality) {
            MusicQuality._hires -> MusicQuality._flac
            MusicQuality._flac -> MusicQuality._320Kbps
            MusicQuality._320Kbps -> MusicQuality._ogg
            MusicQuality._ogg -> MusicQuality._128Kbps
            else -> null
        }
    }

    fun showDownloadDialog(music: MusicEntity) {
        val dialog = MaterialDialog(mContext, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = "下载")
            customView(R.layout.dialog_dowload_option, scrollable = true, horizontalPadding = true)
            negativeButton(text = "关闭")
        }
        // Setup custom view content
        val customView = dialog.getCustomView()
        val recyclerView: RecyclerView = customView.findViewById(R.id.recyclerViewDownloadOption)
        val adapter = DownloadItemAdapter(music, mContext)
        // 点击后关闭对话框
        adapter.setOnItemClickListener(object : DownloadItemAdapter.OnItemClickListener {
            override fun onClick(download: Download?) {
                dialog.dismiss()
            }
        })
        recyclerView.adapter = adapter
    }

    private fun getFilePath(filename: String): String {
        return Config.downloadPath + "/" + filename
    }

    fun downloadBatchMusic(musicList: ArrayList<MusicInfo>, musicQuality: MusicQuality?) {
        Logger.i("批量下载 数量：${musicList.size} 音质：$musicQuality")
        var quality = musicQuality
        if (Config.downloadPath.isNullOrEmpty()) {
            Toaster.out(R.string.text_downloadpath_notfound)
            return
        }
        if (quality == null) {
            quality = Config.downloadQuality
        }
        Toaster.out("已加入资源获取队列")
        for (music in musicList) {
            val loadUrlEntity = LoadUrlEntity(music.toMusicEntity(), quality)
            val musicPath = getFilePath(loadUrlEntity.filenameWithFormat)
            val filename = loadUrlEntity.filenameWithFormat
            if (existLoadUrl(filename)) {
                Logger.e(filename + "任务已存在")
                continue
            }
            // 如果已存在就跳过
            if (FileUtil.fileExists(musicPath)) {
                loadUrlEntity.loadState = LoadUrlEntity.FILE_EXIST
            }
            // 设置文件名
            loadUrlEntityList.add(loadUrlEntity)
        }

        CoroutineScope(Dispatchers.IO).launch {
            var entityList = allLoadUrlEntity
            for (entity in entityList) {
                if (isCancelled) {
                    break
                }
                if (entity.loadState == LoadUrlEntity.WAITING) {
                    // 设置状态
                    entity.loadState = LoadUrlEntity.LOADING
                    val itemMusic = entity.music
                    var itemQuality = entity.quality

                    // 获取url
                    val musicUrl = MusicUrl(itemMusic)
                    var url = musicUrl.getURL(itemQuality)

                    // 判断是否要下载歌词
                    if (Config.autoDownloadLrc) {
                        val musicLyric = MusicLyric(itemMusic)
                        musicLyric.cacheLyric()
                        musicLyric.saveLyric()
                    }

                    // 是否为自动获取低音质
                    // TODO("测试自动获取低音质")
                    if (url.isEmpty() && Config.autoDownLoadLower) {
                        while (true) {
                            itemQuality = changeLowerQuality(itemQuality) ?: break
                            // 利用低音质重新获取资源
                            url = musicUrl.getURL(itemQuality)
                            if (url.isNotEmpty()) {
                                entity.quality = itemQuality
                                break
                            }
                        }
                    }

                    // 提交数据
                    if (!DebugUtil.isApkInDebug(QMDApplication.context) && url.isNotEmpty()) {
                        addDownload(itemMusic, itemQuality)
                    }

                    entity.url = url

                    withContext(Dispatchers.Main) {
                        val entityUrl = entity.url
                        Logger.i("批量下载，下载地址：$entityUrl")
                        if (TextUtils.isEmpty(entityUrl)) {
                            entity.loadState = LoadUrlEntity.FAILURE
                            return@withContext
                        }
                        val musicPath = getFilePath(entity.filenameWithFormat)
                        // 任务不存在时添加
                        if (!Aria.download(QMDApplication.context).taskExists(entityUrl)) {
                            Aria.download(QMDApplication.context).load(entityUrl).setFilePath(musicPath).create()
                        }
                        entity.loadState = LoadUrlEntity.SUCCESS

                        // 重新获取，防止列表被清空的时候还继续遍历
                        entityList = allLoadUrlEntity
                    }
                }
            }
        }
    }

    // 保存歌词
    fun saveLrc(music: MusicEntity) {
        val musicLyric = MusicLyric(music)
        if (musicLyric.saveLyric()) {
            Toaster.out("保存歌词完成")
        } else {
            Toaster.out("保存歌词失败")
        }
    }

    // 保存图片文件
    fun savePicture(music: MusicEntity) {
        val fileName = NameRuleManager.getFileName(music.singer, music.title) + ".jpg"
        val bitmap = CacheManager.getBitmap(music.musicId)
        if (bitmap == null) {
            Toaster.out("图片没有加载完成, 请确保此页面的图片已正常显示!")
            return
        }
        if (FileUtil.saveImageFile(bitmap, fileName)) {
            Toaster.out("图片保存完成:$fileName")
        } else {
            Toaster.out("图片保存失败!")
        }
    }

    private suspend fun addDownload(music: MusicEntity, quality: MusicQuality) {
        withContext(Dispatchers.IO) {
            val reqBody = DownloadReqBody(
                SystemInfoUtil.UID,
                music.musicId,
                music.title,
                music.singer,
                quality.toString()
            )
            try {
                qmdApi.addDownload(reqBody)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        val loadUrlEntityList: MutableList<LoadUrlEntity> = ArrayList()
        var isCancelled = false

        @JvmStatic
        var stateChange = MutableLiveData<LoadUrlEntity?>()

        @JvmStatic
        val allLoadUrlEntity: List<LoadUrlEntity>
            get() = loadUrlEntityList

        @JvmStatic
        fun deleteAllLoadUrlEntity() {
            isCancelled = true
            loadUrlEntityList.clear()
            stateChange.postValue(null)
        }
    }
}