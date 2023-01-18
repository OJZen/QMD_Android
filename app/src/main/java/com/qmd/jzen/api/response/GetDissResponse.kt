package com.qmd.jzen.api.response

import com.google.gson.annotations.SerializedName
import com.qmd.jzen.api.entities.MusicInfo

/**
 * Create by OJun on 2022/1/20.
 *
 */
data class GetDissResponse(
    val code: Int,
    @SerializedName("req_0") val moduleResponse: GetDissModuleResponse
)

data class GetDissModuleResponse(val code: Int, val data: DissData)

data class DissData(
    val code: Int,
    @SerializedName("dirinfo") val dirInfo: DissDirInfo,
    @SerializedName("total_song_num") val songNum: Int,
    @SerializedName("songlist") val songList: ArrayList<MusicInfo>
)

data class DissDirInfo(
    val id: Long,
    val title: String,
    @SerializedName("picurl") val picUrl: String,
    @SerializedName("songnum") val songNum: Int,
    @SerializedName("ctime") val createTime: Long,
    @SerializedName("mtime") val modifyTime: Long,
    val creator: DissCreator
)

data class DissCreator(
    @SerializedName("musicid") val qq: Long,
    @SerializedName("encrypt_uin") val encryptUin: String
)
