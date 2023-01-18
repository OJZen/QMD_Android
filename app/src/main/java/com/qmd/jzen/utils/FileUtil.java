package com.qmd.jzen.utils;

import static android.content.Context.STORAGE_SERVICE;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import kotlin.text.Regex;

/**
 * Created by junzi on 2017/12/25.
 */

public class FileUtil {
    static boolean sdCard = false;

    // 检查外置储存的状态
    public static void initialStorage() {
        sdCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCard) {
            File dir = Environment.getExternalStorageDirectory();
            String path = dir.getAbsolutePath();
            File qmdDir = new File(path + "/Music");

            if (!qmdDir.exists()) {
                qmdDir.mkdir();
            }
        }
    }

    public static String getStoragePath() {
        String path = null;
        if (sdCard) {
            File dir = Environment.getExternalStorageDirectory();
            path = dir.getAbsolutePath() + "/Music";
        }
        return path;
    }

    // 判断文件是否存在
    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }


    /**
     * 删除文件
     *
     * @param path 路径
     * @return
     */
    public static boolean deleteFile(String path) {
        File file = new File(path);
        return file.delete();
    }

    /**
     * 新建目录
     *
     * @param path 路径
     */
    public static void makeDirectory(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // 获取SD卡路径
    public static String[] getStoragePaths(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", new Class<?>[]{});
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, new Object[]{});
            return paths;
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
        }
        return null;
    }

    public static boolean saveTextFile(String text, String fileName) {
        try {
            File file = new File(fileName);
            FileOutputStream outputStream = new FileOutputStream(file);
//            if (containChinese(text)) {
//                outputStream.write(text.getBytes("GBK"));
//            } else {
//                outputStream.write(text.getBytes());
//            }
            // TODO: 做一个设置选项，用于保存某种编码
            outputStream.write(text.getBytes());
            outputStream.close();
            return true;
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
            return false;
        }
    }

    private static boolean containChinese(String text) {
        Regex regex = new Regex("[\\u4e00-\\u9fa5]");
        return regex.containsMatchIn(text);
    }

    public static boolean saveImageFile(Bitmap bitmap, String fileName) {
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

            Uri external = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver resolver = QMDApplication.getContext().getContentResolver();
            Uri insertUri = resolver.insert(external, values);
            if (insertUri != null) {
                try (OutputStream os = resolver.openOutputStream(insertUri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    return true;
                } catch (IOException e) {
                    Logger.e(e.getMessage());
                }
            }
            return false;
        } else {

        }

         */
        String path = Config.INSTANCE.getDownloadImagePath();

        if (!FileUtil.fileExists(path)) {
            FileUtil.makeDirectory(path);
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(path + fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
            return false;
        }
    }

    // 单位转换
    public static String convertSize(long size) {
        int gb = 1024 * 1024 * 1024;
        int mb = 1024 * 1024;
        int kb = 1024;
        if (size >= gb) {
            double sp = size * 1.0 / gb;
            return String.format("%.2f", sp).toString() + "GB";
        } else if (size >= mb) {
            double sp = size * 1.0 / mb;
            return String.format("%.2f", sp).toString() + "MB";
        } else if (size >= kb) {
            double sp = size * 1.0 / kb;
            return String.format("%.2f", sp).toString() + "KB";
        } else {
            return "0KB";
        }
    }

}
