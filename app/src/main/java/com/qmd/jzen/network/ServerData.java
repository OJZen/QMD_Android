package com.qmd.jzen.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.qmd.jzen.database.entity.Music;
import com.qmd.jzen.entity.Download;
import com.qmd.jzen.entity.MusicLink;
import com.qmd.jzen.entity.PlayRecord;
import com.qmd.jzen.entity.SongListCounting;
import com.qmd.jzen.utils.EncryptAndDecrypt;
import com.qmd.jzen.utils.SystemInfoUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

public class ServerData {
    private final String host = "http://127.0.0.1/";
    private static ServerData obj = null;

    public ServerData() {
    }

    public static ServerData getSingle() {
        if (obj == null) {
            obj = new ServerData();
        }
        return obj;
    }

    public boolean getCookie() {
        HttpManager manager = new HttpManager(host + "Cookies");
        // 获取数据
        Gson gson = new Gson();
        String json = gson.toJson(SystemInfoUtil.getDeviceInfo());
        String data = manager.postDataWithResult(json);
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        return EncryptAndDecrypt.decryptAndSetCookie(data);
    }

    public String getNotification() {
        HttpManager manager = new HttpManager(host + "Notifications");
        // 获取数据
        String data = manager.getTextData();
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        return data;
    }

    public void setMusicLinkList(List<MusicLink> linkList) {
        if (linkList == null) return;
        JSONArray array = new JSONArray();
        for (int i = 0; i < linkList.size(); i++) {
            MusicLink link = linkList.get(i);
            try {
                JSONObject root = new JSONObject();
                root.put("midQuality", link.getQuality() + link.getSongmid());
                root.put("quality", link.getQuality());
                root.put("link", link.getLink());
                array.put(root);
            } catch (Exception ex) {
                Logger.e(ex.getMessage());
            }
        }
        if (array.length() <= 0) {
            return;
        }
        String data = array.toString();
        HttpManager httpManager = new HttpManager(host + "MusicLink/list");
        Logger.e(data);
        httpManager.postData(data);
    }

    public void setMusicLink(MusicLink musicLink) {
        try {
            JSONObject root = new JSONObject();
            root.put("filename", musicLink.getFilename());
            root.put("songmid", musicLink.getSongmid());
            root.put("quality", musicLink.getQuality());
            root.put("link", musicLink.getLink());
            HttpManager httpManager = new HttpManager(host + "MusicLink");
            String data = root.toString();
            Logger.e("提交到服务器：" + data);
            httpManager.postData(data);
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
        }
    }

    public String getMusicLink(String filename) {
        String str = EncryptAndDecrypt.encryptText(filename);
        HttpManager httpManager = new HttpManager(host + "MusicLink/link");
        String data = httpManager.postDataWithResult("\"" + str + "\"");
        Logger.e(data);
        return data;

        /*
        try {
            JSONObject root = new JSONObject(data);
            // 暂时用不上全部数据
            MusicLink musicLink = new MusicLink();
            musicLink.setFilename(root.getString("filename"));
            musicLink.setSongmid(root.getString("songmid"));
            musicLink.setQuality(root.getString("quality"));
            musicLink.setLink(root.getString("link"));

            return root.getString("link");
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
        }
        return null;
       */
    }

    public void setSearch(String keyword) {
        try {
            JSONObject root = new JSONObject();
            root.put("keyWord", keyword);
            root.put("uid", SystemInfoUtil.getUID());
            String json = root.toString();
            HttpManager http = new HttpManager(host + "Search");
            http.postData(json);
        } catch (Exception ex) {
            Logger.e(ex.toString());
        }
    }

    public void setDownload(Download download) {
        try {
            JSONObject root = new JSONObject();
            root.put("musicid", download.getMusicId());
            root.put("title", download.getTitle());
            root.put("singername", download.getSinger());
            root.put("quality", download.getQuality());
            root.put("uid", SystemInfoUtil.getUID());
            HttpManager httpManager = new HttpManager(host + "Download");
            String data = root.toString();
            Logger.e(data);
            httpManager.postData(data);
        } catch (Exception ex) {
            Logger.e(ex.toString());
        }
    }

    public void setPlayRecord(PlayRecord playRecord) {
        try {
            JSONObject root = new JSONObject();
            root.put("musicID", playRecord.getMusicId());
            root.put("title", playRecord.getTitle());
            root.put("singerName", playRecord.getSinger());
            root.put("uid", SystemInfoUtil.getUID());
            root.put("quality", playRecord.getQuality());
            Logger.e(root.toString());
            HttpManager http = new HttpManager(host + "PlayRecords");
            http.postData(root.toString());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public void setSongListCounting(SongListCounting counting) {
        try {
            JSONObject root = new JSONObject();
            root.put("songListId", counting.getSongListId());
            root.put("title", counting.getTitle());
            Logger.e(root.toString());
            HttpManager http = new HttpManager(host + "SongListCountings");
            http.postData(root.toString());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public void setFavorites(Music music) {
        try {
            JSONObject root = new JSONObject();
            root.put("musicID", music.getMusicId());
            root.put("uid", SystemInfoUtil.getUID());
            root.put("Title", music.getTitle());
            root.put("SingerName", music.getSinger());
            Logger.e(root.toString());
            HttpManager http = new HttpManager(host + "Favorites");
            http.postData(root.toString());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
    }

    public int getFavoritesCounting(String mid) {
        HttpManager httpManager = new HttpManager(host + "Favorites/" + mid);
        String data = httpManager.getTextData();
        if (TextUtils.isEmpty(data)) {
            return -1;
        }
        Logger.e(data);
        try {
            JSONObject root = new JSONObject(data);
            if (mid.equals(root.getString("MusicID"))) {
                return root.getInt("Counting");
            }
        } catch (Exception e) {
            Logger.e(Objects.requireNonNull(e.getMessage()));
        }
        return -1;
    }

    public String getSearchData(String keyWord, int page, int num) {
        try {
            JSONObject root = new JSONObject();
            root.put("keyword", keyWord);
            root.put("page", page);
            root.put("num", num);
            Logger.e(root.toString());
            HttpManager http = new HttpManager(host + "Search/Result");
            return http.postDataWithResult(root.toString());
        } catch (Exception e) {
            Logger.e(Objects.requireNonNull(e.getMessage()));
            return "";
        }
    }

}
