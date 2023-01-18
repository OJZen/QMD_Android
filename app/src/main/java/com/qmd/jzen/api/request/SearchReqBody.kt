package com.qmd.jzen.api.request

/**
 * Create by OJun on 2021/12/25.
 * 搜索接口用的
 */
data class SearchReqBody(val uid: String, val keyword: String)

data class SearchDataReqBody(val uid: String, val page: Int, val num: Int)