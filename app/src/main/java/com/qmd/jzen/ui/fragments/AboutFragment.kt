package com.qmd.jzen.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.databinding.FragmentAboutBinding
import com.qmd.jzen.ui.view.QMDActivity
import com.qmd.jzen.utils.CrashManager
import com.qmd.jzen.utils.SystemInfoUtil
import com.qmd.jzen.utils.Toaster
import java.util.*


class AboutFragment : Fragment() {
    lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater)
        initView()
        return binding.root
    }

    private fun initView(){
        // QQ群卡片点击
        binding.cardViewQQGroup.setOnClickListener {
            MaterialDialog(requireContext()).listItems(R.array.text_array_qqgroup) { _, index, _ ->
                when (index) {
                    0 -> joinQQGroup("W6hKrgsyW76UuSZjPRmRLyMXIHDkyY2a", "1005433803")
                }
            }.show {
                title(text = "QMD交流群（点击即可跳转）")
            }
        }

        binding.cardViewOffcialAccounts.setOnClickListener {
            copyText("享乐乎")
            Toaster.out("公众号名称已复制到剪切板，请到微信搜索关注。")
        }

        // 邮箱卡片点击
        binding.cardViewEmail.setOnClickListener {
            copyText("admin@jzen.tech")
            Toaster.out("邮箱地址已复制到剪切板。")
        }

        // 设置版本名
        binding.root.findViewById<TextView>(R.id.text_summary).text = "Version ${SystemInfoUtil.appVersionName}"

        binding.root.findViewById<CardView>(R.id.cardView_head).setOnClickListener {
            val content = requireContext().resources.getStringArray(R.array.donttouch)
            val index = Random().nextInt(content.size)
            Toaster.out(content[index])
        }

        binding.cardViewLog.setOnClickListener {
            val logText = CrashManager.logText
            MaterialDialog(requireContext()).show {
                title(text = "崩溃日志")
                message(text = logText)
                positiveButton(text = "复制内容") { dialog ->
                    copyText(logText)
                    Toaster.out("崩溃日志已复制，可以通过邮箱发送给开发者。")
                }
                negativeButton(text = "关闭")
            }
        }
        binding.cardViewTelegram.setOnClickListener {
            val uri = Uri.parse("https://t.me/+gc0qPKIJuQg2ZDg1")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        binding.cardViewTwitter.setOnClickListener {
            val uri = Uri.parse("https://twitter.com/JZennm")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    /**
     *
     * 发起添加群流程。
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     */
    private fun joinQQGroup(key: String, QNum: String) {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent)
        } catch (ex: Exception) {
            // 未安装手Q或安装的版本不支持
            Logger.e(ex.message!!)
            copyText(QNum)
            Toaster.out("未安装手机QQ/TIM或安装的版本不支持调用。已将QQ群号码复制到剪切板。")
        }
    }

    private fun copyText(content: String) {
        val cm = requireActivity().getSystemService(QMDActivity.CLIPBOARD_SERVICE) as ClipboardManager
        val cd = ClipData.newPlainText("qmd", content)
        cm.setPrimaryClip(cd)
    }

}