package com.qmd.jzen.utils;

import android.text.TextUtils;
import android.util.Base64;

import com.qmd.jzen.entity.Cookie;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptAndDecrypt {

    private static String GetFullNumber(int num) {
        if (num < 10) {
            return "00" + num;
        } else if (num < 100) {
            return "0" + num;
        }
        return num + "";
    }

    private static char[] reverse(char[] clist) {
        //遍历数组
        for (int i = 0; i < clist.length / 2; i++) {
            //交换元素 因为i从0开始所以这里一定要再减去1
            char temp = clist[clist.length - i - 1];
            clist[clist.length - i - 1] = clist[i];
            clist[i] = temp;
        }
        //返回反转后的结果
        return clist;
    }

    private static String arrToStr(char[] cArr) {
        String str = "";
        for (int i = 0; i < cArr.length; i++) {
            str += cArr[i];
        }
        return str;
    }

    private static String listToStr(List<Character> cList) {
        String str = "";
        for (int i = 0; i < cList.size(); i++) {
            str += cList.get(i);
        }
        return str;
    }

    public static String encryptDES(String text, String key) {
        if (text == null || key == null)
            return null;
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE,
                    new SecretKeySpec(key.getBytes(), "DES"),
                    new IvParameterSpec(key.getBytes()));
            byte[] bytes = cipher.doFinal(text.getBytes());
            return Base64.encodeToString(bytes, Base64.DEFAULT).trim();
            //return java.util.Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String decryptDES(String text, String key) {
        if (text == null || key == null)
            return null;
        try {
            //byte[] bytes = java.util.Base64.getDecoder().decode(text);
            byte[] bytes = Base64.decode(text, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE,
                    new SecretKeySpec(key.getBytes(), "DES"),
                    new IvParameterSpec(key.getBytes()));
            bytes = cipher.doFinal(bytes);
            return new String(bytes, "utf-8");
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * 解码cookie并且设置全局cookie
     * @param enStr cookie字符串
     * @return
     */
    public static boolean decryptAndSetCookie(String enStr) {
        String text = enStr.replace("-", "").replace("|", "");
        if (text.length() < 10 || !text.contains("%")) {
            return false;
        }
        String[] textS = text.split("%");
        String ekey = textS[0];
        String eqq = textS[1];

        String qq = decryptDES(eqq, ekey.substring(0, 8));
        if (qq.length() < 8) {
            qq += "QMD";
        }
        String key = decryptDES(ekey, qq.substring(0, 8));
        // 设置cookie
        Cookie.setCookie(key, qq);

        return true;
    }

    public static String encryptText(String text, String qq) {

        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(qq)) {
            return "";
        }

        qq = "QMD" + qq;

        String result = encryptDES(text, qq.substring(0, 8));
        StringBuilder builder = new StringBuilder(result);

        Random r = new Random(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        int times = r.nextInt(4) + 1;
        for (int i = 0; i < times; i++) {
            int rdPos = r.nextInt(builder.length());
            builder.insert(rdPos, "-");
        }
        return builder.toString();
    }

    public static String encryptText(String text){
        return encryptText(text, Cookie.getQQ());
    }

    public static String decryptText(String text, String qq) {
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(qq)) {
            return "";
        }
        qq = "QMD" + qq;

        text = text.replace("-", "");
        return decryptDES(text, qq.substring(0, 8));
    }

    public static String decryptText(String text){
        return decryptText(text, Cookie.getQQ());
    }
}
