package com.qmd.jzen.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.widget.Toast
import com.arialyy.aria.core.Aria
import com.drake.brv.utils.BRV
import com.drake.statelayout.StateConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.qmd.jzen.BR
import com.qmd.jzen.R
import com.qmd.jzen.api.ApiSource
import com.qmd.jzen.api.repository.QMDRepository
import com.qmd.jzen.api.response.Notification
import com.qmd.jzen.database.QMDRoomDatabase.Companion.getDatabase
import com.qmd.jzen.database.repository.MusicRepository
import com.qmd.jzen.database.repository.SearchHistoryRepository
import com.qmd.jzen.extensions.launchWebApi
import com.qmd.jzen.utils.*
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.system.exitProcess

class QMDApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder().tag("QMD").build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))

        CacheManager.init(this)

        // 初始化Music路径
        FileUtil.initialStorage()

        Toaster.init(this)
        // 配置初始化设置
        Config.initialize(this)
        // 初始化崩溃记录
        CrashManager.manager

        Aria.init(this)

        // bugly
        CrashReport.initCrashReport(applicationContext, "e6ff351f37", false)

        initData()

        // 缺省页全局配置
        StateConfig.apply {
            emptyLayout = R.layout.layout_empty // 配置全局的空布局
            loadingLayout = R.layout.layout_loading // 配置全局的加载中布局
            //setRetryIds(R.id.msg) // 全局的重试Id
        }
        // databinding
        BRV.modelId = BR.m

        // 如果名字被改了,就强行退出
        if (resources.getString(R.string.app_name) != "QMD") {
            exitProcess(0)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null
            private set

        val database
            get() = getDatabase(context!!)
        val musicRepository
            get() = MusicRepository(database.musicDao())
        val searchRepository
            get() = SearchHistoryRepository(database.searchHistoryDao())
    }

    /**
     * 获取服务器数据
     */
    private fun initData() {
        val qmdApi = QMDRepository(ApiSource.getServerApi())
        // 获取服务器数据
        CoroutineScope(Dispatchers.IO).launchWebApi<String> {
            method {
                qmdApi.getCookie()
            }
            success {
                EncryptAndDecrypt.decryptAndSetCookie(it)
            }
            fail {
                Toast.makeText(context, "获取服务器数据失败，歌曲将无法下载和试听。", Toast.LENGTH_SHORT).show()
            }
            exception {
                Toast.makeText(context, "获取服务器数据异常，歌曲将无法下载和试听。", Toast.LENGTH_SHORT).show()
            }
        }

        CoroutineScope(Dispatchers.IO).launchWebApi<Notification> {
            method {
                qmdApi.getNotifications()
            }
            success {
                NotificationManager(context, it).Notify()
            }
        }
    }
}