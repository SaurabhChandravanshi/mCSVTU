package com.saurabhchandr.em.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.saurabhchandr.em.Model.ResultData;
import com.saurabhchandr.em.R;

import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    private ResultData data;
    public SectionsPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle,ResultData data) {
        super(fragmentManager, lifecycle);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 3:
                return PlaceholderFragment.newInstance(position,data.getRrvLink());
            case 2:
                return PlaceholderFragment.newInstance(position,data.getRvLink());
            case 1:
                return PlaceholderFragment.newInstance(position,data.getRtLink());
            case 0:
            default:
                return PlaceholderFragment.newInstance(position,data.getRegLink());
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}