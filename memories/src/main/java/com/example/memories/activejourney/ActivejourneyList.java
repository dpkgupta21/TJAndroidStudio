package com.example.memories.activejourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.activejourney.adapters.ActiveJourneyListAdapter;
import com.example.memories.customviews.MyFABView;
import com.example.memories.newjourney.LapsList;

/**
 * Created by ankit on 27/5/15.
 */
public class ActivejourneyList extends BaseActivity {

    private static final String TAG = "<ActivejourneyList>";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ActiveJourneyListAdapter mAdapter;

    private boolean backPressedToExitOnce = false;
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_journey_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.active_journey_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d(TAG, "buddies lisr === " + JourneyDataSource.getAllActiveJourneys(this));
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
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
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
