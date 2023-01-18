package com.qmd.jzen.utils;

import android.app.Activity;
import android.content.Context;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.qmd.jzen.R;

public class ThemeColorManager {

    public static final String RED = "Red";
    public static final String PURPLE = "Purple";
    public static final String BLUE = "Blue";
    public static final String CYAN = "Cyan";
    public static final String GREEN = "Green";
    public static final String BROWN = "Brown";
    public static final String GREY = "Grey";
    public static final String YELLOW = "Yellow";

    public static int getColor(Context context, String colorName) {
        switch (colorName) {
            case PURPLE:
                return ContextCompat.getColor(context, R.color.colorPrimary_Purple);
            case BLUE:
                return ContextCompat.getColor(context, R.color.colorPrimary_Bule);
            case CYAN:
                return ContextCompat.getColor(context, R.color.colorPrimary_Cyan);
            case GREEN:
                return ContextCompat.getColor(context, R.color.colorPrimary_Green);
            case BROWN:
                return ContextCompat.getColor(context, R.color.colorPrimary_Brown);
            case GREY:
                return ContextCompat.getColor(context, R.color.colorPrimary_Grey);
            case YELLOW:
                return ContextCompat.getColor(context, R.color.colorPrimary_Yellow);
            default:
                return ContextCompat.getColor(context, R.color.colorPrimary);
        }
    }

    public static String getColorNameChs(Context context, String colorName) {
        String[] colorNameList = getColorNameList(context);
        String[] colorChsList = getColorNameChsList(context);
        for (int i = 0; i < colorNameList.length; i++) {
            if (colorName.equals(colorNameList[i])) {
                return colorChsList[i];
            }
        }
        return colorChsList[0];
    }

    public static String[] getColorNameList(Context context) {
        return context.getResources().getStringArray(R.array.theme_color);
    }

    public static String[] getColorNameChsList(Context context) {
        return context.getResources().getStringArray(R.array.theme_color_chs);
    }

    public static int getStyle(String colorName) {
        switch (colorName) {
            case PURPLE:
                return R.style.AppTheme_Purple;
            case BLUE:
                return R.style.AppTheme_Blue;
            case CYAN:
                return R.style.AppTheme_Cyan;
            case GREEN:
                return R.style.AppTheme_Green;
            case BROWN:
                return R.style.AppTheme_Brown;
            case GREY:
                return R.style.AppTheme_Grey;
            case YELLOW:
                return R.style.Yellow;
            default:
                return R.style.AppTheme;
        }
    }

    /*
    获取配置里面的主题
     */
    public static int getConfigStyle() {
        return getStyle(Config.INSTANCE.getThemeColor());
    }

    public static int getConfigColor(Context context) {
        return getColor(context, Config.INSTANCE.getThemeColor());
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    public static int getDarkColor(Activity activity) {
        if (activity != null && activity.getTheme() != null) {
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            return typedValue.data;
        }
        return -1;
    }
}
