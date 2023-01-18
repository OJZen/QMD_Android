package com.qmd.jzen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.qmd.jzen.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 君子 on 2018/1/16.
 */

public class DownLoadedAdapter extends BaseAdapter {
    List<DownloadEntity> entityList;
    Context mContext;

    public DownLoadedAdapter(Context context) {
        mContext = context;
        entityList = Aria.download(mContext).getAllCompleteTask();

        // 空处理
        if (entityList == null) {
            entityList = new ArrayList<>();
        }

        Collections.reverse(entityList);
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

    public void removeAll() {
        entityList = new ArrayList<>();
        notifyDataSetChanged();
    }

    // 添加一行数据并刷新
    public void update() {
        entityList = Aria.download(mContext).getAllCompleteTask();

        // 空处理
        if (entityList == null) {
            entityList = new ArrayList<>();
        }
        Collections.reverse(entityList);

        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_downloaded, parent, false);
            holder = new ViewHolder();
            holder.text_title = (TextView) convertView.findViewById(R.id.text_title_p2);
            holder.text_filesize = (TextView) convertView.findViewById(R.id.text_filesize_p2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text_title.setText(entityList.get(position).getFileName());
        holder.text_filesize.setText(convertSize(entityList.get(position).getFileSize()));
        return convertView;
    }

    static class ViewHolder {
        TextView text_title;
        TextView text_filesize;
    }

    // 转换大小
    public String convertSize(long filesize) {
        int gb = 1024 * 1024 * 1024;
        int mb = 1024 * 1024;
        int kb = 1024;
        if (filesize >= gb) {
            double sp = filesize * 1.0 / gb;
            return String.format("%.2f", sp) + "GB";
        } else if (filesize >= mb) {
            double sp = filesize * 1.0 / mb;
            return String.format("%.2f", sp) + "MB";
        } else if (filesize >= kb) {
            double sp = filesize * 1.0 / kb;
            return String.format("%.2f", sp) + "KB";
        } else {
            return filesize + "b";
        }
    }
}
