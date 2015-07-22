package com.traveljar.memories.video;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.traveljar.memories.picture.UploadPictureFromCamera;

/**
 * Created by ankit on 22/7/15.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[]{"Gallery", "Capture"};

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new UploadVideoFromGallery();
            case 1:
                return new UploadVideoFromCamera();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
