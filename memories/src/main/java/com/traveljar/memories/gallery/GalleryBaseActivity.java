package com.traveljar.memories.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.TextView;

import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.customviews.SlidingTabLayout;
import com.traveljar.memories.gallery.adapters.GalleryTabsPagerAdapter;

public class GalleryBaseActivity extends BaseActivity {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_base_activity);

        setUpToolBar();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new GalleryTabsPagerAdapter(getSupportFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Gallery");
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            Intent i = new Intent(this, ActivejourneyList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }


}
