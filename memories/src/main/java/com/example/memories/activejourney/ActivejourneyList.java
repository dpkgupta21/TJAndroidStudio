package com.example.memories.activejourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.activejourney.adapters.ActiveJourneyListAdapter;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.LapsList;
import com.example.memories.services.PullContactsService;

import java.util.List;

/**
 * Created by ankit on 27/5/15.
 */
public class ActivejourneyList extends BaseActivity {

    private static final String TAG = "<ActivejourneyList>";
    List<Journey> allActiveJourney;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActiveJourneyListAdapter mAdapter;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private TextView noActivejourneysMsgTxt;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_journey_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Active Journeys");

        allActiveJourney = JourneyDataSource.getAllActiveJourneys(this);

        if (allActiveJourney.size() > 0) {
            mRecyclerView = (RecyclerView) findViewById(R.id.active_journey_recycler_view);
            mRecyclerView.setVisibility(View.VISIBLE);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new ActiveJourneyListAdapter(allActiveJourney, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);

            // Add pull to refresh functionality
            mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.active_journey_swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    allActiveJourney = JourneyDataSource.getAllActiveJourneys(getBaseContext());
                    mAdapter.updateList(allActiveJourney);
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } else {
            noActivejourneysMsgTxt = (TextView) findViewById(R.id.active_journey_no_buddies_msg);
            noActivejourneysMsgTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
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
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.active_journey_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_add_journey:
                Log.d(TAG, "new journey clicked!");
                Intent i = new Intent(getBaseContext(), LapsList.class);
                startActivity(i);
                i = new Intent(getBaseContext(), PullContactsService.class);
                startService(i);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
