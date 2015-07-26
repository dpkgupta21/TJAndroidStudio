package com.traveljar.memories.currentjourney.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.traveljar.memories.currentjourney.StatisticsFragment;
import com.traveljar.memories.currentjourney.TimecapsuleFragment;
import com.traveljar.memories.currentjourney.TimelineFragment;


public class CurrentJourneyTabsAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Timeline", "Stats", "Timecapsule"};

    public CurrentJourneyTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new TimelineFragment();
            case 1:
                return new StatisticsFragment();
            case 2:
                return new TimecapsuleFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
