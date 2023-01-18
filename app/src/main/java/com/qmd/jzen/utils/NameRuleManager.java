package com.qmd.jzen.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameRuleManager {

    public static String getFileName(String singer, String title) {
        if (singer.length() > 80) {
            // 防止歌手过多，导致文件名过长
            Pattern pattern = Pattern.compile("/.+");
            Matcher matcher = pattern.matcher(singer);
            // 替换为 “歌手 等”
            singer = matcher.replaceFirst("等");
        }
        if (title.length() > 50) {
            title = title.substring(0, 10) + "_";
        }

        String fileName = "";
        switch (Config.INSTANCE.getNameRule()) {
            case 0:
                fileName = singer + " - " + title;
                break;
            case 1:
                fileName = title + " - " + singer;
                break;
            case 2:
                fileName = title;
                break;
            default:
                fileName = singer + " - " + title;
        }
        // 替换斜杠为下划线
        fileName = fileName.replace('/', '_').replace('\\','_');
        fileName = fileName.replace(':', '：').replace('*', '＊')
        .replace('|', '｜').replace('<','(').replace('>',')')
        .replace('?', '？').replace('"','“');
        return fileName;
    }
}
