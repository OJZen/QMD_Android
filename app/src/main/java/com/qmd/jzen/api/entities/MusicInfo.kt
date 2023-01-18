package com.qmd.jzen.api.entities

import com.google.gson.annotations.SerializedName
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.entity.MusicEntity

/**
 * Create by OJun on 2022/1/19.
 *
 */
data class MusicInfo(
    @SerializedName("mid") val musicId: String,
    val title: String,
    val singer: ArrayList<SingerInfo>,
    val album: AlbumInfo,
    val file: MusicFileInfo,
    val subtitle: String,
    val pay: PayInfo,
    @SerializedName("time_public") val publishDate: String,
) {
    fun toMusic(): Music {
        val singerList = ArrayList<String>()
        singer.forEach {
            singerList.add(it.title)
        }
        val isBuy = pay.payMonth == 1

        return Music(
            musicId,
            file.mediaId,
            title,
            singerList,
            album.title,
            album.mid,
            isBuy,
            publishDate,
            file.size128,
            file.size192,
            file.size320,
            file.sizeFlac,
            file.sizeHires
        )
    }

    fun isBuy() = pay.payMonth != 0 || pay.priceAlbum == 0

    fun getSingerName() = StringBuilder().let { sb ->
        singer.forEach {
            sb.append(it.title + "_")
        }
        sb.removeSuffix("_").toString()
    }

    fun toMusicEntity(): MusicEntity {
        val isBuy = pay.payMonth == 1

        return MusicEntity(
            musicId,
            file.mediaId,
            title,
            getSingerName(),
            album.title,
            album.mid,
            isBuy,
            publishDate,
            file.size128,
            file.size192,
            file.size320,
            file.sizeFlac,
            file.sizeHires,
            ""
        )
    }
}


