package com.qmd.jzen.extensions

import android.os.Handler
import android.os.Looper

/**
 * Create by OJun on 2022/3/7.
 * 主线程运行
 */
fun runMain(block: () -> Unit) {
    if (Looper.myLooper() != Looper.getMainLooper()) {
        Handler(Looper.getMainLooper()).post {
            block()
        }
    } else {
        block()
    }
}