package com.damnation.etachat.ui.tabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitle = new ArrayList<>();
    private final List<Integer> fragmentIcon = new ArrayList<>();

    public SectionsPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    public void addFragment(Fragment fragment, String title, int icon) {
        fragmentList.add(fragment);
        fragmentTitle.add(title);
        fragmentIcon.add(icon);
    }

    public CharSequence getFragmentName(int position) {
        return fragmentTitle.get(position);
    }

    public int getIcon(int position) {
        return fragmentIcon.get(position);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
