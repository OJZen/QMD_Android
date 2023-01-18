package com.qmd.jzen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.qmd.jzen.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 君子 on 2018/1/16.
 */

public class DownLoadingAdapter extends BaseAdapter {
    List<DownloadEntity> entityList;
    Context mContext;

    public DownLoadingAdapter(Context context) {
        mContext = context;
        update();
    }

    @Override
    public int getCount() {
        return entityList.size();
    }

    @Override
    public Object getItem(int i) {
        return entityList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // 更新数据
    public void update(DownloadEntity entity) {
        if (entity == null) return;
        List<DownloadEntity> entityListNew = Aria.download(mContext).getAllNotCompleteTask();

        if (entityList == null || entityListNew == null)
        {
            entityList = new ArrayList<>();
            notifyDataSetChanged();
            return;
        }

        if (entityListNew.size() != entityList.size()) {
            entityList = entityListNew;
            notifyDataSetChanged();
            return;
        }

        for (int i = 0; i < entityList.size(); i++) {
            if (entity.getId() == entityList.get(i).getId()) {
                if (entity.isComplete()) {
                    entityList.remove(i);
                } else {
                    entityList.set(i, entity);
                }
            }
        }
        notifyDataSetChanged();
    }

    // 更新,重查所有
    public void update() {
        entityList = Aria.download(mContext).getAllNotCompleteTask();
        // 空处理
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloading, parent, false);
            holder = new ViewHolder();
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress_downloading);
            holder.text_progress = (TextView) convertView.findViewById(R.id.text_progress);
            holder.text_title = (TextView) convertView.findViewById(R.id.item_playList_text_title);
            holder.text_state = (TextView) convertView.findViewById(R.id.text_status);
            holder.text_speed = (TextView) convertView.findViewById(R.id.text_speed);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DownloadEntity downloadEntity = entityList.get(position);

        holder.text_title.setText(downloadEntity.getFileName());                        // 标题
        holder.text_state.setText(getState(downloadEntity.getState()));                 //状态
        holder.text_speed.setText(convertSpeed(downloadEntity.getSpeed()));             // 速度
        holder.text_progress.setText(convertProgress(downloadEntity.getCurrentProgress(), downloadEntity.getFileSize())); //进度
        holder.progressBar.setProgress(intProgress(downloadEntity.getCurrentProgress(), downloadEntity.getFileSize()));

        return convertView;
    }

    static class ViewHolder {
        TextView text_title;
        TextView text_state;
        TextView text_speed;
        TextView text_progress;
        ProgressBar progressBar;
    }

    // 进度
    private int intProgress(long progress, long filesize) {
        double percent = progress * 1.0 / filesize * 100;
        return (int) percent;
    }

    // 转换百分比
    private String convertProgress(long progress, long filesize) {
        double percent = progress * 1.0 / filesize * 100;
        if (Float.isNaN((float) percent)) {
            percent = 0;
        }
        return String.format("%.2f", percent) + "%";
    }

    //0：失败；1：完成；2：停止；3：等待；
    //4：正在执行；5：预处理；6：预处理完成；7：取消任务
    private String getState(int state) {
        String mes = "";
        switch (state) {
            case 0:
                mes = "下载失败";
                break;
            case 1:
                mes = "下载完成";
                break;
            case 2:
                mes = "暂停下载";
                break;
            case 3:
                mes = "等待下载";
                break;
            case 4:
                mes = "正在下载";
                break;
        }
        return mes;
    }

    // 速度转换
    private String convertSpeed(long speed) {
        int gb = 1024 * 1024 * 1024;
        int mb = 1024 * 1024;
        int kb = 1024;
        if (speed >= gb) {
            double sp = speed * 1.0 / gb;
            return String.format("%.2f", sp) + "GB/S";
        } else if (speed >= mb) {
            double sp = speed * 1.0 / mb;
            return String.format("%.2f", sp) + "MB/S";
        } else if (speed >= kb) {
            double sp = speed * 1.0 / kb;
            return String.format("%.2f", sp) + "KB/S";
        } else {
            return "0KB/S";
        }
    }
}
