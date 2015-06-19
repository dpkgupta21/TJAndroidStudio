package com.example.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.audio.AudioCapture;
import com.example.memories.checkin.CheckInPlacesList;
import com.example.memories.currentjourney.adapters.CurrentJourneyTabsAdapter;
import com.example.memories.customviews.SlidingTabLayout;
import com.example.memories.moods.MoodCapture;
import com.example.memories.note.CreateNotes;
import com.example.memories.picture.PictureCapture;
import com.example.memories.utility.TJPreferences;
import com.example.memories.video.VideoCapture;

public class CurrentJourneyBaseActivity extends BaseActivity {

    private static final String TAG = "<CurJourneyActivity>";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_base_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(getBaseContext())).getName());
        toolbar.setSubtitle(JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(getBaseContext())).getTagLine());

        mViewPager = (ViewPager) findViewById(R.id.timeline_viewpager);
        mViewPager.setAdapter(new CurrentJourneyTabsAdapter(getSupportFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.timeline_sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.current_journey_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
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
                i = new Intent(this, PictureCapture.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_info:
                Log.d(TAG, "info clicked!");
                Intent i = new Intent(getBaseContext(), JourneyInfo.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, ActivejourneyList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}
