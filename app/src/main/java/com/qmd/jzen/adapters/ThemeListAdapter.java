package com.qmd.jzen.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qmd.jzen.R;
import com.qmd.jzen.utils.Config;
import com.qmd.jzen.utils.ThemeColorManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by OJun on 2020/9/1.
 */
public class ThemeListAdapter extends RecyclerView.Adapter<ThemeListAdapter.ThemeListHolder> {

    List<ThemeAttr> themeList = new ArrayList<>();
    String nowColor;

    Context context;

    public ThemeListAdapter(Context context) {
        this.context = context;
        initColorData(context);
    }

    @NonNull
    @Override
    public ThemeListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 获取item的view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false);
        return new ThemeListHolder(itemView);
    }

    // 初始化一些颜色配置数据
    private void initColorData(Context context) {
        String[] themeColorList = ThemeColorManager.getColorNameList(context);
        String[] themeColorChsList = ThemeColorManager.getColorNameChsList(context);
        nowColor = Config.INSTANCE.getThemeColor();
        for (int i = 0; i < themeColorList.length; i++) {
            ThemeAttr attr = new ThemeAttr(themeColorList[i], themeColorChsList[i]);
            themeList.add(attr);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ThemeListHolder holder, int position) {
        ThemeAttr theme = themeList.get(position);
        holder.setTitle(theme.getText());
        holder.setColor(theme.getColor(context));
        holder.setSelected(nowColor.equals(theme.name));

        holder.itemView.setOnClickListener((view) -> {
            nowColor = theme.name;
            notifyItemRangeChanged(0, getItemCount());
        });
    }

    public String getNowColor() {
        return nowColor;
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    class ThemeListHolder extends RecyclerView.ViewHolder {
        TextView text_title;
        ImageView image_color;
        ImageView image_selected;

        public ThemeListHolder(View itemView) {
            super(itemView);
            image_color = itemView.findViewById(R.id.item_image_color);
            image_selected = itemView.findViewById(R.id.item_image_selected);
            text_title = itemView.findViewById(R.id.item_text_theme_name);
        }

        public void setTitle(String title) {
            text_title.setText(title);
        }

        public void setColor(int color) {
            ColorDrawable drawable = new ColorDrawable(color);
            image_color.setImageDrawable(drawable);
        }

        public void setSelected(boolean isSelected) {
            if (isSelected) {
                image_selected.setVisibility(View.VISIBLE);
            } else {
                image_selected.setVisibility(View.INVISIBLE);
            }
        }

    }

    class ThemeAttr {
        String name;
        String text;

        ThemeAttr(String name, String text) {
            this.name = name;
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getColor(Context context) {
            return ThemeColorManager.getColor(context, name);
        }

    }
}
