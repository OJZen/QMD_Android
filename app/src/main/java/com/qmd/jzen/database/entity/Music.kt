package com.qmd.jzen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.qmd.jzen.api.entities.*
import com.qmd.jzen.database.ListConverters

/**
 * Create by OJun on 2021/3/5.
 *
 */

@Entity(tableName = "FavoriteMusic")
@TypeConverters(ListConverters::class)
data class Music(
    @PrimaryKey var musicId: String,
    var mediaId: String,
    var title: String,
    var singer: ArrayList<String>,
    var album: String,
    var albumId: String,
    var isBuy: Boolean,
    var publishDate: String,
    var size128: Long,
    var size192: Long,
    var size320: Long,
    var sizeFlac: Long,
    var sizeHires: Long
) {
    fun toMusicInfo(): MusicInfo {
        val singerList = ArrayList<SingerInfo>()
        singer.forEach {
            singerList.add(SingerInfo(0, "", it, it))
        }
        return MusicInfo(
            musicId,
            title,
            singerList,
            AlbumInfo(0, albumId, album, album, "", ""),
            MusicFileInfo(mediaId, size128, size192, size320, sizeFlac, sizeHires),
            "",
            PayInfo(0, if (isBuy) 1 else 0, 0, 0, 0),
            publishDate
        )
    }

    fun getSingerName() = StringBuilder().let { sb ->
        singer.forEach {
            sb.append(it + "_")
        }
        sb.removeSuffix("_").toString()
    }
}