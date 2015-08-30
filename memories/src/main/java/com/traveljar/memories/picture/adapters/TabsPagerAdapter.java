package com.traveljar.memories.picture.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.traveljar.memories.picture.UploadImageFromGallery;
import com.traveljar.memories.picture.UploadPictureFromCamera;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Gallery", "Capture"};

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                return new UploadImageFromGallery();
            case 1:
                return new UploadPictureFromCamera();
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