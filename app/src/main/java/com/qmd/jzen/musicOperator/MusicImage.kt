package com.qmd.jzen.musicOperator

import android.graphics.Bitmap
import com.qmd.jzen.network.HttpManager

// 获取歌曲图片
// 歌曲的album_mid
// 图片尺寸,默认为正方形,一个size即可
class MusicImage(albumId: String, val size: Int = 300) {
    companion object {
        const val SMALL_SIZE = 300
        const val BIG_SIZE = 800
        private const val url = "https://y.gtimg.cn/music/photo_new/T002R%dx%dM000%s.jpg?max_age=25920"
    }

    val imgUrl: String = String.format(url, size, size, albumId)

    // 获取图片
    val image: Bitmap?
        get() {
            val reader = HttpManager(imgUrl)
            // 如果啥也没有,就返回默认音乐图标
            return reader.imageData
        }

}