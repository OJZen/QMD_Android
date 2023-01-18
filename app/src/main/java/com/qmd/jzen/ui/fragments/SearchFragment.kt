package com.qmd.jzen.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.drake.brv.utils.bindingAdapter
import com.drake.brv.utils.linear
import com.drake.brv.utils.setup
import com.drake.statelayout.state
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.adapters.MusicItemClickListener
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.database.entity.SearchHistory
import com.qmd.jzen.databinding.FragmentSearchBinding
import com.qmd.jzen.musicOperator.MusicImage
import com.qmd.jzen.ui.viewmodel.SearchFragmentViewModel
import com.qmd.jzen.ui.viewmodel.ShareMusicViewModel
import com.qmd.jzen.utils.Toaster
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private val viewModel: SearchFragmentViewModel by viewModels()
    private val shareViewModel: ShareMusicViewModel by activityViewModels()

    private lateinit var binding: FragmentSearchBinding

    // 脚布局空壳类
    class Footer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)

        // 音乐搜索的返回数据
        viewModel.musicList.observe(viewLifecycleOwner) { searchData ->
            // 初始化列表
            initList(searchData)

            // 显示列表内容
            if (searchData.isNotEmpty()) {
                binding.music.showContent()
            }
        }

        viewModel.searchHistory.observe(viewLifecycleOwner) {
            initHistory(it)
        }

        // 返回
        binding.toolbarSearch.setNavigationOnClickListener {
            if (binding.textSearch.isFocused) {
                binding.textSearch.clearFocus()
            } else {
                findNavController().popBackStack()
            }
        }

        // 搜索
        binding.textSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search(binding.textSearch.text.toString())
            }
            return@setOnEditorActionListener true
        }

        // 用焦点判断要不要显示搜索历史
        binding.textSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showHistory()
            } else {
                showMusic()
            }
        }

        if (viewModel.isShowHistory) {
            showHistory()
        } else {
            showMusic()
        }

        viewModel.errorInfo.observe(viewLifecycleOwner) {
            Toaster.out(it)
        }

        return binding.root
    }

    private fun initHistory(listData: List<SearchHistory>) {
        binding.historyRv.linear().setup {
            // 点击抖动
            clickThrottle = 1000
            addType<SearchHistory>(R.layout.item_search_history)
            addType<Footer>(R.layout.item_search_histoty_footview)
            // 删除历史记录
            R.id.item_history_delete.onClick {
                viewModel.deleteHistory(getModel())
            }
            //点击
            R.id.item_history.onClick {
                val keyword = getModel<SearchHistory>().keyword
                search(keyword)
                binding.textSearch.setText(keyword)
            }
            // 清空历史
            R.id.item_history_foot.onClick {
                viewModel.deleteAllHistory()
            }
        }.models = listData
        if (listData.isNotEmpty()) {
            binding.historyRv.bindingAdapter.addFooter(Footer())
        }
    }

    private fun showHistory() {
        binding.music.visibility = View.INVISIBLE
        binding.history.visibility = View.VISIBLE
        viewModel.isShowHistory = true
    }

    private fun showMusic() {
        binding.music.visibility = View.VISIBLE
        binding.history.visibility = View.INVISIBLE
        hideKeyboard()
        viewModel.isShowHistory = false
    }

    private fun initList(listData: ArrayList<MusicInfo>) {
        val clickListener = MusicItemClickListener(requireActivity(), shareViewModel)
        binding.musicRv.linear().setup {
            addType<MusicInfo>(R.layout.item_musiclist)
            onBind {
                val model = getModel<MusicInfo>()
                // 设置标题和歌手
                findView<TextView>(R.id.text_music_title).text = model.title
                findView<TextView>(R.id.text_music_singer).text = model.singer[0].title
                // 设置图片
                val imgView = findView<CircleImageView>(R.id.image_album_pic)
                val imgUrl = MusicImage(model.album.mid).imgUrl
                Glide.with(context).load(imgUrl).into(imgView)
                // 菜单点击
                findView<ImageView>(R.id.image_menu).setOnClickListener {
                    clickListener.onItemMenuClick(it, model)
                }
            }
            // 点击
            R.id.music_item_layout.onClick {
                val model = getModel<MusicInfo>()
                clickListener.onItemClick(itemView, model)
            }
        }.models = listData

    }

    // 搜索
    private fun search(keyword: String) {
        if (keyword.trim().isEmpty()) {
            return
        }
        showMusic()

        viewModel.search(keyword)

        // 显示正在加载
        binding.music.showLoading()
        // 取消焦点
        binding.textSearch.clearFocus()

        // insert to the database
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.insertHistory(SearchHistory(keyword, System.currentTimeMillis()))
        }
    }

    // 隐藏输入法
    private fun hideKeyboard() {
        val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(binding.textSearch.applicationWindowToken, 0)
    }

}