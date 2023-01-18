package com.qmd.jzen.entity

import com.google.gson.annotations.SerializedName

/**
 * Create by OJun on 2021/3/7.
 *
 */
data class PlayRecord(
    val UID: String,
    val musicId: String,
    val title: String,
    @SerializedName("singerName") val singer: String,
    val quality: String
)
