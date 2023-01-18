package com.qmd.jzen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmd.jzen.R;
import com.qmd.jzen.entity.LoadUrlEntity;
import com.qmd.jzen.network.MusicDownload;

import java.util.ArrayList;
import java.util.List;


/**
 * Create by OJun on 2021/3/1.
 */
public class UrlLoadingAdapter extends RecyclerView.Adapter<UrlLoadingAdapter.UrlLoadAdapterHolder> {
    List<LoadUrlEntity> loadUrlEntityList;
    Context mContext;

    public UrlLoadingAdapter(Context context) {
        mContext = context;
        update();
    }

    @NonNull
    @Override
    public UrlLoadAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        // 获取item的view
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_urlloading, parent, false);
        return new UrlLoadAdapterHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlLoadAdapterHolder holder, int position) {
        LoadUrlEntity entity = loadUrlEntityList.get(position);
        holder.text_filename.setText(entity.getFilenameWithFormat());
        if (entity.getLoadState() == LoadUrlEntity.FAILURE) {
            int color = mContext.getResources().getColor(R.color.summary);
            holder.text_state.setTextColor(color);
        } else {
            int color = mContext.getResources().getColor(R.color.Caption);
            holder.text_state.setTextColor(color);
        }
        holder.text_state.setText(getStateText(entity.getLoadState()));
    }

    private String getStateText(int state) {
        String mes = "";
        switch (state) {
            case LoadUrlEntity.WAITING:
                mes = "等待";
                break;
            case LoadUrlEntity.LOADING:
                mes = "正在获取资源";
                break;
            case LoadUrlEntity.FAILURE:
                mes = "获取资源失败";
                break;
            case LoadUrlEntity.SUCCESS:
                mes = "完成";
                break;
            case LoadUrlEntity.FILE_EXIST:
                mes = "文件已存在";
                break;
        }
        return mes;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return loadUrlEntityList.size();
    }

    // 更新,重查所有
    public void update() {
        List<LoadUrlEntity> allLoadUrlEntity = MusicDownload.getAllLoadUrlEntity();
        if (allLoadUrlEntity == null) {
            loadUrlEntityList = new ArrayList<>();
        }
        else {
            // 去除所有已完成的项
            List<LoadUrlEntity> remainUrlEntity = new ArrayList<>();
            for (LoadUrlEntity entity : allLoadUrlEntity) {
                if (entity.getLoadState() != LoadUrlEntity.SUCCESS) {
                    remainUrlEntity.add(entity);
                }
            }
            loadUrlEntityList = remainUrlEntity;
        }
        notifyDataSetChanged();
    }

    class UrlLoadAdapterHolder extends RecyclerView.ViewHolder {
        TextView text_filename;
        TextView text_state;

        UrlLoadAdapterHolder(View itemView) {
            super(itemView);
            text_filename = itemView.findViewById(R.id.text_filename);
            text_state = itemView.findViewById(R.id.text_state);
        }
    }
}
