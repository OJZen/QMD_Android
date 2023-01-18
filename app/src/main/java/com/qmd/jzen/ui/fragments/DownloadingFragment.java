package com.qmd.jzen.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.arialyy.annotations.Download;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.arialyy.aria.core.task.DownloadTask;
import com.qmd.jzen.R;
import com.qmd.jzen.adapters.DownLoadingAdapter;
import com.qmd.jzen.utils.FileUtil;
import com.qmd.jzen.utils.Toaster;

import java.util.List;

public class DownloadingFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    ListView listview_downloading;
    DownLoadingAdapter adapter;
    Context mContext;

    public DownloadingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_downloading, container, false);
        initView(v);
        return v;
    }

    void initView(View view) {
        mContext = requireActivity();
        Aria.download(DownloadingFragment.this).register();
        listview_downloading = view.findViewById(R.id.listview_downloading);
        adapter = new DownLoadingAdapter(mContext);
        listview_downloading.setAdapter(adapter);
        listview_downloading.setOnItemLongClickListener(this);
        listview_downloading.setOnItemClickListener(this);
    }

    public void notifyData() {
        if (adapter == null) return;
        adapter.update();
    }

    @Download.onTaskRunning
    public void running(DownloadTask task) {
        adapter.update(task.getEntity());
    }

    @Download.onTaskComplete
    public void complete(DownloadTask task) {
        adapter.update(task.getEntity());
    }

    @Download.onTaskStop
    public void stop(DownloadTask task) {
        adapter.update(task.getEntity());
    }

    @Download.onTaskCancel
    public void cancel(DownloadTask task) {
        adapter.update();
    }

    @Download.onTaskStart
    public void start(DownloadTask task) {
        adapter.update();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        List<DownloadEntity> entityList = Aria.download(mContext).getAllNotCompleteTask();
        if (entityList == null) return false;
        DownloadEntity entity = entityList.get(position);
        if (entity == null) return false;
        new AlertDialog.Builder(mContext)
                .setTitle(entity.getFileName())
                .setIcon(R.drawable.ic_baseline_help_outline_24)
                .setMessage("您确认要删除此项任务?")
                .setPositiveButton("删除", (dialogInterface, i) -> {
                    // 删除任务, 也删除残留文件
                    Aria.download(DownloadingFragment.this).load(entity.getId()).cancel(true);
                    FileUtil.deleteFile(entity.getFilePath());
                    adapter.update();
                })
                .setNegativeButton("取消", null).show();
        return true;
    }

    //0：失败；1：完成；2：停止；3：等待；
    //4：正在执行；5：预处理；6：预处理完成；7：取消任务
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<DownloadEntity> entityList = Aria.download(mContext).getAllNotCompleteTask();
        if (entityList == null) return;
        DownloadEntity entity = entityList.get(position);
        if (entity == null) return;
        if (entity.getState() == 4) {
            Aria.download(this).load(entity.getId()).stop();
        } else {
            Aria.download(this).load(entity.getId()).resume();
        }
        adapter.update(entity);
    }

    @Download.onTaskFail
    public void taskFail(DownloadTask task) {
        if (task == null) {
            Toaster.Companion.out("内部错误，请在设置页面尝试重置下载器。还是不行的话就卸载重装或者清除应用数据。");
            return;
        }
        adapter.update(task.getDownloadEntity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Aria.download(this).unRegister();
    }
}
