package com.qmd.jzen.player;

import android.net.Uri;

public class NowPlayingMetadata {
    private String ID;
    private Uri MediaUri;
    private String Subtitle;
    private String TITLE;
    private Uri ICON_URL;
    private long Duration;


    public NowPlayingMetadata(String id, Uri url, String title, String subtitle, Uri iconUrl, long duration) {
        ID = id;
        MediaUri = url;
        TITLE = title;
        ICON_URL = iconUrl;
        Subtitle = subtitle;
        Duration = duration;
    }

    //%d:%02d
    public static String timestampToMSS(long position) {
        int totalSeconds = (int) Math.floor(position / 1E3);
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds - (minutes * 60);
        if (position < 0) {
            return "--:--";
        } else {
            return String.format("%d:%02d", minutes, remainingSeconds);
        }
    }

    public String getID() {
        return ID;
    }

    public Uri getMediaUri() {
        return MediaUri;
    }

    public String getSinger() {
        return Subtitle;
    }

    public String getTitle() {
        return TITLE;
    }

    public long getDuration() {
        return Duration;
    }

    public Uri getICONUrl() {
        return ICON_URL;
    }
}