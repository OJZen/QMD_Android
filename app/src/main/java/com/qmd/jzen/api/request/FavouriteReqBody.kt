package com.qmd.jzen.api.request

/**
 * Create by OJun on 2021/12/25.
 * 收藏音乐请求体
 */
data class FavouriteReqBody(
    val uid: String,
    val musicId: String,
    val title:String,
    val singerName:String
)
