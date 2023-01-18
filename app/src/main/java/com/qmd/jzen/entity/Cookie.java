package com.qmd.jzen.entity;

/**
 * Create by OJun on 2021/2/23.
 */
public class Cookie {
    private static String Mkey;
    private static String QQ;

    public static String getMkey() {
        return Mkey;
    }

    public static String getQQ() {
        return QQ;
    }

    public static void setCookie(String key, String qq){
        Mkey = key;
        QQ = qq;
    }

    public static String getCookie(){
        return String.format("qqmusic_key=%s;qqmusic_uin=%s;", Mkey, QQ);
    }

}
