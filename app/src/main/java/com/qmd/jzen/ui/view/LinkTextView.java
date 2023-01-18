package com.qmd.jzen.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.qmd.jzen.R;
import com.qmd.jzen.utils.ThemeColorManager;

public class LinkTextView extends androidx.appcompat.widget.AppCompatTextView {

    public LinkTextView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LinkTextView);
        String link = typedArray.getString(R.styleable.LinkTextView_link);

        // 设置颜色
        setTextColor(ThemeColorManager.getConfigColor(context));
        // 设置下划线，抗拒齿
        getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

        // 设置点击监听
        setOnClickListener(v -> {
            Uri uri;
            // 判断协议
            if (link.startsWith("https://")) {
                uri = Uri.parse(link);
            } else {
                uri = Uri.parse("https://" + link);
            }
            // 打开链接
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        });
        typedArray.recycle();
    }

}
