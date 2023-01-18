package com.qmd.jzen.ui.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadTask;
import com.orhanobut.logger.Logger;
import com.qmd.jzen.R;
import com.qmd.jzen.adapters.DownLoadedAdapter;

import java.io.File;


public class DownloadedFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    DownLoadedAdapter adapter;
    Context mContext;

    public DownloadedFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        initView(view);
        return view;
    }

    // 初始化
    void initView(View view) {
        mContext = requireActivity();
        Aria.download(this).register();
        // 获取所有数据
        ListView listview_downlaoded = view.findViewById(R.id.listview_downloaded);
        // 设置适配器
        adapter = new DownLoadedAdapter(mContext);
        listview_downlaoded.setAdapter(adapter);
        listview_downlaoded.setOnItemLongClickListener(this);
        listview_downlaoded.setOnItemClickListener(this);
    }

    public void removeAll() {
        if (adapter == null) return;
        adapter.removeAll();
    }

    @Download.onTaskComplete
    void complete(DownloadTask task) {
        // 当下载完成时，给列表添加一条数据，并刷新
        adapter.update();
    }

    @Download.onTaskCancel
    void onTaskCancel(DownloadTask task) {
        adapter.update();
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position, long l) {
        String[] musicOptions = {"删除", "歌曲信息"};
        int taskNum = Aria.download(mContext).getAllCompleteTask().size();
        // 反转过来
        final DownloadEntity entity = Aria.download(mContext).getAllCompleteTask().get(taskNum - position - 1);
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        // 设置列表框的内容
        // 列表框子项点击事件
        dialog.setTitle(entity.getFileName())    //标题
                .setIcon(R.drawable.music)   //图标
                .setItems(musicOptions, (dialogInterface, which) -> {
                    switch (which) {
                        case 0:
                            // 删除按钮
                            showDelDialog(entity);
                            break;
                        case 1:
                            // 歌曲信息
                            showInfoDialog(entity);
                            break;
                    }
                });

        dialog.create().show();
        return true;
    }

    // 歌曲删除询问对话框
    private void showDelDialog(final DownloadEntity entity) {
        new AlertDialog.Builder(mContext)
                .setTitle(entity.getFileName())
                .setIcon(R.drawable.ic_baseline_help_outline_24)
                .setMessage("你确认要删除?")
                .setPositiveButton("删除", (dialogInterface, i) -> {
                    // 删除listview的数据
                    Aria.download(requireContext()).load(entity.getId()).cancel(true);
                    String path = entity.getFilePath();
                    // 删除歌词文件
                    showDelLrcDialog(path.substring(0, path.length() - 4));
                })
                .setNegativeButton("取消", null).show();
    }

    // 歌曲信息对话框
    private void showInfoDialog(DownloadEntity entity) {
        AlertDialog.Builder dialogInfo = new AlertDialog.Builder(mContext);
        dialogInfo.setTitle("歌曲信息：")
                .setIcon(R.drawable.music_info)
                .setMessage("歌名：" + entity.getFileName() +
                        "\n大小：" + adapter.convertSize(entity.getFileSize()) +
                        "\n位置：" + entity.getFilePath()
                )
                .setPositiveButton("确认", null).show();
    }

    // 删除歌词对话框
    private void showDelLrcDialog(String path) {
        final File file = new File(path + ".lrc");
        if (!file.exists()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("检测到有歌词文件:")
                .setMessage("是否删除歌词文件?\n" + file.getAbsolutePath()).setIcon(R.drawable.ic_baseline_help_outline_24)
                .setPositiveButton("删除", (dialog, which) -> {
                    if (!file.delete()) {
                        Toast.makeText(requireContext(), "文件删除失败，可能已被删除或者移走。", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int taskNum = Aria.download(mContext).getAllCompleteTask().size();
        // 反转过来
        final DownloadEntity entity = Aria.download(mContext).getAllCompleteTask().get(taskNum - position - 1);
        // 如果是歌词文件就不触发
        if (entity.getFilePath().endsWith(".lrc")) {
            return;
        }

        Intent mIntent = new Intent(Intent.ACTION_VIEW);
        File file = new File(entity.getFilePath());
        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mContext, "com.qmd.jzen.provider", file);
            mIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(file);
        }

        Logger.e(file.getPath());
        Logger.e(uri.toString());

        mIntent.setDataAndType(uri, "audio/*");

        try {
            mContext.startActivity(mIntent);
            Toast.makeText(mContext, "这里仅提供试听功能，建议您现在到您的音乐APP里面进行扫描和播放。", Toast.LENGTH_LONG).show();
        } catch (ActivityNotFoundException aex) {
            Toast.makeText(mContext, "没有可以打开音乐的程序", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(mContext, "发生错误：" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }
}
