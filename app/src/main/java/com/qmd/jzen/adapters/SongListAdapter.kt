package com.qmd.jzen.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qmd.jzen.R
import com.qmd.jzen.adapters.SongListAdapter.SongListHolder
import com.qmd.jzen.database.entity.SongListInfo
import com.qmd.jzen.databinding.ItemSonglistBinding

class SongListAdapter : RecyclerView.Adapter<SongListHolder>() {
    private var mContext: Context? = null
    var itemClickCallback: ((view: View, SongListInfo) -> Unit)? = null
    var itemLongClickCallback: ((view: View, SongListInfo) -> Unit)? = null

    private val diffCallback = object : DiffUtil.ItemCallback<SongListInfo>() {
        override fun areItemsTheSame(oldItem: SongListInfo, newItem: SongListInfo): Boolean {
            return oldItem.sid == oldItem.sid
        }

        override fun areContentsTheSame(oldItem: SongListInfo, newItem: SongListInfo): Boolean {
            return oldItem.title == newItem.title
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(songLists: List<SongListInfo>) {
        differ.submitList(songLists)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongListHolder {
        if (mContext == null) {
            mContext = parent.context
        }
        // 获取item的view
        val itemView = LayoutInflater.from(mContext).inflate(R.layout.item_songlist, parent, false)
        val binding = ItemSonglistBinding.bind(itemView)
        return SongListHolder(binding)
    }

    override fun onBindViewHolder(holder: SongListHolder, position: Int) {
        val songList = getItem(position)
        // 设置数据
        holder.setData(songList)

        holder.itemView.setOnLongClickListener {
            itemLongClickCallback?.invoke(it, songList)
            false
        }
        holder.itemView.setOnClickListener {
            itemClickCallback?.invoke(it, songList)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private fun getItem(position: Int): SongListInfo {
        return differ.currentList[position]
    }

    inner class SongListHolder(val binding: ItemSonglistBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun setData(songListInfo: SongListInfo) {
            // 设置图片
            Glide.with(mContext!!).load(songListInfo.logoUrl).into(binding.itemSonglistImageLogo)
            binding.itemSonglistTextTitle.text = songListInfo.title
            binding.itemSonglistTextCreator.text = songListInfo.creatorName
            binding.itemSonglistTextDesc.text = songListInfo.desc
            binding.itemSonglistTextNum.text = "${songListInfo.num}首"
        }
    }
}