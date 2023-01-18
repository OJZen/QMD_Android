package com.qmd.jzen.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by junzi on 2017/12/27.
 */

public class DownloadFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTitles = new String[]{"资源队列","正在下载", "已下载"};
    List<Fragment> fragments;

    public DownloadFragmentPagerAdapter(FragmentManager fm, List<Fragment> f) {
        super(fm);
        fragments = f;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }


}
