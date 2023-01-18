package com.qmd.jzen.musicOperator

import android.text.TextUtils
import com.orhanobut.logger.Logger
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.entity.Cookie
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.entity.MusicLink
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.network.HttpManager
import com.qmd.jzen.utils.QualityConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MusicUrl(private val music: MusicEntity) {
    var hostL = "http://dl.stream.qqmusic.qq.com/"
    var hostW = "http://ws.stream.qqmusic.qq.com/"

    private val QMDApi = QMDRepository(ApiSource.getServerApi())

    enum class Method {
        Vkey,  // 普通
        Ekey,  // 加密
        Dkey // 下载 会减下载次数
    }

    suspend fun getURL(quality: MusicQuality): String {
        var downloadUrl: String
        val converter = QualityConverter(quality)
        // 未加密文件名
        val filename = converter.getFileName(music.mediaId)

        try {
            // 先从从服务器获取链接
            val response = QMDApi.getMusicLink(filename)
            if (response.isSuccessful) {
                downloadUrl = response.body().toString()
                // 判断地址是否有效
                if (canUsable(downloadUrl)) {
                    return downloadUrl
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 重置一下
        downloadUrl = ""

        if (downloadUrl.isEmpty()) {
            // vkey，用多了封号
            downloadUrl = getUrlSingle(quality, Method.Vkey)
        }

        Logger.e("文件地址：$downloadUrl")
        if (downloadUrl.isEmpty()) {
            return ""
        }

        // 判断是否加密
        if (downloadUrl.contains(filename)) {
            // 判断地址是否有效
            if (canUsable(downloadUrl)) {
                //保存到服务器
                CoroutineScope(Dispatchers.IO).launchWebApi<String> {
                    method { QMDApi.addMusicLink(MusicLink(filename, music.musicId, quality.toString(), downloadUrl)) }
                }
                return downloadUrl
            }
        }
        return ""
    }

    fun saveToServer() {

    }

    private fun getUrlSingle(quality: MusicQuality, method: Method): String {
        val reqData = getRequestJson(quality, method, music) ?: return ""
        Logger.e(reqData)
        val httpManager = HttpManager("https://u.y.qq.com/cgi-bin/musicu.fcg")

        // 获取数据
        val resultData = httpManager.postDataWithResult(reqData)
        Logger.e(resultData)
        try {
            val root_json = JSONObject(resultData)
            val info_json = root_json.getJSONObject("queryvkey").getJSONObject("data").getJSONArray("midurlinfo").getJSONObject(0)
            val purl = info_json.getString("purl")
            // 如果不包含，肯定是获取失败了
            return if (purl.contains(music.mediaId)) {
                "$hostW$purl&fromtag=140"
            } else {
                ""
            }
        } catch (ex: Exception) {
            Logger.e(ex.message!!)
        }
        return ""
    }

    /**
     * 判断目标url是否有效
     *
     * @param url url
     * @return
     */
    private fun canUsable(url: String): Boolean {
        if (TextUtils.isEmpty(url)) return false
        //Logger.e(url);
        val reader = HttpManager(url)
        // 判断文件地址是否有效
        return reader.isSuccess
    }

    private fun getRequestJson(quality: MusicQuality, mm: Method, music: MusicEntity?): String? {
        var isEncrypt = false
        if (TextUtils.isEmpty(Cookie.getMkey())) {
            return null
        }
        var method = ""
        var module = ""
        when (mm) {
            Method.Dkey -> {
                method = "CgiGetEDownUrl"
                module = "vkey.GetEDownUrlServer"
            }
            Method.Ekey -> {
                method = "CgiGetEVkey"
                module = "vkey.GetEVkeyServer"
                isEncrypt = true
            }
            Method.Vkey -> {
                method = "CgiGetVkey"
                module = "vkey.GetVkeyServer"
            }
        }
        try {
            val root = JSONObject()
            val comm = JSONObject()
            comm.put("ct", "19")
            comm.put("cv", "1777")
            root.put("comm", comm)
            val queryvkey = JSONObject()
            queryvkey.put("method", method)
            queryvkey.put("module", module)
            val param = JSONObject()
            param.put("uin", Cookie.getQQ())
            param.put("guid", "QMD" + Random().nextInt(1000))
            param.put("referer", "y.qq.com")
            val songtype = JSONArray()
            val filename = JSONArray()
            val songmid = JSONArray()
            if (music != null) {
                val converter = QualityConverter(quality, isEncrypt)
                val fn = converter.getFileName(music.mediaId)
                filename.put(fn)
                songmid.put(music.musicId)
                songtype.put(1)
            }
            param.put("songtype", songtype)
            param.put("filename", filename)
            param.put("songmid", songmid)
            queryvkey.put("param", param)
            root.put("queryvkey", queryvkey)
            return root.toString()
        } catch (ex: Exception) {
            Logger.e(ex.message!!)
        }
        return null
    }
}