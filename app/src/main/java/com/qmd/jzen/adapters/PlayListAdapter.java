package com.qmd.jzen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmd.jzen.R;
import com.qmd.jzen.entity.MusicEntity;
import com.qmd.jzen.player.PlayList;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListAdapterHolder> {
    PlayList playListInstance;
    Context mContext;
    List<MusicEntity> playingList;
    PlayListAdapter.OnItemClickListener itemClickListener;

    public PlayListAdapter() {
        if (playListInstance == null) {
            playListInstance = PlayList.Companion.getInstance();
        }
        playingList = playListInstance.getPlayingList();
    }

    public void setItemClickListener(PlayListAdapter.OnItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public PlayListAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        // 获取item的view
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_playlist, parent, false);
        return new PlayListAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapterHolder holder, int position) {
        MusicEntity music = playingList.get(position);
        holder.setText(music.getTitle(), music.getSinger());

        playListInstance.getNowPlayingId().observeForever(id -> {
            // 判断当前item是否正在播放
            holder.isPlaying(music.getMusicId().equals(id));
        });

        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(view -> itemClickListener.onItemClick(music, position));
        }

    }

    @Override
    public int getItemCount() {
        if (playingList == null) {
            return 0;
        }
        return playingList.size();
    }

    class PlayListAdapterHolder extends RecyclerView.ViewHolder {
        TextView text_title;
        TextView text_singer;
        ImageView view_isPlaying;

        public PlayListAdapterHolder(@NonNull View itemView) {
            super(itemView);
            text_title = itemView.findViewById(R.id.item_playList_text_title);
            text_singer = itemView.findViewById(R.id.item_playList_text_singer);
            view_isPlaying = itemView.findViewById(R.id.item_playList_imageView_isPlaying);
        }

        public void isPlaying(boolean isPlaying) {
            if (isPlaying) {
                view_isPlaying.setVisibility(View.VISIBLE);
            } else {
                view_isPlaying.setVisibility(View.INVISIBLE);
            }
        }

        public void setText(String title, String singer) {
            text_title.setText(title);
            text_singer.setText(singer);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(MusicEntity music, int position);
    }

}
