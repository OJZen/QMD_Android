package com.qmd.jzen.api.response

import com.google.gson.annotations.SerializedName
import com.qmd.jzen.api.common.ConstantParam
import com.qmd.jzen.api.entities.MusicInfo

/**
 * Create by OJun on 2022/1/18.
 *
 */
data class MusicSearchResponse(
    val code: Int,
    @SerializedName(ConstantParam.MusicSearchModuleName) val moduleResponse: MusicSearchModuleResponse
)

data class MusicSearchModuleResponse(val code: Int, val data: MusicSearchModuleData)

data class MusicSearchModuleData(val code: Int, val body: MusicSearchBody)

data class MusicSearchBody(val song: MusicSearchList)

data class MusicSearchList(val list: ArrayList<MusicInfo>)
