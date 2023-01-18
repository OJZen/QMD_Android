package com.qmd.jzen.utils

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.orhanobut.logger.Logger
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.entity.DeviceInfo
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*

object SystemInfoUtil {
    private var Uid = ""

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    val systemLanguage: String
        get() = Locale.getDefault().language

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    val systemLanguageList: Array<Locale>
        get() = Locale.getAvailableLocales()

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    @JvmStatic
    val systemVersion: String
        get() = Build.VERSION.RELEASE

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    @JvmStatic
    val systemModel: String
        get() = Build.MODEL

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    @JvmStatic
    val deviceBrand: String
        get() = Build.BRAND

    /**
     * 获取应用唯一标识符（不是设备唯一ID）
     *
     * @return 唯一标识符
     */
    @JvmStatic
    val UID: String
        get() {
            val contents: String
            val filename = "uid"
            if (!TextUtils.isEmpty(Uid)) {
                return Uid
            }
            val file = File(QMDApplication.context!!.filesDir, filename)
            return try {
                if (file.exists()) {
                    val fis = QMDApplication.context!!.openFileInput(filename)
                    val inputStreamReader = InputStreamReader(fis, StandardCharsets.UTF_8)
                    val reader = BufferedReader(inputStreamReader)
                    contents = reader.readLine()
                } else {
                    contents = UUID.randomUUID().toString()
                    val fos = QMDApplication.context!!.openFileOutput(filename, Context.MODE_PRIVATE)
                    fos.write(contents.toByteArray())
                }
                Uid = contents
                contents
            } catch (ex: IOException) {
                Logger.e(ex.message!!)
                ""
            }
        }

    @JvmStatic
    val appVersionName: String
        get() = try {
            QMDApplication.context!!.packageManager.getPackageInfo(QMDApplication.context!!.packageName, 0).versionName
        } catch (e: Exception) {
            ""
        }

    @JvmStatic
    val packageName: String
        get() = try {
            QMDApplication.context!!.packageName
        } catch (e: Exception) {
            ""
        }

    @JvmStatic
    val appVersionCode: Int
        get() = try {
            QMDApplication.context!!.packageManager.getPackageInfo(QMDApplication.context!!.packageName, 0).versionCode
        } catch (e: Exception) {
            0
        }

    @JvmStatic
    val deviceInfo: DeviceInfo
        get() {
            val info = DeviceInfo(
                UID,
                systemModel,
                deviceBrand,
                appVersionName,
                systemVersion,
                appVersionCode.toString() + ""
            )
            var text: String? = (info.uid + info.deviceModel + info.deviceBrand + info.systemVersion
                    + info.appVersion + info.versionCode)
            text = EncryptAndDecrypt.encryptText(text, "F*ckYou!")
            info.ip = text
            return info
        }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    /*
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(QMDApplication.getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (tm != null) {

            return tm.getDeviceId();
        }
        return null;
    }*/
}