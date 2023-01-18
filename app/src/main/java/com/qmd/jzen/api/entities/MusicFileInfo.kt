package com.qmd.jzen.api.entities

import com.google.gson.annotations.SerializedName

/**
 * Create by OJun on 2022/1/19.
 *
 */
data class MusicFileInfo(
    @SerializedName("media_mid") val mediaId: String,
    @SerializedName("size_128mp3") val size128: Long,
    @SerializedName("size_192ogg") val size192: Long,
    @SerializedName("size_320mp3") val size320: Long,
    @SerializedName("size_flac") val sizeFlac: Long,
    @SerializedName("size_hires") val sizeHires: Long
)