package com.qmd.jzen.api.request

/**
 * Create by OJun on 2021/12/25.
 *
 */
data class DownloadReqBody(
    val uid:String,
    val musicId:String,
    val title:String,
    val singerName:String,
    val quality:String
)