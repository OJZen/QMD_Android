package com.qmd.jzen.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.qmd.jzen.R;
import com.qmd.jzen.adapters.PlayListAdapter;
import com.qmd.jzen.databinding.ActivityPlayerBinding;
import com.qmd.jzen.entity.MusicEntity;
import com.qmd.jzen.network.MusicDownload;
import com.qmd.jzen.player.MusicService;
import com.qmd.jzen.player.MusicServiceConnection;
import com.qmd.jzen.player.NowPlayingMetadata;
import com.qmd.jzen.player.PlayController;
import com.qmd.jzen.player.PlayList;
import com.qmd.jzen.ui.fragments.PlayListDialogFragment;
import com.qmd.jzen.ui.viewmodel.PlayerActivityViewModel;
import com.qmd.jzen.utils.CacheManager;
import com.qmd.jzen.utils.ThemeColorManager;
import com.qmd.jzen.utils.Toaster;

public class PlayerActivity extends AppCompatActivity {

    private ActivityPlayerBinding binding;

    MusicServiceConnection musicServiceConnection;
    PlayList playList;

    PlayerActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeColorManager.getConfigStyle());
        // databinding
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.setClick(new ClickEvent(this));

        musicServiceConnection = MusicServiceConnection.Companion.getInstance(this,
                new ComponentName(this, MusicService.class));

        viewModel = new ViewModelProvider(this,
                (ViewModelProvider.Factory) new PlayerActivityViewModel.Factory(musicServiceConnection)).get(PlayerActivityViewModel.class);

        playList = PlayList.Companion.getInstance();
        initView();
        setObserve();
    }

    private void initView() {

        // 拖动进度条的监听
        binding.seekBarProgress.setOnSeekBarChangeListener(new SeekBarChangeListener());

        if (binding.recylerViewPlayList != null) {
            // 设置列表框
            binding.recylerViewPlayList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
            PlayListAdapter adapter = new PlayListAdapter();
            adapter.setItemClickListener((MusicEntity music, int position) -> {
                PlayController.Companion.getInstance().play(music);
            });
            binding.recylerViewPlayList.setAdapter(adapter);
            // 滚到当前位置
            binding.recylerViewPlayList.scrollToPosition(PlayList.Companion.getInstance().getNowPlayingIdPosition());
        }
    }

    // 设置观察数据
    void setObserve() {
        // 数据改变
        viewModel.getPlayingMetadata().observe(this, this::updateUI);

        // 进度改变
        viewModel.getMediaPosition().observe(this, position -> {
            String posText = NowPlayingMetadata.timestampToMSS(position);
            binding.textViewPosition.setText(posText);
            // 如果进度条被按下了，那就不用调进度
            if (binding.seekBarProgress.isPressed()) {
                return;
            }
            NowPlayingMetadata data = viewModel.getPlayingMetadata().getValue();
            if (data != null) {
                long duration = data.getDuration();
                if (duration > 0) {
                    int pos = (int) Math.floor(1.0 * position / duration * 100);
                    binding.seekBarProgress.setProgress(pos);
                }
            }
        });

        // 设置播放按钮的资源图片
        viewModel.getButtonRes().observe(this, resId -> binding.imageViewPlay.setImageResource(resId));

        // 设置加载进度条是否显示
        PlayController.Companion.getInstance().urlGotStatus().observe(this,
                isUrlGot -> binding.progressBar.setVisibility(isUrlGot ? View.INVISIBLE : View.VISIBLE)
        );
    }

    // 刷新界面
    void updateUI(NowPlayingMetadata data) {
        Glide.with(PlayerActivity.this).load(data.getICONUrl()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                binding.imageViewShow.setImageDrawable(resource);
                BitmapDrawable bd = (BitmapDrawable) resource;
                Bitmap bitmap = bd.getBitmap();
                // 保存图片
                CacheManager.INSTANCE.saveBitmap(bitmap, data.getID());
            }
        });
        binding.textViewTitle.setText(data.getTitle());
        binding.textViewSinger.setText(data.getSinger());
        binding.textViewDuration.setText(NowPlayingMetadata.timestampToMSS(data.getDuration()));
    }

    /**
     * Databinding的Click，控件onClick所触发的方法
     */
    public class ClickEvent {

        Context context;

        public ClickEvent(Context context) {
            this.context = context;
        }

        public void playAndPause() {
            int pbState = musicServiceConnection.getPlaybackState().getValue().getState();
            if (pbState == PlaybackStateCompat.STATE_NONE) {
                Toaster.Companion.out(R.string.msg_not_loaded);
            } else {
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    musicServiceConnection.getTransportControls().pause();
                } else if (pbState == PlaybackStateCompat.STATE_BUFFERING) {
                    Toaster.Companion.out("正在缓冲音乐");
                } else {
                    musicServiceConnection.getTransportControls().play();
                }
            }
        }

        // 下一曲
        public void next() {
            binding.imageViewNext.setEnabled(false);
            NowPlayingMetadata data = viewModel.getPlayingMetadata().getValue();
            if (data == null) {
                Toaster.Companion.out(R.string.msg_not_loaded);
                return;
            }

            // 加载下一首的资源
            playList.next(music -> {
                if (music == null) {
                    Toaster.Companion.out("不存在下一首");
                }
                binding.imageViewNext.setEnabled(true);
            });
        }

        // 上一曲
        public void previous() {
            binding.imageViewPrevious.setEnabled(false);
            NowPlayingMetadata data = viewModel.getPlayingMetadata().getValue();
            if (data == null) {
                Toaster.Companion.out(R.string.msg_not_loaded);
                return;
            }

            // 加载上一首的资源
            playList.previous(music -> {
                if (music == null) {
                    Toaster.Companion.out("不存在上一曲");
                }
                binding.imageViewPrevious.setEnabled(true);
            });
        }

        public void playlist() {
            if (playList.getPlayingList().size() > 0) {
                PlayListDialogFragment.newInstance().show(getSupportFragmentManager(), null);
            } else {
                Toaster.Companion.out("没有可操作的播放列表");
            }
        }

        public void download() {
            // 获取正在播放的音乐
            MusicEntity music = PlayController.Companion.getInstance().getNowPlayingMusic();
            if (music != null) {
                MusicDownload download = new MusicDownload(context);
                download.showDownloadDialog(music);
            } else {
                Toaster.Companion.out(R.string.msg_not_loaded);
            }
        }

        public void playMode() {
            if (binding.imageViewControl == null) {
                Toaster.Companion.out("控件出错了");
                return;
            }
            playList.changePlayMode();
            switch (playList.getPlayMode()) {
                case Random:
                    Toaster.Companion.out("随机播放");
                    binding.imageViewControl.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    break;
                case ListLoop:
                    Toaster.Companion.out("列表循环");
                    binding.imageViewControl.setImageResource(R.drawable.ic_repeat_black_24dp);
                    break;
                case SingleLoop:
                    Toaster.Companion.out("单曲循环");
                    binding.imageViewControl.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                    break;
                default:
                    binding.imageViewControl.setImageResource(R.drawable.ic_repeat_black_24dp);
            }
        }
    }


    class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            NowPlayingMetadata data = viewModel.getPlayingMetadata().getValue();
            if (data != null) {
                long duration = data.getDuration();
                if (duration > 0) {
                    long pos = (long) (progress / 100.0 * data.getDuration());
                    musicServiceConnection.getTransportControls().seekTo(pos);
                }
            }

        }
    }
}
