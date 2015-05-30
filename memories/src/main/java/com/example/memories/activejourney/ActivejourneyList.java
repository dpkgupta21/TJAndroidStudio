package com.example.memories.activejourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.activejourney.adapters.ActiveJourneyListAdapter;
import com.example.memories.currentjourney.CurrentJourneyBaseActivity;
import com.example.memories.customviews.MyFABView;
import com.example.memories.newjourney.LapsList;
import com.example.memories.services.CustomResultReceiver;

/**
 * Created by ankit on 27/5/15.
 */
public class ActivejourneyList extends BaseActivity implements CustomResultReceiver.Receiver {

    private static final String TAG = "<ActivejourneyList>";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActiveJourneyListAdapter mAdapter;
    private static final int REQUEST_FETCH_BUDDIES = 1;

    private boolean backPressedToExitOnce = false;
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_journey_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Active Journeys");
        toolbar.setLogo(R.drawable.ic_launcher);

        mRecyclerView = (RecyclerView) findViewById(R.id.active_journey_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new ActiveJourneyListAdapter(JourneyDataSource.getAllActiveJourneys(this), getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        // Add lap FAB Button
        final MyFABView fabButton = new MyFABView.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.plus79))
                .withButtonColor(getResources().getColor(R.color.tj_orange))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT).withMargins(0, 0, 16, 16).create();

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FAB clicked");
                Intent i = new Intent(getBaseContext(), LapsList.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "on receive result called " + resultCode);
        if (resultCode == REQUEST_FETCH_BUDDIES) {
            Log.d(TAG, "fetch buddies service completed");
            Intent intent = new Intent(this, CurrentJourneyBaseActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        Log.d(TAG, "sign in contacts fetched successfully");
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
}
