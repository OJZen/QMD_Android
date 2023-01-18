package com.qmd.jzen.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.qmd.jzen.R
import com.qmd.jzen.adapters.SongListAdapter
import com.qmd.jzen.database.entity.SongListInfo
import com.qmd.jzen.databinding.FragmentSongListBinding
import com.qmd.jzen.ui.viewmodel.LoadMusicListViewModel
import com.qmd.jzen.ui.viewmodel.SongListFragmentViewModel
import com.qmd.jzen.utils.Toaster

class SongListFragment : Fragment() {

    private lateinit var adapter: SongListAdapter
    private lateinit var binding: FragmentSongListBinding

    private val loadMusicListViewModel: LoadMusicListViewModel by activityViewModels()
    private val viewModel: SongListFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSongListBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        // 悬浮按钮
        binding.fabAddSongList.setOnClickListener {
            MaterialDialog(requireContext()).input(hint = "QQ号码/歌单码")
            { _, text ->
                val input = text.trim().toString()
                if (input.length < 5) {
                    Toaster.out(R.string.text_input_correct)
                    return@input
                }
                // 判断是否歌单码
                if (input.contains(":/")) {
                    viewModel.processSongListCode(input)
                } else {
                    viewModel.processSongListQQ(input)
                }
            }.show {
                title(text = "添加歌单")
                positiveButton()
                negativeButton()
            }
        }

        // 初始化adapter
        adapter = SongListAdapter().also {
            binding.recyclerViewSonglist.adapter = it

            // item长按
            it.itemLongClickCallback = { _, songlist ->
                onItemLongClick(songlist)
            }
            // item单击
            it.itemClickCallback = { _, songlist ->
                loadMusicListViewModel.getMusicListFromSongList(songlist)
                findNavController().navigate(R.id.action_songListFragment_to_musicListFragment)
            }
        }

        // 来自数据库的歌单
        viewModel.songListInfoArray.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.state.showEmpty()
            } else {
                binding.state.showContent()
            }
            adapter.submitList(it)
        }

        viewModel.showProcess.observe(viewLifecycleOwner) {
            binding.progressBarLoad.visibility = if (it) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
        }

        binding.state.onEmpty {
            findViewById<TextView>(R.id.text_empty_msg).text = "歌单为空，点击右下角按钮添加歌单"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_songlist_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_del -> {
                MaterialDialog(requireActivity()).show {
                    title(text = "高能警告：")
                    message(text = "您是否要删除所有歌单？")
                    positiveButton {
                        viewModel.deleteAll()
                    }
                    negativeButton { }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onItemLongClick(songList: SongListInfo) {
        MaterialDialog(requireActivity()).//"删除歌单", "歌单详情", "复制歌单码"
        listItems(R.array.text_songlist_option) { _, index, _ ->
            when (index) {
                0 -> {
                    MaterialDialog(requireActivity()).show {
                        title(text = "删除歌单")
                        message(text = "是否删除?")
                        positiveButton(text = "删除") {
                            viewModel.delete(songList)
                        }
                        negativeButton(text = "取消")
                    }
                }
                1 -> {
                    val info = String.format(
                        "\n歌单名:  %s\n\n描述:  %s\n\n歌单建立者:  %s\n\n歌曲数量:  %s\n\n歌单ID:  %d",
                        songList.title, songList.desc, songList.creatorName, songList.num, songList.sid
                    )
                    MaterialDialog(requireActivity()).show {
                        icon(R.drawable.music_info)
                        title(text = "歌单信息")
                        message(text = info)
                        positiveButton(text = "关闭")
                    }
                }
                2 -> {
                    //获取剪贴板管理器
                    val cm = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", "qmd:/" + songList.sid)
                    cm.setPrimaryClip(mClipData)
                    Toaster.out("已复制到剪切板")
                }
            }
        }.show {
            title(text = "歌单")
            icon(R.drawable.music)
        }
    }
}