package com.qmd.jzen.api.response

/**
 * Create by OJun on 2021/9/25.
 *
 */

/**
 * @property code 0为正常
 */
data class VkeyBaseResponse(
    val code: Int, val queryVKey: QueryVKeyResponse
)

data class QueryVKeyResponse(val module: String, val method: String, val param: ResponseData)

data class ResponseData(val msg: String, val midUrlInfo: ArrayList<MidUrlInfo>)

data class MidUrlInfo(
    val songMid: String, val fileName: String, val purl: String,
    val vkey: String
)