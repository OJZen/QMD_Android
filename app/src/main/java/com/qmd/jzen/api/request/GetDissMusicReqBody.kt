package com.qmd.jzen.api.request

import com.google.gson.annotations.SerializedName
import com.qmd.jzen.api.entities.Comm

/**
 * Create by OJun on 2022/1/20.
 *
 */
data class GetDissMusicReqBody(
    val comm: Comm,
    @SerializedName("req_0") val req: GetDissModule
)

data class GetDissModule(val module: String, val method: String, val param: GetDissParam)

data class GetDissParam(
    @SerializedName("disstid") val disstid: Long,
    @SerializedName("onlysonglist") val onlySongList: Int,
    @SerializedName("song_begin") val beginIndex: Int,
    @SerializedName("song_num") val songNum: Int
)

/**
 *
{
"req_0": {
"module": "srf_diss_info.DissInfoServer",
"method": "CgiGetDiss",
"param": {
"disstid": 5717054402,
"onlysonglist": 0,
"song_begin": 0,
"song_num": 1
}
},
"comm": {
"ct": 20,
"cv": 1845
}
}
 */
