package com.qmd.jzen.extensions

import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import retrofit2.Response

/**
 * Create by OJun on 2021/12/27.
 *
 */
fun <T> CoroutineScope.launchWebApi(dsl: RetrofitDSL<T>.() -> Unit):Job {
    val listener = RetrofitDSL<T>().also(dsl)
    return launch(Dispatchers.Main) {
        try {
            val response = withContext(Dispatchers.IO) {
                 listener.methodCallback?.invoke() ?: throw Exception("Response is null")
            }
            if (response.isSuccessful) {
                val body = response.body()
                if (body == null) {
                    // 空 错误
                    throw Exception("Response body is null")
                } else {
                    // 成功
                    listener.successCallback?.invoke(body)
                    Logger.i(body.toString())
                }
            } else {
                // 失败，错误
                listener.failCallback?.invoke(response.toString())
                Logger.e(response.toString())
            }
        } catch (e: Exception) {
            // 异常
            listener.exceptionCallback?.invoke(e)
            Logger.e(e.toString())
        }
    }
}

class RetrofitDSL<T>() {
    var methodCallback: (suspend () -> Response<T>)? = null
    fun method(block: suspend () -> Response<T>) {
        methodCallback = block
    }

    var successCallback: ((T) -> Unit)? = null
    fun success(block: (T) -> Unit) {
        successCallback = block
    }

    var failCallback: ((String) -> Unit)? = null
    fun fail(block: (String) -> Unit) {
        failCallback = block
    }

    var exceptionCallback: ((Exception) -> Unit)? = null
    fun exception(block: (Exception) -> Unit) {
        exceptionCallback = block
    }
}
