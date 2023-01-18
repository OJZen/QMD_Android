package com.qmd.jzen.entity;

import android.text.TextUtils;

import com.qmd.jzen.network.MusicDownload;
import com.qmd.jzen.utils.NameRuleManager;
import com.qmd.jzen.utils.QualityConverter;

/**
 * 用于检测url获取状态的实体
 * Create by OJun on 2021/2/28.
 */
public class LoadUrlEntity {
    // 0：正在等待，1：正在获取，2：获取资源成功，3：获取资源失败，4：文件已存在
    public static final int WAITING = 0;
    public static final int LOADING = 1;
    public static final int SUCCESS = 2;
    public static final int FAILURE = 3;
    public static final int FILE_EXIST = 4;

    int loadState = 0;
    String fileName;
    MusicEntity music;
    MusicQuality quality;
    String url;

    public LoadUrlEntity(MusicEntity music, MusicQuality quality) {
        this.music = music;
        this.quality = quality;
    }

    public int getLoadState() {
        return loadState;
    }

    public void setLoadState(int loadState) {
        MusicDownload.getStateChange().postValue(this);
        this.loadState = loadState;
    }

    public String getFileName() {
        if (TextUtils.isEmpty(fileName)) {
            fileName = NameRuleManager.getFileName(music.getSinger(), music.getTitle());
        }
        return fileName;
    }

    public String getFilenameWithFormat() {
        QualityConverter converter = new QualityConverter(quality);
        return getFileName() + "." + converter.getFormat();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public MusicEntity getMusic() {
        return music;
    }

    public void setMusic(MusicEntity music) {
        this.music = music;
    }

    public MusicQuality getQuality() {
        return quality;
    }

    public void setQuality(MusicQuality quality) {
        this.quality = quality;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
