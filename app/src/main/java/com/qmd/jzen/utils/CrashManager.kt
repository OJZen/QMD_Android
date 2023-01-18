package com.qmd.jzen.utils

import com.qmd.jzen.utils.SystemInfoUtil.appVersionCode
import com.qmd.jzen.utils.SystemInfoUtil.appVersionName
import com.qmd.jzen.utils.SystemInfoUtil.deviceBrand
import com.qmd.jzen.utils.SystemInfoUtil.packageName
import com.qmd.jzen.utils.SystemInfoUtil.systemModel
import com.qmd.jzen.utils.SystemInfoUtil.systemVersion
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Create by OJun on 2021/8/15.
 */
class CrashManager : Thread.UncaughtExceptionHandler {
    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     *
     * Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    override fun uncaughtException(t: Thread, e: Throwable) {
        saveLog(e)
        defaultHandler.uncaughtException(t, e)
    }

    /**
     * 保存日志文件
     */
    private fun saveLog(throwable: Throwable) {
        try {
            val format = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM)
            val date = format.format(Date())
            val builder = StringBuilder()
            builder.append(date).append("\n")
            builder.append("PackageName : ").append(packageName).append("\n")
            builder.append("SystemVersion : ").append(systemVersion).append("\n")
            builder.append("VersionCode : ").append(appVersionCode).append("\n")
            builder.append("VersionName : ").append(appVersionName).append("\n")
            builder.append("DeviceBrand : ").append(deviceBrand).append("\n")
            builder.append("Model : ").append(systemModel).append("\n\n")
            val writer: Writer = StringWriter()
            val printWriter = PrintWriter(writer)
            throwable.printStackTrace(printWriter)
            var cause = throwable.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.flush()
            printWriter.close()
            val result = writer.toString()
            builder.append(result)
            val sFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val fileName = "CrashLog" + sFormat.format(Date()) + ".log"

            CacheManager.saveText(builder.toString(), fileName)

        } catch (e: Exception) {
            e.printStackTrace()
            Toaster.out("写入崩溃日志错误 ${e.message}")
        }
    }

    companion object {
        val manager: CrashManager by lazy {
            CrashManager()
        }

        @JvmStatic
        val logText: String
            get() {
                val file = File(CacheManager.cachePath)
                val allLog = file.listFiles { pathname: File -> pathname.name.contains(".log") }
                if (allLog != null) {
                    if (allLog.isNotEmpty()) return CacheManager.getText(allLog[allLog.size - 1].name)
                }
                return "没有崩溃日志文件"
            }
    }


}