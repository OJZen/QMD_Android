package com.qmd.jzen.api.services

import com.qmd.jzen.api.request.DownloadReqBody
import com.qmd.jzen.api.request.FavouriteReqBody
import com.qmd.jzen.api.request.SearchDataReqBody
import com.qmd.jzen.api.request.SearchReqBody
import com.qmd.jzen.api.response.Notification
import com.qmd.jzen.entity.DeviceInfo
import com.qmd.jzen.entity.MusicLink
import com.qmd.jzen.entity.PlayRecord
import com.qmd.jzen.entity.SongListCounting
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Create by OJun on 2021/11/23.
 *
 */
interface QMDService {
    @POST("Cookies")
    suspend fun getCookie(@Body deviceInfo: DeviceInfo): Response<String>

    @GET("Notifications")
    suspend fun getNotifications(): Response<Notification>

    @POST("MusicLink")
    suspend fun addMusicLink(@Body musicLink: MusicLink): Response<String>

    @POST("MusicLink/link")
    suspend fun getMusicLink(@Body requestBody: RequestBody): Response<String>

    @POST("Search")
    suspend fun addSearch(@Body searchReqBody: SearchReqBody): Response<Unit>

    @POST("Search/Result")
    suspend fun getSearchData(@Body searchDataReqBody: SearchDataReqBody): Response<String>

    @POST("Download")
    suspend fun addDownload(@Body downloadReqBody: DownloadReqBody): Response<Unit>

    @POST("PlayRecords")
    suspend fun addPlayRecord(@Body playRecord: PlayRecord): Response<Unit>

    @POST("SongListCountings")
    suspend fun addSongListCounting(@Body songListCounting: SongListCounting): Response<Unit>

    @POST("Favorites")
    suspend fun addFavourite(@Body favouriteReqBody: FavouriteReqBody): Response<Unit>

}