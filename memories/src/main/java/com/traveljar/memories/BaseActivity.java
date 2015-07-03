package com.traveljar.memories;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.gallery.GalleryBaseActivity;
import com.traveljar.memories.pastjourney.PastJourneyList;
import com.traveljar.memories.profile.ProfileActivity;
import com.traveljar.memories.settings.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "<BaseActivity>";
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    protected DrawerLayout drawerLayout;
    private ImageButton settingsButton;

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

        if(layoutResID == R.layout.current_journey_base_activity) {
            findViewById(R.id.toolbar).setVisibility(View.GONE);
            toolbar = (Toolbar) findViewById(R.id.subtitle_toolbar);
        }else {
            findViewById(R.id.subtitle_toolbar).setVisibility(View.GONE);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
        }
        initDrawer();
        Log.d(TAG, "4");
        setupNavigationView();

        settingsButton = (ImageButton)drawerLayout.findViewById(R.id.sidemenu_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, Settings.class);
                startActivity(intent);

            }
        });
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

    private void setupNavigationView(){
        ListView featuresListView = (ListView) findViewById(R.id.sidemenu_features_list);
        ArrayList<Map<String, String>> featuresList = new ArrayList<>();
        Integer[] categoryIconArray = {R.drawable.ic_account_balance_black_24dp, R.drawable.ic_explore_black_24dp, R.drawable.ic_photo_library_black_24dp,
                R.drawable.ic_account_circle_black_24dp};
        String[] hashMapKeys = {"icon", "title"};

        Integer len = getResources().getStringArray(R.array.sidemenu_features_list).length;
        for (int i = 0; i < len; i++) {
            HashMap<String, String> setting = new HashMap<>();
            setting.put("icon", "" + categoryIconArray[i]);
            setting.put("title", getResources().getStringArray(R.array.sidemenu_features_list)[i]);
            featuresList.add(setting);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, featuresList,
                R.layout.side_menu_drawer_item, hashMapKeys, new int[]{R.id.side_menu_cloud_icon,
                R.id.side_menu_cloud_title});
        featuresListView.setAdapter(adapter);

        featuresListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                Intent i;
                switch (position) {
                    case 0:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(BaseActivity.this, ActivejourneyList.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        break;
                    case 1:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(BaseActivity.this, PastJourneyList.class);
                        startActivity(i);
                        break;
                    case 2:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(BaseActivity.this, GalleryBaseActivity.class);
                        startActivity(i);
                        break;
                    case 3:
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        i = new Intent(BaseActivity.this, ProfileActivity.class);
                        startActivity(i);
                        break;

                    default:
                        break;
                }
            }
        });
    }

}
