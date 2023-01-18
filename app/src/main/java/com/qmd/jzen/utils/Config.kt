package com.qmd.jzen.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.arialyy.aria.core.Aria
import com.qmd.jzen.entity.MusicQuality
import java.io.File

object Config {
    const val DOWNLOAD_QUALITY = "downquality"
    const val DOWNLOAD_PATH = "downpath"
    const val DOWNLOAD_QUANTITY = "downquantity"
    const val PLAY_QUANTITY = "playquality"
    const val DOWNLOAD_IMAGE_PATH = "imagepath"
    const val NAME_RULE = "namerule"
    const val THEME_COLOR = "themecolor"
    private const val AUTO_DOWNLOAD_LRC = "autodownlrc"
    private const val NOTIFICATION = "notification"
    private const val AUTO_DOWNLOAD_LOWER = "autodownlower"
    private var preferences: SharedPreferences? = null

    /*
     * 首次打开初始化配置
     * */
    fun initialize(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context).also {
            // 随便判断一个key是否存在，不存在则初始化
            val editor = it.edit()
            if (!it.contains(DOWNLOAD_PATH)) {
                editor.putString(DOWNLOAD_PATH, FileUtil.getStoragePath()) // 默认下载路径
            }
            if (!it.contains(DOWNLOAD_QUANTITY)) {
                editor.putString(DOWNLOAD_QUANTITY, "1") // 默认下载路径
                Aria.get(context).downloadConfig.maxTaskNum = 2
            }
            if (!it.contains("notifyNumber")) {
                editor.putInt("notifyNumber", 0) // 默认通知序号为0
            }
            if (!it.contains(DOWNLOAD_IMAGE_PATH)) {
                editor.putString(DOWNLOAD_IMAGE_PATH, "${FileUtil.getStoragePath()}/专辑图片") // 专辑图片下载路径
            }
            editor.apply()
        }
    }

    // 获取下载质量
    private val downloadQualityRaw: String
        get() {
            return preferences!!.getString(DOWNLOAD_QUALITY, "0") ?: "0"
        }

    // 获取播放质量
    private val playQualityRaw: String
        get() {
            return preferences!!.getString(PLAY_QUANTITY, "3") ?: "3"
        }

    val autoDownloadLrc: Boolean
        get() {
            return preferences!!.getBoolean(AUTO_DOWNLOAD_LRC, false)
        }

    val notification: Boolean
        get() {
            return preferences!!.getBoolean(NOTIFICATION, true)
        }
    val autoDownLoadLower: Boolean
        get() {
            return preferences!!.getBoolean(AUTO_DOWNLOAD_LOWER, true)
        }

    /**
     * 设置通知序号
     */
    var notifyNumber: Int
        get() {
            return preferences!!.getInt("notifyNumber", 0)
        }
        set(number) {
            val editor = preferences!!.edit()
            editor.putInt("notifyNumber", number)
            editor.apply()
        }

    val nameRule: Int
        get() {
            return preferences!!.getString(NAME_RULE, "0")!!.toInt()
        }// 默认主题颜色

    /***
     * 设置主题色
     */
    var themeColor: String
        get() {
            return preferences!!.getString(THEME_COLOR, "Red") ?: "Red"
        }
        /**
         * @param colorName 主题名
         */
        set(colorName) {
            val editor = preferences!!.edit()
            editor.putString(THEME_COLOR, colorName) // 默认主题颜色
            editor.apply()
        }

    /**
     * 获取下载质量，返回质量枚举
     */
    val downloadQuality: MusicQuality
        get() = downTextToQuality(downloadQualityRaw)

    /**
     * 获取试听质量，返回质量枚举
     */
    val playQuality: MusicQuality
        get() = playTextToQuality(playQualityRaw)

    /**
     * 设置图片下载的路径
     */
    var downloadImagePath: String
        get() {
            val path = preferences!!.getString(DOWNLOAD_IMAGE_PATH, "$downloadPath/专辑图片/")
            if (!FileUtil.fileExists(path)) {
                FileUtil.makeDirectory(path)
            }
            return if (path!!.endsWith("/")) {
                path
            } else {
                "$path/"
            }
        }
        set(path) {
            val editor = preferences!!.edit()
            editor.putString(DOWNLOAD_IMAGE_PATH, path)
            editor.apply()
        }

    /**
     * 设置下载路径
     */
    var downloadPath: String?
        get() {
            val path = preferences!!.getString(DOWNLOAD_PATH, FileUtil.getStoragePath()) ?: return null
            val file = File(path)
            return if (file.exists()) {
                path
            } else FileUtil.getStoragePath()
        }
        /**
         * @param path 下载路径
         */
        set(path) {
            val editor = preferences!!.edit()
            editor.putString(DOWNLOAD_PATH, path)
            editor.apply()
        }

    /*
     * 字符串转枚举类型
     * */
    private fun downTextToQuality(data: String?): MusicQuality {
        return when (data) {
            "0" -> MusicQuality.manual
            "1" -> MusicQuality._hires
            "2" -> MusicQuality._flac
            "3" -> MusicQuality._320Kbps
            "4" -> MusicQuality._ogg
            "5" -> MusicQuality._128Kbps
            else -> MusicQuality._320Kbps
        }
    }

    private fun playTextToQuality(data: String?): MusicQuality {
        return when (data) {
            "0" -> MusicQuality._320Kbps
            "1" -> MusicQuality._ogg
            "2" -> MusicQuality._128Kbps
            "3" -> MusicQuality._96Kbps
            else -> MusicQuality._128Kbps
        }
    }
}