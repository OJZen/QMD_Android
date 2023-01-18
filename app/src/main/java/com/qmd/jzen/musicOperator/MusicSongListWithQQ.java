package com.qmd.jzen.musicOperator;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.qmd.jzen.network.HttpManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MusicSongListWithQQ {
    Long QQ;
    String Url_HomePage = "https://c.y.qq.com/rsc/fcgi-bin/fcg_get_profile_homepage.fcg?needNewCode=1&cid=205360838&ct=20&cv=1777&userid=%s&reqfrom=1&reqtype=0";
    String Url_Created = "https://c.y.qq.com/rsc/fcgi-bin/fcg_user_created_diss?hostuin=%s&size=100&format=json&platform=yqq.json&utf8=1";
    String url_c;       // 获取用户创建的歌单，不包含“我喜欢”
    String url_h;       // 获取用户主页，包含所有歌单

    public MusicSongListWithQQ(Long qq) {
        QQ = qq;
        url_c = String.format(Url_Created, qq);
    }

    public List<String> getSongListID() {
        HttpManager httpManager = new HttpManager(url_c);
        String rawData = httpManager.getTextData();
        String encrypt_uin = "";
        List<String> listID = new ArrayList<>();

        // 先获取用户创建的歌单，这一步不需要cookie，还需要拿到加密的账号
        try {
            // json根
            JSONObject rootJsonObject = new JSONObject(rawData).getJSONObject("data");
            encrypt_uin = rootJsonObject.getString("encrypt_uin");
            JSONArray disslist = rootJsonObject.getJSONArray("disslist");
            // 添加了所有用户新建的歌单
            for (int i = 0; i < disslist.length(); i++) {
                String id = disslist.getJSONObject(i).getString("tid");
                if (!TextUtils.isEmpty(id)) {
                    listID.add(id);
                }
            }
        } catch (Exception ex) {
            Logger.e(Objects.requireNonNull(ex.getMessage()));
        }

        if (TextUtils.isEmpty(encrypt_uin)) {
            return listID;
        }

        // 获取我喜欢的歌单
        url_h = String.format(Url_HomePage, encrypt_uin);
        httpManager = new HttpManager(url_h);
        rawData = httpManager.getTextData();

        Logger.i(rawData);
        try {
            // json根
            JSONObject rootJsonObject = new JSONObject(rawData);
            int code = rootJsonObject.getInt("code");
            if (code != 0) {
                // 重新获取我喜欢的歌单
                url_h = String.format(Url_HomePage, QQ);
                httpManager = new HttpManager(url_h);
                rawData = httpManager.getTextData();
                rootJsonObject = new JSONObject(rawData);
            }
            JSONObject dataJson = rootJsonObject.getJSONObject("data");
            JSONArray myMusic = dataJson.getJSONArray("mymusic");
            for (int i = 0; i < myMusic.length(); i++) {
                JSONObject favorite = myMusic.getJSONObject(i);
                String id = favorite.getString("id");
                if (id.equals("0") || id.length() < 5) {
                    continue;
                }
                listID.add(0, id);
            }
        } catch (Exception ex) {
            Logger.e(Objects.requireNonNull(ex.getMessage()));
        }
        return listID;
    }

}
