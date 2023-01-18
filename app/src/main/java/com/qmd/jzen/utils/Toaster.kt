package com.qmd.jzen.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.qmd.jzen.extensions.runMain

/**
 * Create by OJun on 2022/1/18.
 *
 */
class Toaster {
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        @SuppressLint("ShowToast")
        fun init(context: Context) {
            this.context = context
        }

        fun out(text: String) {
            runMain {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            }
        }

        fun out(@StringRes res: Int) {
            out(context.getString(res))
        }

    }
}

