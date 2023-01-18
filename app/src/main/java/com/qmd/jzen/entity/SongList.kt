package com.qmd.jzen.entity

import androidx.room.PrimaryKey
import java.io.Serializable

/**
 * @property id 歌单id
 * @property title 歌单标题
 * @property desc 歌单描述
 * @property logoUrl logo的地址
 * @property creatorName 歌单创作者
 * @property num 歌曲数量
 *
 */
class SongList(
    @PrimaryKey val id: Long,
    val title: String,
    val desc: String,
    val logoUrl: String,
    val creatorName: String,
    val num: Int
) : Serializable {
    val numStr: String
        get() = num.toString() + "首"
}