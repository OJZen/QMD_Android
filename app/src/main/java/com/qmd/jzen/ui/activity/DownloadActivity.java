package com.qmd.jzen.ui.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.download.DownloadEntity;
import com.google.android.material.tabs.TabLayout;
import com.qmd.jzen.R;
import com.qmd.jzen.adapters.DownloadFragmentPagerAdapter;
import com.qmd.jzen.entity.LoadUrlEntity;
import com.qmd.jzen.network.MusicDownload;
import com.qmd.jzen.ui.fragments.DownloadedFragment;
import com.qmd.jzen.ui.fragments.DownloadingFragment;
import com.qmd.jzen.ui.fragments.UrlLoadingFragment;
import com.qmd.jzen.ui.view.QMDActivity;
import com.qmd.jzen.utils.FileUtil;
import com.qmd.jzen.utils.QMDDialog;
import com.qmd.jzen.utils.ThemeColorManager;
import com.qmd.jzen.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends QMDActivity {
    Toolbar toolbar;
    ViewPager viewPager;
    DownloadFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(ThemeColorManager.getConfigStyle());
        setContentView(R.layout.activity_download);
        //初始化
        initial();
    }

    private void initial() {
        // 设置toolbar
        toolbar = (Toolbar) findViewById(R.id.download_toolbar);
        toolbar.setTitle("下载");
        setToolbar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new UrlLoadingFragment());
        fragments.add(new DownloadingFragment());
        fragments.add(new DownloadedFragment());
        // 获取fragment的适配器
        adapter = new DownloadFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);

        // 绑定viewpager
        tabLayout.setupWithViewPager(viewPager);
        // 默认为正在下载页面
        viewPager.setCurrentItem(1);

        // 根据参数跳转页面
        String page = getIntent().getStringExtra("page");
        if (page != null) {
            if (page.equals("downloaded")) {
                viewPager.setCurrentItem(2);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_downloaded_menu, menu);
        MenuItem menuPause = menu.findItem(R.id.menu_pause);
        MenuItem menuStart = menu.findItem(R.id.menu_start);

        // 页面
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    menuPause.setVisible(true);
                    menuStart.setVisible(true);
                } else {
                    menuPause.setVisible(false);
                    menuStart.setVisible(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_del:
                switch (viewPager.getCurrentItem()) {
                    case 0:
                        showDelUrlLoadDialog();
                        break;
                    case 1:
                        showDelTaskDialog();
                        break;
                    case 2:
                        showDelDialog();
                        break;
                }
                break;
            case R.id.menu_start:
                Aria.download(this).resumeAllTask();
                break;
            case R.id.menu_pause:
                Aria.download(this).stopAllTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 歌曲删除询问对话框
    private void showDelDialog() {
        List<DownloadEntity> list = Aria.download(this).getAllCompleteTask();
        if (list == null) return;
        QMDDialog.Companion.showDialogWithCheckBox(this, "高能警告：", "您是否要清空所有下载记录？", "同时删除源文件",
                (checked) -> {
                    // 删除所有已完成的任务
                    for (DownloadEntity entity : list) {
                        Aria.download(getApplicationContext()).load(entity.getId()).cancel(checked);
                    }
                    // 刷新界面数据
                    DownloadedFragment fragment = (DownloadedFragment) adapter.getItem(2);
                    fragment.removeAll();
                    Toaster.Companion.out("搞定！");
                });
    }

    // 删除所有任务
    private void showDelTaskDialog() {
        List<DownloadEntity> list = Aria.download(this).getAllNotCompleteTask();
        if (list == null) return;
        QMDDialog.Companion.showBaseDialog(this, "高能警告：", "您是否要删除所有下载任务？",
                (dialog) -> {
                    // 删除所有未完成的任务
                    for (DownloadEntity entity : list) {
                        Aria.download(DownloadActivity.this).load(entity.getId()).cancel(true);
                        FileUtil.deleteFile(entity.getFilePath()); // 会出现没删除的情况，所以手动删除一遍。
                    }
                    // 刷新界面数据
                    DownloadingFragment fragment = (DownloadingFragment) adapter.getItem(1);
                    fragment.notifyData();
                    Toaster.Companion.out("删除完毕！");
                });
    }

    // 删除所有获取资源队列
    private void showDelUrlLoadDialog() {
        List<LoadUrlEntity> entityList = MusicDownload.getAllLoadUrlEntity();
        if (entityList.size() == 0) return;

        QMDDialog.Companion.showBaseDialog(this, "提示：", "您是否要删除所有资源获取队列？",
                (dialog) -> {
                    MusicDownload.deleteAllLoadUrlEntity();
                    Toaster.Companion.out("已清空所有获取资源队列");
                });
    }

}

