package com.traveljar.memories.picture;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.traveljar.memories.R;
import com.traveljar.memories.customviews.SlidingTabLayout;
import com.traveljar.memories.picture.adapters.TabsPagerAdapter;

public class PictureCaptureBaseActivity extends AppCompatActivity{

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_media_base_activity);
        Log.d("PictureCaptureBaseActivity", "on create called");
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TabsPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
