package com.qmd.jzen.api.repository

import android.util.Log
import com.qmd.jzen.api.common.ConstantParam
import com.qmd.jzen.api.entities.Comm
import com.qmd.jzen.api.request.*
import com.qmd.jzen.api.response.GetDissResponse
import com.qmd.jzen.api.response.MusicSearchResponse
import com.qmd.jzen.api.response.VkeyBaseResponse
import com.qmd.jzen.api.services.QQMusicService
import retrofit2.HttpException
import retrofit2.Response
import kotlin.random.Random

/**
 * Create by OJun on 2021/9/25.
 *
 */
class QQMusicRepository(private val service: QQMusicService) {
    companion object {
        private const val TAG = "MusicRepository"
    }

    suspend fun getUrl(mid: String, fileName: String): Result<VkeyBaseResponse?> {
        val body = MusicUrlReqBody(
            Comm("19", "1777"),
            MusicUrlModule(
                "CgiGetVkey", "vkey.GetVkeyServer",
                MusicUrlParam(
                    1, 0, listOf(fileName), "QMD${Random.nextInt(1000)}",
                    "y.qq.com", 0, listOf(mid), listOf(1), "00"
                )
            )
        )
        Log.i(TAG, body.toString())
        return try {
            val response = service.getMusicUrl(body)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMusic(keyword: String, num: Int = 10, page: Int = 1): Response<MusicSearchResponse> {
        val body = MusicSearchReqBody(
            Comm("19", "1845"),
            MusicSearchModule(
                "DoSearchForQQMusicDesktop",
                ConstantParam.MusicSearchModuleName,
                MusicSearchParam(num, page, keyword)
            )
        )
        return service.searchMusic(body)
    }

    suspend fun getDissMusic(dissId: Long): Response<GetDissResponse> {
        val body = GetDissMusicReqBody(
            Comm("20", "1845"),
            GetDissModule(
                "srf_diss_info.DissInfoServer",
                "CgiGetDiss",
                GetDissParam(dissId, 0, 0, 0)
            )
        )
        println(body)
        return service.getDissMusic(body)
    }

}