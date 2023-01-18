package com.qmd.jzen.api.services

import com.qmd.jzen.api.request.GetDissMusicReqBody
import com.qmd.jzen.api.request.MusicSearchReqBody
import com.qmd.jzen.api.request.MusicUrlReqBody
import com.qmd.jzen.api.response.GetDissResponse
import com.qmd.jzen.api.response.MusicSearchResponse
import com.qmd.jzen.api.response.VkeyBaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Create by OJun on 2021/9/25.
 *
 */
interface QQMusicService {
    @POST("cgi-bin/musicu.fcg")
    suspend fun getMusicUrl(@Body body: MusicUrlReqBody): Response<VkeyBaseResponse>

    @POST("cgi-bin/musicu.fcg")
    suspend fun searchMusic(@Body body: MusicSearchReqBody): Response<MusicSearchResponse>

    @POST("cgi-bin/musicu.fcg")
    suspend fun getDissMusic(@Body body: GetDissMusicReqBody): Response<GetDissResponse>
}