package com.example.memories.gallery.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.memories.gallery.GalleryAudiosFragment;
import com.example.memories.gallery.GalleryNotesFragment;
import com.example.memories.gallery.GalleryPhotosFragment;
import com.example.memories.gallery.GalleryVideosFragment;

public class GalleryTabsPagerAdapter extends FragmentPagerAdapter {

    public GalleryTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new GalleryPhotosFragment();
            case 1:
                // Games fragment activity
                return new GalleryAudiosFragment();
            case 2:
                // Movies fragment activity
                return new GalleryVideosFragment();
            case 3:
                // Movies fragment activity
                return new GalleryNotesFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
    }

}
