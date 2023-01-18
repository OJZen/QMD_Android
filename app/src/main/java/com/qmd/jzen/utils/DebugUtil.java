package com.qmd.jzen.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.orhanobut.logger.Logger;

public class DebugUtil {
    //判断当前应用是否是debug状态
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ex) {
            Logger.e(ex.getMessage());
            return false;
        }
    }
}
