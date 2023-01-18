package com.qmd.jzen.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.adapters.MusicAdapter
import com.qmd.jzen.adapters.MusicItemClickListener
import com.qmd.jzen.adapters.MusicItemDetailsLookup
import com.qmd.jzen.adapters.MusicItemKeyProvider
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.databinding.FragmentMusicListBinding
import com.qmd.jzen.entity.MusicQuality
import com.qmd.jzen.network.MusicDownload
import com.qmd.jzen.ui.viewmodel.LoadMusicListViewModel
import com.qmd.jzen.ui.viewmodel.ShareMusicViewModel
import com.qmd.jzen.utils.Toaster
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicListFragment : Fragment() {
    private var adapter: MusicAdapter? = null
    private var selectionTracker: SelectionTracker<String>? = null
    private val shareViewModel: ShareMusicViewModel by activityViewModels()
    private val viewModel: LoadMusicListViewModel by activityViewModels()

    private lateinit var binding: FragmentMusicListBinding
    private var menuSelectAll: MenuItem? = null

    // 多选之后的按钮是否显示
    private var isButtonVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        binding.state.showLoading()
        viewModel.musicList.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }
            if (it.size == 0) {
                binding.state.showEmpty()
                selectionTracker?.clearSelection()
            } else {
                setListAdapter(it, savedInstanceState)
                binding.state.showContent()
            }
        }
        initButton()
        return binding.root
    }

    @SuppressLint("CheckResult")
    private fun initButton() {
        // 收藏按钮
        binding.fabFavorite.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressBarLoad.visibility = View.VISIBLE
                var msg: String
                withContext(Dispatchers.IO) {
                    msg = if (viewModel.isFavorite) {
                        viewModel.delete(selectionToMusicList())
                        "取消收藏"
                    } else {
                        viewModel.insert(selectionToMusicList())
                        withContext(Dispatchers.Main) {
                            selectionTracker?.clearSelection()
                        }
                        "收藏歌曲"
                    }
                }
                Toaster.out(msg)
                binding.progressBarLoad.visibility = View.INVISIBLE
            }
        }

        // 批量下载按钮
        binding.fabDownload.setOnClickListener {
            if (viewModel.musicList.value.isNullOrEmpty()) {
                Toaster.out("音乐未加载完成")
                return@setOnClickListener
            }

            if (selectionTracker == null) {
                return@setOnClickListener
            }
            // 音质选择
            MaterialDialog(requireContext()).show {
                listItems(R.array.list_download_quality) { _, index, _ ->
                    val quality = when (index) {
                        0 -> MusicQuality._hires
                        1 -> MusicQuality._flac
                        2 -> MusicQuality._320Kbps
                        3 -> MusicQuality._ogg
                        else -> MusicQuality._128Kbps
                    }

                    // 开始批量下载
                    MusicDownload(requireContext()).downloadBatchMusic(
                        selectionToMusicList(), quality
                    )
                    // 清空所有选择
                    selectionTracker!!.clearSelection()
                }
            }

        }
    }

    private fun selectionToMusicList(): ArrayList<MusicInfo> {
        // id匹配需要下载的歌曲
        val needToDownloadList = arrayListOf<MusicInfo>()
        selectionTracker!!.selection.forEach { id ->
            viewModel.musicList.value!!.forEach { musicInfo ->
                if (id == musicInfo.musicId) {
                    needToDownloadList.add(musicInfo)
                }
            }
        }
        return needToDownloadList
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectionTracker?.onSaveInstanceState(outState)
    }

    private fun setListAdapter(musicList: ArrayList<MusicInfo>, savedInstanceState: Bundle?) {
        // 设置播放的临时音乐列表
        shareViewModel.selectedMusicList = musicList

        if (adapter != null) {
            selectionTracker?.clearSelection()
            adapter!!.submitList(musicList)
            binding.recyclerViewMusicList.adapter = adapter
            return
        }

        // 设置adapter
        adapter = MusicAdapter().apply {
            Logger.i("music list size:${musicList.size}")
            submitList(musicList)

            val clickListener = MusicItemClickListener(requireActivity(), shareViewModel)
            setItemClickListener(clickListener)
            binding.recyclerViewMusicList.adapter = this

            selectionTracker?.clearSelection()

            selectionTracker = SelectionTracker.Builder(
                "musicSelection",
                binding.recyclerViewMusicList,
                MusicItemKeyProvider(binding.recyclerViewMusicList, ItemKeyProvider.SCOPE_CACHED),
                MusicItemDetailsLookup(binding.recyclerViewMusicList),
                StorageStrategy.createStringStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()

            selectionTracker?.let {
                // 设置adapter的tracker，用于多选
                tracker = it
                // 观察多选的变化
                it.addObserver(object : SelectionTracker.SelectionObserver<String>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        changeTitleAndButton()
                    }
                })
                // 用于状态恢复
                it.onRestoreInstanceState(savedInstanceState)
                changeTitleAndButton()
            }

        }
    }

    fun changeTitleAndButton() {
        selectionTracker?.let {
            val size = it.selection.size()
            requireActivity().title = if (size > 0) {
                showButton(true)
                "已选择${it.selection.size()}首歌曲"
            } else {
                showButton(false)
                "歌曲列表"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_music_select_menu, menu)
        menuSelectAll = menu.findItem(R.id.menu_select_all)
        menuSelectAll?.isVisible = isButtonVisible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_select_all) {
            if (adapter == null) {
                Toaster.out("歌曲列表没有加载完毕")
                return false
            }

            selectionTracker?.let {
                if (it.selection.size() == adapter!!.itemCount) {
                    it.clearSelection()
                } else {
                    for (musicInfo in adapter!!.differ.currentList) {
                        it.select(musicInfo.musicId)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showButton(isVisible: Boolean) {
        isButtonVisible = isVisible
        menuSelectAll?.isVisible = isVisible
        if (isVisible) {
            binding.fabDownload.visibility = View.VISIBLE
            binding.fabFavorite.visibility = View.VISIBLE
        } else {
            binding.fabDownload.visibility = View.INVISIBLE
            binding.fabFavorite.visibility = View.INVISIBLE
        }
    }

    // 拦截返回键用的，但是目前actionbar的返回无法拦截。需要后续方案
    fun doNotBack(): Boolean {
        Logger.i("doNotBack")

        if (selectionTracker == null) {
            return false
        }
        if (!selectionTracker!!.selection.isEmpty) {
            selectionTracker!!.clearSelection()
            return true
        }
        return false
    }
}