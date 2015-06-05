package com.example.memories.gallery;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.customviews.SlidingTabLayout;
import com.example.memories.gallery.adapters.GalleryTabsPagerAdapter;

public class GalleryBaseActivity extends BaseActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_base_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Gallery");

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new GalleryTabsPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }


}
