package com.traveljar.memories.activejourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.LapDataSource;
import com.traveljar.memories.activejourney.adapters.ActiveJourneyListAdapter;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.newjourney.LapsList;

import java.util.Collections;
import java.util.List;

public class ActivejourneyList extends BaseActivity {

    private static final String TAG = "<ActivejourneyList>";
    List<Journey> allActiveJourney;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActiveJourneyListAdapter mAdapter;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout mLayout;

    /*activityVisible will track the visible state of the activity which
    * will be used to refresh the listview when gcm notification arrives*/
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

    private static ActivejourneyList instance;

    public ActivejourneyList() {
        super(0);
        instance = this;
    }

    public static ActivejourneyList getInstance() {
        return instance == null ? new ActivejourneyList() : instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.active_journey_list);

        setUpToolbar();

        /*Just for testing purpose*/
        List<Lap> lapsList = LapDataSource.getAllLaps(this);
        Log.d(TAG, "total laps fetched for journey are " + lapsList.size());
        for (Lap lap : lapsList) {
            Log.d(TAG, "lap is " + lap);
        }

        mLayout = (LinearLayout) findViewById(R.id.active_journey_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.active_journey_recycler_view);
        mRecyclerView.setVisibility(View.VISIBLE);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        refreshJourneys();

        // Add pull to refresh functionality
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.active_journey_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshJourneys();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            Log.d(TAG, "closing the drawer");
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            if (backPressedToExitOnce) {
                super.onBackPressed();
            } else {
                this.backPressedToExitOnce = true;
                showToast("Press again to exit");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        backPressedToExitOnce = false;
                    }
                }, 2000);
            }
        }
    }

    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }

    @Override
    protected void onPause() {
        if (this.toast != null) {
            this.toast.cancel();
        }
        activityPaused();
        super.onPause();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Active Journeys");

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.inflateMenu(R.menu.active_journey_action_bar);
        //toolbar.setTitle("Active Journeys");
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_journey:
                        Log.d(TAG, "new journey clicked!");
                        Intent i = new Intent(getBaseContext(), LapsList.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }

    private void refreshJourneys() {
        allActiveJourney = JourneyDataSource.getAllActiveJourneys(this);
        Collections.sort(allActiveJourney);
        if (allActiveJourney.size() > 0) {
            if (mAdapter == null) {
                mAdapter = new ActiveJourneyListAdapter(allActiveJourney, this);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.updateList(allActiveJourney);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            mLayout.setBackgroundResource(R.drawable.img_no_active_journey);
            ImageButton startNewJourneyBtn = (ImageButton) findViewById(R.id.active_journey_start_btn);
            startNewJourneyBtn.setVisibility(View.VISIBLE);
            startNewJourneyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "new journey clicked!");
                    Intent i = new Intent(getBaseContext(), LapsList.class);
                    startActivity(i);
                }
            });

        }
    }

    @Override
    public void onResume() {
        refreshJourneys();
        activityResumed();
        super.onResume();
    }

    /*This method will be called by GCMIntentService if the activity is visible to refresh the listview*/
    public void refreshJourneysList() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }
}
