package com.qmd.jzen.api.request

import com.google.gson.annotations.SerializedName
import com.qmd.jzen.api.common.ConstantParam
import com.qmd.jzen.api.entities.Comm

/**
 * Create by OJun on 2022/1/18.
 *
 */
data class MusicSearchReqBody(
    val comm: Comm,
    @SerializedName(ConstantParam.MusicSearchModuleName) val module: MusicSearchModule
)

data class MusicSearchModule(val method: String, val module: String, val param: MusicSearchParam)

data class MusicSearchParam(
    @SerializedName("num_per_page") val num: Int,
    @SerializedName("page_num") val page: Int,
    @SerializedName("query") val keyword: String
)

/*
    "comm" : {
      "ct" : "19",
      "cv" : "1845"
   },
   "music.search.SearchCgiService" : {
      "method" : "DoSearchForQQMusicDesktop",
      "module" : "music.search.SearchCgiService",
      "param" : {
         "num_per_page" : 20,
         "page_num" : 1,
         "query" : "孤勇者"
      }
   }
}
*/