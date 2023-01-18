package com.qmd.jzen.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qmd.jzen.api.response.Notification;

import java.util.Objects;

/**
 * 要推送的目标，以数字版本号确认目标
 * 有两种方式：
 * 1. 范围： 30->40   (推送到版本号30到40的应用，第一个要小于第二个数)
 * 2. 单个或多个： 50,52,55 （推送到版本号为50,52,55的应用，可以只有一个版本号）
 * 3. 空或者null表示全部
 */

public class NotificationManager {
    final int DIALOG_EVEYTIME_CANNOTCLOSE = 14;      //弹窗通知，每次都会提示，不能关闭
    final int DIALOG_EVEYTIME_CANCLOSE = 13;         //弹窗通知，每次都会提示，可以关闭
    final int DIALOG_OPTION = 12;                   //弹窗通知，可选择不再提示
    final int DIALOG_ONECE = 11;                    //弹窗通知，只显示一次
    final int TOASET_ONCE = 1;                      // 吐司通知 一次
    final int TOASET_EVEYTIME = 2;                  // 土司通知 每一次
    final int NONE = 0;                         //不通知

    Notification notification;
    Context mContext;

    public NotificationManager(Context context, Notification notification) {
        mContext = context;
        this.notification = notification;
    }

    /**
     * 用target判断是否需要执行通知
     *
     * @return
     */
    private boolean isNotify() {
        int versionCode = SystemInfoUtil.getAppVersionCode();
        try {
             String target = notification.getTarget();
            if (target.contains("->")) {
                String[] data = target.split("->");
                int startCode = Integer.parseInt(data[0]);
                int endCode = Integer.parseInt(data[1]);
                if (versionCode >= startCode && versionCode <= endCode) {
                    return true;
                }
            }
            if (target.contains(",")) {
                String[] data = target.split(",");
                for (String datum : data) {
                    int code = Integer.parseInt(datum);
                    if (versionCode == code) {
                        return true;
                    }
                }
            }
            if (TextUtils.isEmpty(target)) {
                return true;
            }
            int code = Integer.parseInt(target);
            if (code == versionCode) {
                return true;
            }
        } catch (Exception e) {
            Logger.e(Objects.requireNonNull(e.getMessage()));
        }
        return false;
    }

    // 通知
    public void Notify() {
        if (notification.getTitle().isEmpty() && notification.getContent().isEmpty()){
            return;
        }

        if (!isNotify()) {
            return;
        }

        int id = notification.getId();
        String title = notification.getTitle();
        String content = notification.getContent();

        // 获取本地序号
        int noNum = Config.INSTANCE.getNotifyNumber();
        switch (notification.getType()) {
            case DIALOG_OPTION:
                // 本地的序号比服务器的序号小的话就提示
                if (noNum < id) {
                    QMDDialog.Companion.showBaseDialog(mContext, title, content, "不再提示", "关闭",
                            dialog -> {
                                // 设置本地序号
                                Config.INSTANCE.setNotifyNumber(id);
                            });
                }
                break;
            case DIALOG_ONECE:
                // 本地的序号比服务器的序号小的话就提示
                if (noNum < id) {
                    QMDDialog.Companion.showBaseDialog(mContext, title, content,
                            dialog -> {
                                // 设置本地序号
                                Config.INSTANCE.setNotifyNumber(id);
                            });
                    Config.INSTANCE.setNotifyNumber(id);
                }
                break;
            case DIALOG_EVEYTIME_CANCLOSE:
                QMDDialog.Companion.showBaseDialog(mContext, title, content);
                break;
            case DIALOG_EVEYTIME_CANNOTCLOSE:
                // 关闭不了的窗口
                break;
            case TOASET_ONCE:
                if (noNum < id) {
                    Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                }
                break;
            case TOASET_EVEYTIME:
                Toast.makeText(mContext, content, Toast.LENGTH_SHORT).show();
                break;
            case NONE:
                break;
        }

    }
}
