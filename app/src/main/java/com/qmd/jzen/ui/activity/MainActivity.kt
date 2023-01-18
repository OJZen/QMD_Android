package com.qmd.jzen.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.arialyy.aria.core.Aria
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.databinding.ActivityMainBinding
import com.qmd.jzen.player.MusicService
import com.qmd.jzen.player.MusicServiceConnection.Companion.getInstance
import com.qmd.jzen.services.DownloadNotiService
import com.qmd.jzen.ui.fragments.MusicListFragment
import com.qmd.jzen.utils.SystemInfoUtil
import com.qmd.jzen.utils.ThemeColorManager

class MainActivity : AppCompatActivity() {
    private lateinit var configuration: AppBarConfiguration
    private lateinit var serviceIntent: Intent
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment

    // 窗口创建事件
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeColorManager.getConfigStyle())
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNav()
        // 检查权限
        checkPermission()

        // 启动服务
        serviceIntent = Intent(this, DownloadNotiService::class.java)
        startService(serviceIntent)
        Logger.i(SystemInfoUtil.UID)
    }

    // 权限返回检查，判断用户是否点击允许
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this@MainActivity, "获取权限失败，只能在线听歌，不能下载资源。",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 检查权限
    private fun checkPermission() {
        // 写入外部储存的权限
        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                1
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val controller = findNavController(R.id.fragment_main)
        return controller.navigateUp(configuration) || super.onSupportNavigateUp()
    }

    private fun setNav() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        configuration = AppBarConfiguration(navController.graph)
    }

    /*
     * 返回键
     * */
    override fun onBackPressed() {
        navHostFragment.childFragmentManager.fragments.forEach { fragment ->
            val needBackFragment = fragment as? MusicListFragment
            needBackFragment?.let {
                if (it.doNotBack()) {
                    return
                }
            }
        }

        if (findNavController(R.id.fragment_main).popBackStack()) {
            return
        }

        val musicServiceConnection = getInstance(this, ComponentName(this, MusicService::class.java))

        val entityList = Aria.download(this).allNotCompleteTask
        if (musicServiceConnection.isPlaying()) {
            MaterialDialog(this).show {
                title(text = "提示：")
                message(text = "还有音乐正在播放，需要停止音乐播放吗？")
                positiveButton(text = "停止") {
                    musicServiceConnection.transportControls.stop()
                    finish()
                }
                negativeButton(text = "后台播放") {
                    super.onBackPressed()
                }
            }
        } else if (!entityList.isNullOrEmpty()) {
            MaterialDialog(this).show {
                title(text = "是否退出？")
                message(text = "还有下载任务未完成")
                positiveButton(text = "退出") {
                    finish()
                }
                negativeButton(text = "取消")
            }
        } else {
            finish()
        }
    }
}