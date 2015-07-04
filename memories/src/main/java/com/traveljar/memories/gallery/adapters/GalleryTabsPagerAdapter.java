package com.traveljar.memories.gallery.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.traveljar.memories.gallery.GalleryAudioAlbumsFragment;
import com.traveljar.memories.gallery.GalleryNotesAlbumsFragment;
import com.traveljar.memories.gallery.GalleryPictureAlbumsFragment;
import com.traveljar.memories.gallery.GalleryVideoAlbumsFragment;

public class GalleryTabsPagerAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"Pictures", "Audios", "Videos", "Notes"};

    public GalleryTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new GalleryPictureAlbumsFragment();
            case 1:
                // Games fragment activity
                return new GalleryAudioAlbumsFragment();
            case 2:
                // Movies fragment activity
                return new GalleryVideoAlbumsFragment();
            case 3:
                // Movies fragment activity
                return new GalleryNotesAlbumsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 4;
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
