package com.example.memories.currentjourney;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.memories.R;
import com.example.memories.currentjourney.adapters.CurrentJourneyTabsAdapter;
import com.example.memories.customviews.SlidingTabLayout;

public class CurrentJourneyBaseActivity extends AppCompatActivity {

    private static final String TAG = "<CurJourneyActivity>";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline_base_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        toolbar.setTitle("Capture Audio");
        setSupportActionBar(toolbar);

        mViewPager = (ViewPager) findViewById(R.id.timeline_viewpager);
        mViewPager.setAdapter(new CurrentJourneyTabsAdapter(getSupportFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.timeline_sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    public void onFABClick(View v) {
        Log.d(TAG, "djfnjdfndjn");
    }


}
