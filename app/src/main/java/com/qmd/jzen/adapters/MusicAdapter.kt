package com.qmd.jzen.adapters

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qmd.jzen.R
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.databinding.ItemMusiclistBinding
import com.qmd.jzen.musicOperator.MusicImage

// TODO("多选，但是收藏那里有bug")
class MusicAdapter : RecyclerView.Adapter<MusicAdapter.MusicAdapterHolder>() {
    private var clickListener: OnMusicItemClickListener? = null
    var tracker: SelectionTracker<String>? = null

    private val diffCallback = object : DiffUtil.ItemCallback<MusicInfo>() {
        override fun areItemsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            return oldItem.musicId == oldItem.musicId
        }

        override fun areContentsTheSame(oldItem: MusicInfo, newItem: MusicInfo): Boolean {
            return oldItem.singer == newItem.singer
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(musicList: List<MusicInfo>) {
        differ.submitList(musicList)
    }

    fun setOnMusicItemClickListener(listener: OnMusicItemClickListener) {
        clickListener = listener
    }

    fun getItem(position: Int): MusicInfo {
        return differ.currentList[position]
    }

    // 设置点击的接口，包括单击，长按，点击菜单
    fun setItemClickListener(listener: OnMusicItemClickListener?) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapterHolder {
        val binding = ItemMusiclistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicAdapterHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicAdapterHolder, position: Int) {
        val music = getItem(position)
        tracker?.let {
            holder.bind(music, it.isSelected(music.musicId))
        }
        // 点击监听
        clickListener?.let {
            holder.itemView.setOnClickListener { v: View -> it.onItemClick(v, music) }
            holder.itemView.setOnLongClickListener { v: View -> it.onItemLongClick(v, music) }
            holder.binding.imageMenu.setOnClickListener { v: View -> it.onItemMenuClick(v, music) }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class MusicAdapterHolder(val binding: ItemMusiclistBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var musicInfo: MusicInfo
        fun bind(music: MusicInfo, isActivated: Boolean = false) {
            binding.textMusicTitle.text = music.title
            binding.textMusicSinger.text = music.getSingerName()
            val imageUrl = MusicImage(music.album.mid, 300).imgUrl
            Glide.with(binding.root).load(imageUrl).into(binding.imageAlbumPic)
            musicInfo = music
            itemView.isActivated = isActivated

            if (isActivated) {
                itemView.background = ContextCompat.getDrawable(binding.root.context, R.color.selected)
            } else {
                itemView.background = ContextCompat.getDrawable(binding.root.context, R.drawable.ripple_img_item)
            }
        }

        fun getItemDetails() = object : ItemDetailsLookup.ItemDetails<String>() {
            override fun getPosition(): Int {
                return bindingAdapterPosition
            }

            override fun getSelectionKey(): String {
                return musicInfo.musicId
            }
        }
    }
}

class MusicItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<String>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        return view?.let {
            (recyclerView.getChildViewHolder(view) as MusicAdapter.MusicAdapterHolder).getItemDetails()
        }
    }
}

class MusicItemKeyProvider(val recyclerView: RecyclerView, scope: Int) :
    ItemKeyProvider<String>(scope) {

    override fun getKey(position: Int): String {
        return (recyclerView.adapter as MusicAdapter).getItem(position).musicId
    }

    override fun getPosition(key: String): Int {
        val position = (recyclerView.adapter as MusicAdapter).differ.currentList.indexOfFirst {
            it.musicId == key
        }
        return recyclerView.findViewHolderForAdapterPosition(position)?.bindingAdapterPosition ?: RecyclerView.NO_POSITION
    }
}

