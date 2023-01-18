package com.qmd.jzen.entity;

import com.qmd.jzen.database.entity.Music;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by junzi on 2017/12/25.
 */

public class QMusic implements Serializable {
    private int id;                 //歌曲id
    private String name;            //歌名
    private String title;           //
    private String mid;             // mid
    private String timePublish;     //发布时间
    private String mediaMid;       //
    private String albumMid;       //
    private String singerName;     //歌手
    private String album;           //专辑
    private long size_320;          //文件大小
    private long size_192;          //文件大小
    private long size_128;          //文件大小
    private long size_ape;          //文件大小
    private long size_flac;          //文件大小
    private long size_ogg;          //文件大小
    private String url;             // 下载地址
    private String path;            // 文件路径
    private String picture_url;     // 图片路径
    private Lyric lyric;        // 歌词
    private boolean isSelect;   //是否选中
    private int albumPrice;    // 专辑价格
    private int payStatus;     // 支付状态 2为已支付

    private String songListId;  // 归属于哪个歌单
    private boolean noCopyright;     // 是否无版权
    private boolean isDigitalAblum;  // 是否数字专辑
    private boolean isBuy;            // 是否购买

    private String pmid;            // 专辑id

    public QMusic(String title, String singerName) {
        this.title = title;
        this.singerName = singerName;
    }

    public QMusic() {
    }

    public Lyric getLyric() {
        return lyric;
    }

    public void setLyric(Lyric lyric) {
        this.lyric = lyric;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTimePublish() {
        return timePublish;
    }

    public void setTimePublish(String timePublish) {
        this.timePublish = timePublish;
    }

    public String getMediaMid() {
        return mediaMid;
    }

    public void setMediaMid(String mediaMid) {
        this.mediaMid = mediaMid;
    }

    public String getAlbumMid() {
        return albumMid;
    }

    public void setAlbumMid(String albumMid) {
        this.albumMid = albumMid;
    }

    public String getSingerName() {
        if (singerName == null) {
            return "";
        }
        return singerName.trim();
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        if (path == null) {
            return "";
        }
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public long getSize_320() {
        return size_320;
    }

    public void setSize_320(long size_320) {
        this.size_320 = size_320;
    }

    public long getSize_192() {
        return size_192;
    }

    public void setSize_192(long size_192) {
        this.size_192 = size_192;
    }

    public long getSize_128() {
        return size_128;
    }

    public void setSize_128(long size_128) {
        this.size_128 = size_128;
    }

    public long getSize_ape() {
        return size_ape;
    }

    public void setSize_ape(long size_ape) {
        this.size_ape = size_ape;
    }

    public long getSize_flac() {
        return size_flac;
    }

    public void setSize_flac(long size_flac) {
        this.size_flac = size_flac;
    }

    public long getSize_ogg() {
        return size_ogg;
    }

    public void setSize_ogg(long size_ogg) {
        this.size_ogg = size_ogg;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getAlbumPrice() {
        return albumPrice;
    }

    public void setAlbumPrice(int albumPrice) {
        this.albumPrice = albumPrice;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public boolean noCopyright() {
        return noCopyright;
    }

    public void setNoCopyright(boolean copyright) {
        noCopyright = copyright;
    }

    public boolean isDigitalAblum() {
        return isDigitalAblum;
    }

    public void setDigitalAblum(boolean digitalAblum) {
        isDigitalAblum = digitalAblum;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }


    public String getPmid() {
        return pmid;
    }

    public void setPmid(String pmid) {
        this.pmid = pmid;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getSongListId() {
        return songListId;
    }

    public void setSongListId(String songListId) {
        this.songListId = songListId;
    }

    public Music toMusicEntity() {
        ArrayList<String> names = new ArrayList<>();
        names.add(singerName);
        return new Music(mid, mediaMid, title, names, album, albumMid, isBuy, timePublish,
                size_128, size_192, size_320, size_ogg, size_flac);
    }
}
