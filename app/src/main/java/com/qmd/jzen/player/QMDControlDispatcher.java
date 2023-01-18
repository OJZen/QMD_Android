package com.qmd.jzen.player;

import com.google.android.exoplayer2.ControlDispatcher;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;

/**
 * Create by OJun on 2021/5/18.
 * 这是一个通知栏控件的控制分发类，主要是重写快进后退为上下曲切换。
 */
public class QMDControlDispatcher implements ControlDispatcher {
    /** The default fast forward increment, in milliseconds. */
    public static final int DEFAULT_FAST_FORWARD_MS = 15_000;
    /** The default rewind increment, in milliseconds. */
    public static final int DEFAULT_REWIND_MS = 5000;

    PlayList playingObject;
    public QMDControlDispatcher(){
        playingObject = PlayList.Companion.getInstance();
    }

    @Override
    public boolean dispatchPrepare(Player player) {
        player.prepare();
        return true;
    }

    @Override
    public boolean dispatchSetPlayWhenReady(Player player, boolean playWhenReady) {
        player.setPlayWhenReady(playWhenReady);
        return true;
    }

    @Override
    public boolean dispatchSeekTo(Player player, int windowIndex, long positionMs) {
        player.seekTo(windowIndex, positionMs);
        return true;
    }

    @Override
    public boolean dispatchPrevious(Player player) {
        playingObject.previous(null);
        return true;
    }

    @Override
    public boolean dispatchNext(Player player) {
        playingObject.next();
        return true;
    }

    @Override
    public boolean dispatchRewind(Player player) {
        dispatchPrevious(player);
        return true;
    }

    @Override
    public boolean dispatchFastForward(Player player) {
        dispatchNext(player);
        return true;
    }

    @Override
    public boolean dispatchSetRepeatMode(Player player, int repeatMode) {
        player.setRepeatMode(repeatMode);
        return true;
    }

    @Override
    public boolean dispatchSetShuffleModeEnabled(Player player, boolean shuffleModeEnabled) {
        player.setShuffleModeEnabled(shuffleModeEnabled);
        return true;
    }

    @Override
    public boolean dispatchStop(Player player, boolean reset) {
        player.stop(reset);
        return true;
    }

    @Override
    public boolean dispatchSetPlaybackParameters(Player player, PlaybackParameters playbackParameters) {
        player.setPlaybackParameters(playbackParameters);
        return true;
    }

    @Override
    public boolean isRewindEnabled() {
        return true;
    }

    @Override
    public boolean isFastForwardEnabled() {
        return true;
    }
}
