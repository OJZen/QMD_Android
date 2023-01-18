package com.qmd.jzen.musicOperator

import android.util.Base64
import com.orhanobut.logger.Logger
import com.qmd.jzen.entity.Lyric
import com.qmd.jzen.entity.MusicEntity
import com.qmd.jzen.network.HttpManager
import com.qmd.jzen.utils.CacheManager.getText
import com.qmd.jzen.utils.CacheManager.saveText
import com.qmd.jzen.utils.Config.downloadPath
import com.qmd.jzen.utils.FileUtil
import com.qmd.jzen.utils.NameRuleManager
import com.qmd.jzen.utils.Toaster.Companion.out
import org.json.JSONObject
import java.util.regex.Pattern

class MusicLyric {
    private val url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=%s&g_tk=5381"
    private var musicId: String
    private var fileName: String? = null

    // 歌词和翻译
    private val lyric = Lyric()

    constructor(mid: String) {
        musicId = mid
    }

    constructor(entity: MusicEntity) {
        musicId = entity.musicId
        fileName = NameRuleManager.getFileName(entity.singer, entity.title) + ".lrc"
    }

    fun getLyric(): Lyric {
        initData() //初始化数据,先要拿到歌词
        if (lyric.status != Lyric.STATUS.NONE) {
            handleInfo() //处理歌词信息
            cacheLyric() // 缓存歌词
        }
        return lyric
    }

    val cacheLyric: String
        get() = getText(fileName!!)

    val handledCacheLyric: String
        get() {
            val rawLyric = cacheLyric
            if (rawLyric.isNotEmpty()) {
                lyric.lyric = rawLyric
                handleInfo()
            }
            return lyric.lyricText
        }

    fun cacheLyric() {
        // 保存到缓存
        saveText(lyric.lyric, fileName!!)
    }

    private fun initData() {
        // 未解密的歌词和翻译
        var lyric_encode: String? = null
        var trans_encode: String? = null
        val lyc_url = String.format(url, musicId)
        val reader = HttpManager(lyc_url)
        val data = reader.textData
        if (data.isEmpty()) {
            lyric.status = Lyric.STATUS.NONE
            return
        }
        // 获取开头和结尾的花括号,截取去json部分
        val start = data.indexOf("{")
        val end = data.lastIndexOf("}") + 1
        val json = data.substring(start, end)

        //Logger.e("歌词原始json:" + json);
        // 判断返回的json是否包含歌词
        if (!json.contains("lyric")) {
            lyric.status = Lyric.STATUS.NOTHING
            lyric.lyric = "啊咧？歌词丢了？(๑•̌.•̑๑)ˀ̣ˀ̣"
            return
        }
        try {
            // 获取歌词
            val jsonRoot = JSONObject(json)
            lyric_encode = jsonRoot.getString("lyric")
            // 判断是否有翻译
            if (json.contains("trans")) {
                trans_encode = jsonRoot.getString("trans")
            }
        } catch (ex: Exception) {
            Logger.e(ex.message!!)
        }

        // base64解密
        lyric.lyric = String(Base64.decode(lyric_encode, Base64.DEFAULT))
        lyric.trans = String(Base64.decode(trans_encode, Base64.DEFAULT))
        if (lyric.lyric.contains("]此歌曲为没有填词的纯音乐")) {
            lyric.status = Lyric.STATUS.NOTHING
        } else {
            lyric.status = Lyric.STATUS.OK
        }
        //Logger.e( "歌词:" + lyric.getLyric());
        //Logger.e( "翻译:" + lyric.getTrans());
    }

    // 获取未处理过的歌词
    val rawLyric: String
        get() = lyric.lyric

    // 获取未处理过的翻译
    val rawTrans: String
        get() = lyric.trans

    // 处理的信息
    // 比如将[ti:Red]换成Red,然后存到lyric
    private fun handleInfo() {
        // 分割成多行
        val lrcLines = lyric.lyric.split("\n").toTypedArray()
        val builder = StringBuilder()
        for (line in lrcLines) {
            if (line.contains("[ti:")) {
                // 歌曲名(标题)
                val title = getInfo(line, "ti")
                lyric.title = title
                builder.appendLine("歌名: $title")
            } else if (line.contains("[ar:")) {
                // 艺术家(歌手)
                val single = getInfo(line, "ar")
                lyric.singer = single
                builder.appendLine("歌手: $single")
            } else if (line.contains("[al:")) {
                // 专辑
                val album = getInfo(line, "al")
                lyric.album = album
                builder.appendLine("专辑: $album")
            } else if (line.contains("[by:")) {
                // 歌词创作者
                val by = getInfo(line, "by")
                lyric.lyricBy = "歌词提供: $by"
                builder.appendLine(by)
            } else if (line.contains("[offset:")) {
                // 偏移..暂时不用先
                lyric.office = getInfo(line, "offset")
            } else {
                // 将剩下的也全部添加进去
                builder.appendLine(line)
            }
        }
        val lrcText = builder.toString()

        // 将所有时间都去掉,剩下文本
        // 去除[00:00.00]
        var pattern = Pattern.compile("\\[\\d+:\\d+\\.\\d+\\]")
        var matcher = pattern.matcher(lrcText)
        var lrc = matcher.replaceAll("")
        // 去除[00:00:00]
        pattern = Pattern.compile("\\[\\d+:\\d+:\\d+\\]")
        matcher = pattern.matcher(lrc)
        lrc = matcher.replaceAll("")

        // 替换&apos;为'
        lrc = lrc.replace("&apos;", "'")
        lyric.lyricText = lrc
    }

    // 处理单行数据,将[ti:Red]中的Red返回出来
    private fun getInfo(content: String, info: String): String {
        val start = content.indexOf("[$info")
        val end = content.indexOf("]", start)
        val subStr = content.substring(start + 1, end) // 不需要中括号
        val splStr = subStr.split(":").toTypedArray()
        return if (splStr.size == 2) {
            splStr[1]
        } else {
            ""
        }
    }

    // 保存歌词为文件
    fun saveLyric(): Boolean {
        val path = "$downloadPath/$fileName"

        if (lyric.lyric.isEmpty()) {
            // 查一下缓存有没有
            val content = getText(fileName!!)
            if (content.isEmpty()) {
                // TODO("这里应该尝试再获取一下")
                out("歌词保存失败！可能此资源不存在歌词，或者歌词资源加载失败。")
                return false
            }
            lyric.lyric = content
        }
        // 保存为文本文件
        return FileUtil.saveTextFile(lyric.lyric, path)
    }
}