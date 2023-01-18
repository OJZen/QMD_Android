package com.qmd.jzen.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.afollestad.materialdialogs.list.customListAdapter
import com.arialyy.aria.core.Aria
import com.qmd.jzen.R
import com.qmd.jzen.adapters.ThemeListAdapter
import com.qmd.jzen.app.QMDApplication
import com.qmd.jzen.ui.activity.MainActivity
import com.qmd.jzen.utils.CacheManager
import com.qmd.jzen.utils.Config
import com.qmd.jzen.utils.ThemeColorManager
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class SettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {
    private lateinit var quantityList: Array<String>
    private lateinit var preferenceDownloadPath: Preference
    private lateinit var preferenceImagePath: Preference
    private lateinit var preferenceDownloadQuality: ListPreference
    private lateinit var preferencePlayQuality: ListPreference
    private lateinit var preferenceDownloadQuantity: ListPreference
    private lateinit var preferenceClearCache: Preference
    private lateinit var preferenceResetDownload: Preference
    private lateinit var preferenceNameRule: Preference
    private lateinit var preferenceThemeColor: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取资源下的字符串数组
        quantityList = resources.getStringArray(R.array.down_quantity)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey)

        // find
        preferenceDownloadPath = findPreference(Config.DOWNLOAD_PATH)!!
        preferenceImagePath = findPreference(Config.DOWNLOAD_IMAGE_PATH)!!
        preferenceDownloadQuality = findPreference(Config.DOWNLOAD_QUALITY)!!
        preferencePlayQuality = findPreference(Config.PLAY_QUANTITY)!!
        preferenceDownloadQuantity = findPreference(Config.DOWNLOAD_QUANTITY)!!
        preferenceNameRule = findPreference(Config.NAME_RULE)!!
        preferenceThemeColor = findPreference(Config.THEME_COLOR)!!

        preferenceClearCache = findPreference("clearcache")!!
        preferenceResetDownload = findPreference("resetdownload")!!

        // 设置点击监听
        preferenceDownloadPath.onPreferenceClickListener = this
        preferenceImagePath.onPreferenceClickListener = this
        preferenceClearCache.onPreferenceClickListener = this
        preferenceResetDownload.onPreferenceClickListener = this
        preferenceThemeColor.onPreferenceClickListener = this

        // 监听下载数量的变动，更改配置
        preferenceDownloadQuantity.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val index = newValue.toString().toInt()
            Aria.get(requireContext()).downloadConfig.maxTaskNum = quantityList[index].toInt()
            true
        }

        // 设置主题颜色文本
        val colorText = ThemeColorManager.getColorNameChs(requireActivity(), Config.themeColor)
        preferenceThemeColor.summary = colorText

        // 设置下载路径显示
        preferenceDownloadPath.summary = Config.downloadPath
        preferenceImagePath.summary = Config.downloadImagePath

        // 设置获取缓存大小
        preferenceClearCache.summary = CacheManager.cacheSize

    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            "downpath" -> MaterialDialog(requireActivity()).show {
                folderChooser(requireActivity(), initialDirectory = File(Config.downloadPath!!), emptyTextRes = R.string.text_folder_empty)
                { _, file ->
                    Config.downloadPath = file.absolutePath
                    preferenceDownloadPath.summary = file.absolutePath
                }
            }
            "imagepath" ->
                MaterialDialog(requireActivity()).show {
                    folderChooser(requireActivity(), initialDirectory = File(Config.downloadImagePath), emptyTextRes = R.string.text_folder_empty)
                    { _, file ->
                        Config.downloadImagePath = file.absolutePath
                        preferenceImagePath.summary = file.absolutePath
                    }
                }
            "clearcache" -> {
                CacheManager.clearCache()
                preference.summary = CacheManager.cacheSize
                Toast.makeText(QMDApplication.context, "缓存清除完成。", Toast.LENGTH_SHORT).show()
            }
            "resetdownload" ->
                MaterialDialog(requireActivity()).show {
                    title(text = "灾难性警告：")
                    message(text = "重置下载器将清空所有下载记录，包括已下载和正在下载的任务，但不会删除本地文件，你确定要重置吗？")
                    negativeButton { }
                    positiveButton { dialog ->
                        Aria.download(requireActivity()).removeAllTask(false)
                        Toaster.out("完事！")
                    }
                }
            "themecolor" -> choiceColorDialog(preference)
        }
        return false
    }

    private fun choiceColorDialog(preference: Preference) {
        val adapter = ThemeListAdapter(requireActivity())
        // 主题选择对话框
        MaterialDialog(requireActivity()).show {
            customListAdapter(adapter)
            title(text = "选择主题色")
            positiveButton { _ ->
                // 应用主题对话框
                MaterialDialog(requireActivity()).show {
                    title(text = "是否应用主题？")
                    message(text = "应用主题将重启应用程序。")
                    negativeButton { }
                    positiveButton {
                        // 确认按钮
                        val colorName = adapter.nowColor
                        Config.themeColor = colorName
                        preference.summary = ThemeColorManager.getColorNameChs(requireContext(), colorName)
                        // 重启APP
                        restartApp()
                    }
                }
            }
        }
    }

    // 重新启动APP
    private fun restartApp() {
        // 加个小延时，以便在退出前能够及时保存数据
        lifecycleScope.launch {
            delay(300)
            //启动页
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            Process.killProcess(Process.myPid())
        }
    }

}