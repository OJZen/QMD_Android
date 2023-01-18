package com.qmd.jzen.ui.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class FabScrollBehavior extends FloatingActionButton.Behavior {

    int lastNumber = 0;

    // 因为需要在布局xml中引用，所以必须实现该构造方法
    public FabScrollBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        // 确保滚动方向为垂直方向
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FloatingActionButton child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        // 与上一个数不一样说明改变的方向
        if (dyConsumed * lastNumber <= 0) {
            if (dyConsumed > 0) { // 向下滑动
                animateOut(child);
            } else if (dyConsumed < 0) { // 向上滑动
                animateIn(child);
            }
        }
        lastNumber = dyConsumed;

        //Logger.e(""+dyConsumed);
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
    }

    // FAB移出屏幕动画（隐藏动画）
    private void animateOut(FloatingActionButton fab) {
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        int bottomMargin = layoutParams.bottomMargin;
        fab.animate().translationY(fab.getHeight() + bottomMargin).setInterpolator(new DecelerateInterpolator()).start();
    }

    // FAB移入屏幕动画（显示动画）
    private void animateIn(FloatingActionButton fab) {
        fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
    }
}
