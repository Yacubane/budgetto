package pl.cyfrogen.budget.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pl.cyfrogen.budget.fragments.HistoryFragment;
import pl.cyfrogen.budget.fragments.HomeFragment;
import pl.cyfrogen.budget.fragments.OptionsFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static int TAB_COUNT = 3;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return HomeFragment.newInstance();
            case 1:
                return HistoryFragment.newInstance();
            case 2:
                return OptionsFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return HomeFragment.TITLE;

            case 1:
                return HistoryFragment.TITLE;

            case 2:
                return OptionsFragment.TITLE;
        }
        return super.getPageTitle(position);
    }
}