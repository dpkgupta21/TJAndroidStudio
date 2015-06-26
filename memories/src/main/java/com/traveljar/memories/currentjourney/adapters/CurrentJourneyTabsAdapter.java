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

    // BEGIN_INCLUDE (pageradapter_getpagetitle)

    /**
     * Return the title of the item at {@code position}. This is important
     * as what this method returns is what is displayed in the
     * {@link //SlidingTabLayout}.
     * <p/>
     * Here we construct one using the position value, but for real
     * application the title should refer to the item's contents.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
