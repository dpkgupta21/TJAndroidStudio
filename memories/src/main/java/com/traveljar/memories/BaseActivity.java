package com.traveljar.memories;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "<BaseActivity>";
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    protected DrawerLayout drawerLayout;

    @Override
    public void setContentView(int layoutResID) {
        Log.d(TAG, "1");

        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.base_activity, null);
        //LinearLayout activityContent = (LinearLayout) drawerLayout.findViewById(R.id.activity_content);
        FrameLayout frameLayout = (FrameLayout) drawerLayout.findViewById(R.id.content_activity_frame);

        Log.d(TAG, "2");
        getLayoutInflater().inflate(layoutResID, frameLayout, true);
        super.setContentView(drawerLayout);

        Log.d(TAG, "3");

        toolbar = (Toolbar) findViewById(R.id.toolbar);

/*        if(layoutResID == R.layout.current_journey_base_activity) {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            toolbar = (Toolbar) findViewById(R.id.subtitle_toolbar);
        }else {
            findViewById(R.id.subtitle_toolbar).setVisibility(View.GONE);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }*/
        initDrawer();
        Log.d(TAG, "4");
//        setupNavigationView();

    }

    private void initDrawer() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        Log.d(TAG, "drawer toggle 1.1 " + drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.d(TAG, "drawer toggle 1.2 " + drawerToggle);
//        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            super.onBackPressed();
        }
    }
}
