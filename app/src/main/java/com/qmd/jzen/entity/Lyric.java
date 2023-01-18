package com.qmd.jzen.entity;

import java.io.Serializable;

public class Lyric implements Serializable {

    public enum STATUS{
        NOTHING,    // 没有歌词
        NONE,       // 没有资源，也就是获取歌词资源失败
        OK          // 歌词正常
    }

    private String title;           // 歌名
    private String singer;          // 歌手
    private String lyricBy;         // 歌词创作者
    private String album;           // 专辑
    private String lyric;           // 原生歌词
    private String trans;           // 原生翻译
    private String lyricText;       // 转成文本的歌词
    private String office;          // 偏移
    private STATUS status = STATUS.NONE;     // 歌词状态


    public String getLyricText() {
        if (lyricText == null) {
            return "";
        }
        return lyricText;
    }

    public void setLyricText(String lyricText) {
        this.lyricText = lyricText;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getTrans() {
        return trans;
    }

    public void setTrans(String trans) {
        this.trans = trans;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getLyricBy() {
        return lyricBy;
    }

    public void setLyricBy(String lyricby) {
        this.lyricBy = lyricby;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyric() {
        if (lyric == null) {
            return "";
        }
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }
}
