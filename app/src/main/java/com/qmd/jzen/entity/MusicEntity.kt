package com.qmd.jzen.entity

import java.io.Serializable


/**
 * Create by OJun on 2022/1/23.
 * 用于应用内交换数据的音乐实体，主要是播放和下载。
 */
data class MusicEntity(
    var musicId: String,
    var mediaId: String,
    var title: String,
    var singer: String,
    var album: String,
    var albumId: String,
    var isBuy: Boolean,
    var publishDate: String,
    var size128: Long,
    var size192: Long,
    var size320: Long,
    var sizeFlac: Long,
    var sizeHires: Long,
    var url: String
) : Serializable
