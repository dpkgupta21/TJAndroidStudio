package com.traveljar.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.audio.AudioCapture;
import com.traveljar.memories.checkin.CheckInPlacesList;
import com.traveljar.memories.currentjourney.adapters.CurrentJourneyTabsAdapter;
import com.traveljar.memories.customviews.SlidingTabLayout;
import com.traveljar.memories.media.CaptureMedia;
import com.traveljar.memories.moods.MoodCapture;
import com.traveljar.memories.note.CreateNotes;
import com.traveljar.memories.pastjourney.PastJourneyList;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.video.VideoCapture;

public class CurrentJourneyBaseActivity extends BaseActivity {

    private static final String TAG = "<CurJourneyActivity>";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    CurrentJourneyTabsAdapter mTabsAdapter;
    public static final int NOTIFICATION_ID = 1;

    private static boolean activityVisible;
    public static boolean isActivityVisible() {
        return activityVisible;
    }
    public static void activityResumed() {
        activityVisible = true;
    }
    public static void activityPaused() {
        activityVisible = false;
    }

    private static CurrentJourneyBaseActivity instance;

    public CurrentJourneyBaseActivity(){
        super(5);
        instance = this;
    }

    public static CurrentJourneyBaseActivity getInstance(){
        return instance == null ? new CurrentJourneyBaseActivity() : instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_base_activity);

        setUpToolbar();
        mViewPager = (ViewPager) findViewById(R.id.timeline_viewpager);
        mTabsAdapter = new CurrentJourneyTabsAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAdapter);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.timeline_sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    public void overlayTimelineFragment(int color){
        //overlayFrame.setBackgroundColor(getResources().getColor(color));
    }



    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        TextView subtitle = (TextView)toolbar.findViewById(R.id.toolbar_subtitle);
        title.setText(JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(getBaseContext())).getName());
        subtitle.setText(JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(getBaseContext())).getTagLine());

        toolbar.inflateMenu(R.menu.current_journey_action_bar);
        toolbar.setTitle("Current Journeys");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_info:
                        Log.d(TAG, "info clicked!");
                        Intent i = new Intent(getBaseContext(), JourneyInfo.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }

    public void fabClick(View view) {
        Log.d(TAG, "on fab view click called" + view);
        Intent i;
        switch (view.getId()) {
            case R.id.button_mood:
                Log.d(TAG, "set a mood clicked");
                i = new Intent(this, MoodCapture.class);
                startActivity(i);
                break;
            case R.id.button_checkin:
                Log.d(TAG, "checkin clicked");
                i = new Intent(this, CheckInPlacesList.class);
                startActivity(i);
                break;
            case R.id.button_photo:
                Log.d(TAG, "photo clicked");
                i = new Intent(this, CaptureMedia.class);
                startActivity(i);
                break;
            case R.id.button_note:
                i = new Intent(this, CreateNotes.class);
                startActivity(i);
                Log.d(TAG, "note clicked");
                break;
            case R.id.button_video:
                Log.d(TAG, "video clicked");
                i = new Intent(this, VideoCapture.class);
                startActivity(i);
                break;
            case R.id.button_audio:
                Log.d(TAG, "audio clicked");
                i = new Intent(this, AudioCapture.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.closeDrawer(Gravity.LEFT);
        }else {
            Intent intent = new Intent(this, ActivejourneyList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onPause() {
        activityPaused();
        super.onPause();
    }

    public void onResume() {
        activityResumed();
        super.onResume();
    }

    // Call this method from from anywhere to refresh timeline adapter when this activity is visible
    public void refreshTimelineList(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.timeline_viewpager + ":" + mViewPager.getCurrentItem());
        if(fragment instanceof TimelineFragment){
            TimelineFragment.getInstance().loadMemoriesList();
        }
    }

    public void refreshTimelineFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.timeline_viewpager + ":" + mViewPager.getCurrentItem());
        if(fragment instanceof TimelineFragment){
            getSupportFragmentManager()
                    .beginTransaction()
                    .detach(fragment)
                    .attach(fragment)
                    .commit();
        }
    }

    public void endJourney(String journeyId){
        if(TJPreferences.getActiveJourneyId(this).equals(journeyId)){
            Toast.makeText(this, "Your journey has been marked finished by the admin", Toast.LENGTH_SHORT).show();
            finish();
            Intent intent = new Intent(this, PastJourneyList.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}
