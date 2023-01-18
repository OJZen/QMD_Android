package com.qmd.jzen.api

import com.orhanobut.logger.Logger
import com.qmd.jzen.entity.Cookie
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Create by OJun on 2021/11/23.
 * 获取API请求对象
 */
object ApiSource {

    // QQ音乐
    const val QQMUSICBASEURL = "https://u.y.qq.com/"
    // 服务器端
    const val SERVERURL = "http://127.0.0.1/"

    class AuthInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request().newBuilder()
                .addHeader("Referer", "https://y.qq.com/portal/profile.html")
                .addHeader(
                    "user-agent",
                    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36"
                )
                .addHeader("cookie", Cookie.getCookie())
                .build()
            return chain.proceed(request)
        }

    }

    inline fun <reified T> getQMusicApi(): T {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(QQMUSICBASEURL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(T::class.java)
    }

    inline fun <reified T> getServerApi(): T {
        val logger = HttpLoggingInterceptor.Logger {
            Logger.d(it)
        }

        val interceptor = HttpLoggingInterceptor(logger).apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(SERVERURL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(T::class.java)
    }


}