package com.qmd.jzen.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadTask;
import com.orhanobut.logger.Logger;
import com.qmd.jzen.R;
import com.qmd.jzen.ui.activity.DownloadActivity;
import com.qmd.jzen.utils.Config;
import com.qmd.jzen.utils.Toaster;

import java.io.File;

/**
 * 用于下载通知的服务
 */
public class DownloadNotiService extends Service {

    NotificationManager manager;
    String channelId = "Notification";

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("！服务启动！");
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(getResources().getString(R.string.notification_download_channel),
                    getResources().getString(R.string.notification_download_name), NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
            // Notification notification = new Notification.Builder(this, getResources().getString(R.string.notification_download_channel)).build();
            // startForeground(1, notification);
        }
        Aria.download(this).register();
    }

    @Download.onTaskComplete
    void taskComplete(DownloadTask task) {
        if (Config.INSTANCE.getNotification()) {
            notification("下载完成", task.getEntity().getFileName());

            // 广播更新文件
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(task.getEntity().getFileName())));
            sendBroadcast(intent);

        } else {
            Toaster.Companion.out("下载完成: " + task.getDownloadEntity().getFileName());
        }
    }

    @Download.onTaskFail
    void taskFail(DownloadTask task) {
        if (task == null) {
            Toaster.Companion.out("内部错误，请在设置页面尝试重置下载器。还是不行的话就卸载重装或者清除应用数据。");
            Aria.download(this).removeAllTask(false);
            return;
        }
        DownloadEntity entity = task.getDownloadEntity();
        if (Config.INSTANCE.getNotification()) {
            notification("下载失败", entity.getFileName());
        } else {
            Toaster.Companion.out("下载失败: " + entity.getFileName());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
        Logger.i("！服务死啦！！");
    }

    public void notification(String title, String text) {
        Intent intent = new Intent(this, DownloadActivity.class);
        if (!title.contains("失败")) {
            intent.putExtra("page", "downloaded");
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        // 判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_download)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent).build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setShowWhen(true)
                    .setSmallIcon(R.drawable.ic_download)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pendingIntent).build();
        }
        manager.notify(1, notification);
    }
}
