package com.qmd.jzen.ui.fragments

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.databinding.FragmentMainBinding
import com.qmd.jzen.player.MusicService
import com.qmd.jzen.player.MusicServiceConnection
import com.qmd.jzen.player.MusicServiceConnection.Companion.getInstance
import com.qmd.jzen.ui.activity.DownloadActivity
import com.qmd.jzen.ui.activity.PlayerActivity
import com.qmd.jzen.ui.viewmodel.LoadMusicListViewModel
import com.qmd.jzen.utils.Toaster
import kotlin.system.exitProcess

/**
 * 主页
 */
class MainFragment : Fragment() {
    private var musicServiceConnection: MusicServiceConnection? = null
    private val loadMusicListViewModel: LoadMusicListViewModel by activityViewModels()
    private lateinit var binding : FragmentMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        musicServiceConnection = getInstance(
            requireContext(),
            ComponentName(requireContext(), MusicService::class.java)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.click = ClickEvent()
        // 音乐按钮
        binding.fabPlayer.setOnClickListener {
            if (musicServiceConnection!!.hasData()) {
                val intent = Intent(requireActivity(), PlayerActivity::class.java)
                startActivity(intent)
            } else {
                Toaster.out("现在没有正在播放的音乐")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 如果关于和捐助被隐藏或者删除就强行退出
        if (binding.menuItemAbout.visibility == View.GONE || binding.menuItemDonate.visibility == View.GONE || binding.menuItemAbout.visibility == View.INVISIBLE || binding.menuItemDonate.visibility == View.INVISIBLE) {
            exitProcess(0)
        }

        val aboutParam = binding.menuItemAbout.layoutParams
        if (aboutParam.height < 50 || aboutParam.width < 40){
            exitProcess(0)
        }
        val donateParam = binding.menuItemDonate.layoutParams
        if (donateParam.height < 50 || donateParam.width < 40){
            exitProcess(0)
        }
    }

    inner class ClickEvent {
        fun openSonglist() {
            findNavController().navigate(R.id.songListFragment, null)
        }

        fun openDownload() {
            val intent = Intent(requireActivity(), DownloadActivity::class.java)
            startActivity(intent)
        }

        fun openDonate() {
            val dialog = AlertDialog.Builder(requireContext()).setTitle("请选择支付平台")
                .setItems(arrayOf("支付宝", "微信")) { _, which ->
                    val bundle = Bundle().apply {
                        putInt(DonateFragment.keyDonateWay, which)
                    }
                    findNavController().navigate(R.id.action_mainFragment_to_donateFragment, bundle)
                }.create()
            dialog.show()
        }

        fun openFavorite() {
            loadMusicListViewModel.getMusicListFromDatabase()
            findNavController().navigate(R.id.action_mainFragment_to_favoriteMusicFragment)
        }

        fun openAbout() {
            findNavController().navigate(R.id.action_mainFragment_to_aboutFragment)
        }

        fun openSetting() {
            findNavController().navigate(R.id.action_mainFragment_to_settingFragment)
        }

        fun openSearch(){
            findNavController().navigate(R.id.action_mainFragment_to_searchFragment)
        }
    }
}