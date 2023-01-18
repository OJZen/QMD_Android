package com.qmd.jzen.utils;

import com.qmd.jzen.entity.MusicQuality;

/*
 * 音质转换器,主要是下载的时候,需要转换成的链接标识和下载文件格式
 * */
public class QualityConverter {
    private String code;
    private String format;
    private String qStr;    // 品质对应的字符串
    private String info;    //附加信息
    private MusicQuality quality;

    public QualityConverter(MusicQuality quality) {
        initial(quality, false);
    }

    public QualityConverter(MusicQuality quality, boolean isEncrypt) {
        initial(quality, isEncrypt);
    }

    private void initial(MusicQuality quality, boolean isEncrypt) {
        this.quality = quality;
        switch (quality) {
            case _hires:
                code = "RS01";
                format = "flac";
                qStr = "Hi-Res";
                info = "高解析无损";
                break;
            case _flac:
                if (isEncrypt) {
                    code = "F0M0";
                    format = "mflac";
                } else {
                    code = "F000";
                    format = "flac";
                }
                qStr = "FLAC";
                info = "无损品质";
                break;
            case _320Kbps:
                code = "M800";
                format = "mp3";
                qStr = "320kbps";
                info = "超高品质";
                break;
            case _ogg:
                if (isEncrypt) {
                    code = "O6M0";
                    format = "mgg";
                } else {
                    code = "O600";
                    format = "ogg";
                }
                qStr = "OGG";
                info = "高品质 ";
                break;
            case _128Kbps:
                if (isEncrypt) {
                    code = "O4M0";
                    format = "mgg";
                } else {
                    code = "M500";
                    format = "mp3";
                }
                qStr = "128kbps";
                info = "标准品质";
                break;
            case _96Kbps:
                code = "C400";
                format = "m4a";
                qStr = "96kbps";
                info = "低品质";
                break;
        }
    }

    public String getFileName(String mmid) {
        return String.format("%s%s.%s", getCode(), mmid, getFormat());
    }

    public String getFormatName(){
        return String.format("%s.%s", getCode(), getFormat());
    }

    public String getCode() {
        return code;
    }

    public String getFormat() {
        return format;
    }

    public MusicQuality getQuality() {
        return quality;
    }

    public void setQuality(MusicQuality quality) {
        this.quality = quality;
    }


    public String getInfo() {
        return info;
    }

    public String getQualityStr() {
        return qStr;
    }
}
