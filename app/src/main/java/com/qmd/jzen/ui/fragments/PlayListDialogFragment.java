package com.qmd.jzen.ui.fragments;

import android.content.ComponentName;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.qmd.jzen.R;
import com.qmd.jzen.adapters.PlayListAdapter;
import com.qmd.jzen.entity.MusicEntity;
import com.qmd.jzen.player.MusicService;
import com.qmd.jzen.player.MusicServiceConnection;
import com.qmd.jzen.player.PlayController;
import com.qmd.jzen.player.PlayList;

public class PlayListDialogFragment extends BottomSheetDialogFragment {
    MusicServiceConnection musicServiceConnection;
    BottomSheetBehavior bottomSheetBehavior;

    PlayListDialogFragment() {
    }

    public static PlayListDialogFragment newInstance() {
        return new PlayListDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicServiceConnection = MusicServiceConnection.Companion.getInstance(requireContext(),
                new ComponentName(requireContext(), MusicService.class));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist_dialog, container, false);
        // 列表框
        RecyclerView recyclerView_playlist = view.findViewById(R.id.recylerView_playList);
        // 整个底部对话框的布局
        View layout_bottom = view.findViewById(R.id.layout_bottom);
        // 标题
        TextView textView_title = view.findViewById(R.id.textView_title);

        // 设置列表框
        recyclerView_playlist.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        PlayListAdapter adapter = new PlayListAdapter();
        adapter.setItemClickListener((MusicEntity music, int position) -> {
            PlayController.Companion.getInstance().play(music);
            dismiss();
        });
        recyclerView_playlist.setAdapter(adapter);
        // 滚到当前位置
        recyclerView_playlist.scrollToPosition(PlayList.Companion.getInstance().getNowPlayingIdPosition());

        // 设置标题
        textView_title.setText(String.format("播放列表（共%d首）", PlayList.Companion.getInstance().getPlayingList().size()));

        bottomSheetBehavior = BottomSheetBehavior.from(layout_bottom);
        // 设置布局的高度
        ViewGroup.LayoutParams layoutParams = layout_bottom.getLayoutParams();
        layoutParams.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.6);
        layout_bottom.setLayoutParams(layoutParams);

        view.post(() -> {
            //设置高度
            bottomSheetBehavior.setPeekHeight(view.getHeight());
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

}
