package com.qmd.jzen.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qmd.jzen.R
import com.qmd.jzen.api.entities.MusicInfo
import com.qmd.jzen.musicOperator.MusicImage
import com.qmd.jzen.utils.Toaster
import de.hdodenhof.circleimageview.CircleImageView

class MusicListFromSearchAdapter(// 所有音乐列表
    var musicList: ArrayList<MusicInfo>, private val parentActivity: Activity
) : RecyclerView.Adapter<MusicListFromSearchAdapter.MusicAdapterHolder>() {
    lateinit var context: Context
    private var clickListener: OnMusicItemClickListener? = null

    // 设置单击接口
    fun setItemClickListener(clickListener: OnMusicItemClickListener?) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapterHolder {
        context = parent.context
        // 获取item的view
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_musiclist, parent, false)
        return MusicAdapterHolder(itemView)
    }

    override fun onBindViewHolder(holder: MusicAdapterHolder, position: Int) {
        val music = musicList[position]
        holder.textMusicName.text = music.title
        holder.textSinger.text = music.singer[0].title

        // 设置图片
        val imageUrl = MusicImage(music.album.mid).imgUrl
        Glide.with(context).load(imageUrl).into(holder.imageMusic)

        // 点击监听
        if (clickListener != null) {
            holder.itemView.setOnClickListener { v: View -> clickListener!!.onItemClick(v, music) }
            holder.btnMore.setOnClickListener { v: View -> clickListener!!.onItemMenuClick(v, music) }
        }

        // 判断到达底部自动加载下一页
        if (position == itemCount - 5) {
            //Logger.e("到达底部")
            //loadMoreMusic()
            //TODO("Paging，待开发")
            Toaster.out("没有更多内容啦")
            Toaster.out("其实是没做完")
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    /**
     * 继承已实现的Holder
     */
    inner class MusicAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textMusicName: TextView
        var textSinger: TextView
        var imageMusic: CircleImageView
        var btnMore: ImageView

        init {
            imageMusic = itemView.findViewById(R.id.image_album_pic)
            textMusicName = itemView.findViewById(R.id.text_music_title)
            textSinger = itemView.findViewById(R.id.text_music_singer)
            btnMore = itemView.findViewById(R.id.image_menu)
        }
    }
}