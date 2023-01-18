package com.qmd.jzen.qmd

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.api.repository.QQMusicRepository
import com.qmd.jzen.api.response.GetDissResponse
import com.qmd.jzen.api.response.MusicSearchResponse
import com.qmd.jzen.api.services.QQMusicService
import com.qmd.jzen.entity.Cookie
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.utils.QualityConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Create by OJun on 2021/9/25.
 *
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTest {

    companion object {
        private const val TAG = "InstrumentedTest"
    }

    @Before
    fun doSomeThings() {
        Cookie.setCookie(
            "Q_H_L_2iZSg760eGuGceF8RCrKNpJ_cqKNNwDhOYevcyyAs2F9f2z3p93pQ2JKqQsrXs9",
            "350577342"
        )
    }

    @Test
    fun testApi() {
        runBlocking {
            val converter = QualityConverter(MusicQuality._flac, false)
            val filename = converter.getFileName("001YxfVL2UdnwB")
            val musicService = ApiSource.getQMusicApi<QQMusicService>()
            val result = QQMusicRepository(musicService).getUrl("000ruq2M3hZIAO", filename)
            if (result.isSuccess) {
                val response = result.getOrNull()
                if (response != null) {
                    println(response.toString())
                }
            }
        }
    }

    @Test
    fun testQMDServer() {
        runBlocking {
            val converter = QualityConverter(MusicQuality._flac, false)
            val filename = converter.getFileName("003UkWuI0E8U0l")
            Log.i(TAG, "filename: $filename")
            val api = QMDRepository(ApiSource.getServerApi())
            CoroutineScope(Dispatchers.IO).launchWebApi<String> {
                method {
                    api.getMusicLink(filename)
                }
            }.join()
        }
    }

    @Test
    fun testSearchMusic() {
        runBlocking {
            val api = QQMusicRepository(ApiSource.getQMusicApi())
            CoroutineScope(Dispatchers.IO).launchWebApi<MusicSearchResponse> {
                method {
                    api.searchMusic("孤勇者")
                }
                success {
                    println(it)
                }
            }.join()
        }
    }

    @Test
    fun testGetDissMusic() {
        runBlocking {
            val api = QQMusicRepository(ApiSource.getQMusicApi())
            CoroutineScope(Dispatchers.IO).launchWebApi<GetDissResponse> {
                method {
                    api.getDissMusic(5717054402)
                }
                success {
                    println(it)
                }
            }.join()
        }
    }

}