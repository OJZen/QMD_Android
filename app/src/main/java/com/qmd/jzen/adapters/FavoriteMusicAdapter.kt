package com.qmd.jzen.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qmd.jzen.R
import com.qmd.jzen.database.entity.Music
import com.qmd.jzen.musicOperator.MusicImage
import de.hdodenhof.circleimageview.CircleImageView


/**
 * Create by OJun on 2021/3/5.
 *
 */
class FavoriteMusicAdapter : RecyclerView.Adapter<FavoriteMusicAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var clickListener: OnMusicItemClickListener

    private val diffCallback = object : DiffUtil.ItemCallback<Music>() {
        override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.musicId == oldItem.musicId
        }

        override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
            return oldItem.singer == newItem.singer
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(musicList: List<Music>) {
        differ.submitList(musicList)
    }

    fun setOnMusicItemClickListener(listener: OnMusicItemClickListener) {
        clickListener = listener
    }

    private fun getItem(position: Int): Music {
        return differ.currentList[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.text_music_title)
        val textSinger: TextView = view.findViewById(R.id.text_music_singer)
        val imageMenu: ImageView = view.findViewById(R.id.image_menu)
        val imagePic: CircleImageView = view.findViewById(R.id.image_album_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_musiclist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val music = getItem(position).apply {
            holder.textTitle.text = title
            val sb = StringBuilder()
            singer.forEach {
                sb.append(it + "_")
            }
            val singerName = sb.removeSuffix("_").toString()
            holder.textSinger.text = singerName
        }

        // 设置图片
        val imageUrl = MusicImage(music.albumId).imgUrl
        Glide.with(context).load(imageUrl).into(holder.imagePic)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(it, music.toMusicInfo())
        }

        holder.imageMenu.setOnClickListener {
            clickListener.onItemMenuClick(it, music.toMusicInfo())
        }
    }

    override fun getItemCount() = differ.currentList.size
}