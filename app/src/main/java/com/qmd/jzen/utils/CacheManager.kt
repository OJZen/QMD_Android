package com.qmd.jzen.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.orhanobut.logger.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStreamWriter

object CacheManager {

    lateinit var cachePath: String

    fun init(context: Context) {
        cachePath = context.externalCacheDir!!.path + "/"
    }

    // 获取缓存图片
    fun getBitmap(fileName: String): Bitmap? {
        val fileFullName = "$cachePath$fileName.jpg"
        val image = File(fileFullName)
        if (image.exists()) {
            val drawable = BitmapDrawable.createFromPath(fileFullName) as BitmapDrawable
            return drawable.bitmap
        }
        return null
    }

    // 缓存图片文件
    fun saveBitmap(bitmap: Bitmap?, fileName: String) {
        try {
            if (bitmap == null) {
                return
            }
            val fileFullName = "$cachePath$fileName.jpg"
            Logger.e(fileFullName)
            val file = File(fileFullName)
            if (file.exists()) {
                return
            }
            val outputStream = FileOutputStream(fileFullName)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (ex: Exception) {
            Logger.e(ex.message!!)
        }
    }

    /**
     * 获取单个jpg图片的大小
     *
     * @param fileName 文件名,通常这里是mid
     * @return
     */
    fun getPictureSize(fileName: String): Long {
        val image = File("$cachePath$fileName.jpg")
        return if (image.exists()) {
            image.length()
        } else 0
    }

    fun saveText(text: String, fileName: String) {
        try {
            if (text.isEmpty()) {
                return
            }
            Logger.i(text)

            val fileFullName = cachePath + fileName
            Logger.i(fileFullName)
            val file = File(fileFullName)
            if (file.exists()) {
                return
            }
            val outputStream = FileOutputStream(file)
            val writer = OutputStreamWriter(outputStream)
            writer.write(text)
            writer.flush()
            outputStream.close()
            writer.close()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun getText(fileName: String): String {
        try {
            if (fileName.isEmpty()) {
                return ""
            }
            val fileFullName = cachePath + fileName
            val file = File(fileFullName)
            if (!file.exists()) {
                return ""
            }
            val inputStream = FileInputStream(file)
            val data = inputStream.readBytes()
            inputStream.close()
            return String(data)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    /**
     * 获取缓存大小
     */
    val cacheSize: String
        get() {
            var totalSize: Long = 0
            File(cachePath).listFiles()?.forEach {
                totalSize += it.length()
            }
            return FileUtil.convertSize(totalSize)
        }

    // 清除缓存
    fun clearCache() {
        File(cachePath).listFiles()?.forEach {
            it.delete()
        }
    }
}