package com.qmd.jzen.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Create by OJun on 2022/2/19.
 * 歌单信息
 */
@Entity(tableName = "SongList")
data class SongListInfo(
    @PrimaryKey
    val sid: Long,
    val title: String,
    val desc: String,
    val logoUrl: String,
    val creatorName: String,
    val num: Int,
    val addTime: Long
)