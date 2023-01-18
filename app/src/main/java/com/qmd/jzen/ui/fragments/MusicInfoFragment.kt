package com.qmd.jzen.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.qmd.jzen.R
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.databinding.FragmentMusicInfoBinding
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.musicOperator.MusicImage
import com.qmd.jzen.musicOperator.MusicLyric
import com.qmd.jzen.network.MusicDownload
import com.qmd.jzen.player.PlayController
import com.qmd.jzen.player.PlayList
import com.qmd.jzen.ui.activity.PlayerActivity
import com.qmd.jzen.ui.viewmodel.ShareMusicViewModel
import com.qmd.jzen.utils.CacheManager
import com.qmd.jzen.utils.Config
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicInfoFragment : Fragment(), View.OnClickListener, View.OnLongClickListener {

    private val shareViewModel: ShareMusicViewModel by activityViewModels()
    private lateinit var binding: FragmentMusicInfoBinding
    private lateinit var music: MusicInfo
    private lateinit var parentActivity: AppCompatActivity

    override fun onDetach() {
        super.onDetach()
        // 离开页面要重新显示父级的ActionBar
        parentActivity.supportActionBar?.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicInfoBinding.inflate(inflater)
        shareViewModel.selectedMusic.observe(viewLifecycleOwner) {
            parentActivity = (requireActivity() as AppCompatActivity)
            // 隐藏父级的actionbar
            parentActivity.supportActionBar?.hide()
            music = it
            init()
        }
        return binding.root
    }

    fun initView() {
        binding.fabDownload.setOnClickListener(this)
        binding.fabPlayer.setOnClickListener(this)
        binding.fabDownload.setOnLongClickListener(this)
        // 设置可选
        binding.textLyric.setTextIsSelectable(true)

        val config = resources.configuration
        // 竖屏
        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 添加返回按钮
            binding.toolbarInfo?.setupWithNavController(findNavController())
            binding.toolbarInfo?.title = music.title
        }
    }

    private fun init() {
        initView()
        // 如果本地含图片就不用在线获取
        val img = CacheManager.getBitmap(music.musicId)

        if (img != null) {
            // 小于800就加载大图
            if (img.width < 800) {
                loadPicture()
            }
            // 设置图片
            binding.imageScrollingTop.setImageBitmap(img)
        } else {
            loadPicture()
        }

        val lyricManager = MusicLyric(music.toMusicEntity())
        // 如果实体包含歌词就不用在线获取
        val lyricText = lyricManager.handledCacheLyric
        if (lyricText.isEmpty()) {
            binding.progressBarLoad.visibility = View.VISIBLE

            lifecycleScope.launch(Dispatchers.Main) {
                val lyric = withContext(Dispatchers.IO) {
                    lyricManager.getLyric()     // 获取歌词
                }
                binding.textLyric.text = lyric.lyricText
                if (binding.imageScrollingTop.drawable != null) {
                    binding.progressBarLoad.visibility = View.GONE
                }
            }
        } else {
            binding.textLyric.text = lyricText
        }
    }

    private fun loadPicture() {
        binding.progressBarLoad.visibility = View.VISIBLE
        val musicImage = MusicImage(music.album.mid, 800)
        Glide.with(this).load(musicImage.imgUrl).into(object : SimpleTarget<Drawable?>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable?>?) {
                binding.imageScrollingTop.setImageDrawable(resource)
                val bd = resource as BitmapDrawable
                // 保存图片
                CacheManager.saveBitmap(bd.bitmap, music.musicId)
                binding.progressBarLoad.visibility = View.GONE
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.fab_player -> {
                PlayController.instance.play(music.toMusicEntity())
                // 将音乐列表设置为播放列表
                PlayList.instance.confirm(shareViewModel.selectedMusicList)
                val intent = Intent(requireActivity(), PlayerActivity::class.java)
                startActivity(intent)
            }
            R.id.fab_download -> {
                // "下载音乐"
                val musicDownload = MusicDownload(requireActivity())
                musicDownload.downloadMusic(music.toMusicEntity())
            }
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (v.id == R.id.fab_download) {
            val musicEntity = music.toMusicEntity()
            val musicDownload = MusicDownload(requireActivity())
            musicDownload.showDownloadDialog(musicEntity)
        }
        return false
    }

}