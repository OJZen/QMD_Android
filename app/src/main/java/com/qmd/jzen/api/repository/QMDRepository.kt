package com.qmd.jzen.api.repository

import com.qmd.jzen.api.request.DownloadReqBody
import com.qmd.jzen.api.request.FavouriteReqBody
import com.qmd.jzen.api.request.SearchDataReqBody
import com.qmd.jzen.api.request.SearchReqBody
import com.qmd.jzen.api.response.Notification
import com.qmd.jzen.api.services.QMDService
import com.qmd.jzen.entity.MusicLink
import com.qmd.jzen.entity.PlayRecord
import com.qmd.jzen.entity.SongListCounting
import com.qmd.jzen.utils.EncryptAndDecrypt
import com.qmd.jzen.utils.SystemInfoUtil
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

/**
 * Create by OJun on 2021/12/25.
 * TODO("需要加上异常捕获")
 */
class QMDRepository(private val service: QMDService) {

    suspend fun getCookie(): Response<String> {
        return service.getCookie(SystemInfoUtil.deviceInfo)
    }

    suspend fun getNotifications(): Response<Notification> {
        return service.getNotifications()
    }

    suspend fun addMusicLink(musicLink: MusicLink): Response<String> {
        return service.addMusicLink(musicLink)
    }

    suspend fun getMusicLink(filename: String): Response<String> {
        val data = "\"${EncryptAndDecrypt.encryptText(filename)}\""
        val requestBody = data.toRequestBody("application/json;charset=utf-8".toMediaType())
        return service.getMusicLink(requestBody)
    }

    suspend fun addSearch(searchReqBody: SearchReqBody): Response<Unit> {
        return service.addSearch(searchReqBody)
    }

    suspend fun getSearchData(searchDataReqBody: SearchDataReqBody): Response<String> {
        return service.getSearchData(searchDataReqBody)
    }

    suspend fun addDownload(downloadReqBody: DownloadReqBody): Response<Unit> {
        return service.addDownload(downloadReqBody)
    }

    suspend fun addPlayRecord(playRecord: PlayRecord): Response<Unit> {
        return service.addPlayRecord(playRecord)
    }

    suspend fun addSongListCounting(songListCounting: SongListCounting): Response<Unit> {
        return service.addSongListCounting(songListCounting)
    }

    suspend fun addFavourite(favouriteReqBody: FavouriteReqBody): Response<Unit> {
        return service.addFavourite(favouriteReqBody)
    }

}
