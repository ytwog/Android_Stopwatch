package com.project.electrosolve;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ElementAdapter extends FragmentPagerAdapter {


    private int tabsNum;

    public ElementAdapter(FragmentManager fm, int _tabsNum) {
        super(fm);
        tabsNum = _tabsNum;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new commonItems_Fragment();
            case 1:
                return new advancedItems_Fragment();
            case 2:
                return new advancedItems_Fragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNum;
    }
}
