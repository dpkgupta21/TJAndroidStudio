package com.traveljar.memories.media;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter{

        private String[] titles = new String[]{"Gallery", "Image", "Video"};

        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {

            switch (index) {
                case 0:
                    return new CaptureMediaFromGallery();
                case 1:
                    return new CaptureImage();
                case 2:
                    return new CaptureVideo();

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