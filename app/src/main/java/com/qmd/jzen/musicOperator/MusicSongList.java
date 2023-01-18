package com.qmd.jzen.musicOperator;

import com.orhanobut.logger.Logger;
import com.qmd.jzen.database.entity.SongListInfo;
import com.qmd.jzen.entity.Cookie;
import com.qmd.jzen.network.HttpManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MusicSongList {
    private String Url = "https://c.y.qq.com/qzone/fcg-bin/fcg_ucc_getcdinfo_byids_cp.fcg?type=1&onlysong=0&disstid=%s&loginUin=%s&platform=yqq.json&format=json&new_format=1&utf8=1";
    private Long ID;         // 关键字
    private String url;

    public MusicSongList(Long id) {
        this.ID = id;
        url = String.format(Url, ID, Cookie.getQQ());
    }

    public MusicSongList() {
    }

    public void setID(Long id) {
        this.ID = id;
        url = String.format(Url, id, Cookie.getQQ());
    }

    public SongListInfo getSongList() {
        String jsonData = getJsonData();
        if (jsonData.contains("\"cdlist\":[]")) {
            return null;
        }
        try {
            // json根
            JSONObject rootJsonObject = new JSONObject(jsonData);
            // 得到list下的集合 第一个
            JSONObject songListObject = rootJsonObject.getJSONArray("cdlist").getJSONObject(0);
            SongListInfo songList = new SongListInfo(
                    ID,
                    songListObject.getString("dissname"),
                    songListObject.getString("desc"),
                    songListObject.getString("logo"),
                    songListObject.getString("nickname"),
                    songListObject.getInt("total_song_num"),
                    System.currentTimeMillis()
            );
            return songList;
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
            return null;
        }
    }

    public ArrayList<SongListInfo> getSongListWithIDList(List<String> idList) {
        ArrayList<SongListInfo> songLists = new ArrayList<>();

        if (idList.size() == 0) {
            return songLists;
        }

        for (int i = 0; i < idList.size(); i++) {
            setID(Long.parseLong(idList.get(i)));
            SongListInfo list = getSongList();
            if (list != null) {
                songLists.add(list);
            }
        }
        return songLists;
    }

    private String getJsonData() {
        HttpManager manager = new HttpManager(url);
        return manager.getTextData();
    }
}
