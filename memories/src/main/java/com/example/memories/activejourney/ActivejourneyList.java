package com.example.memories.activejourney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.activejourney.adapters.AllJourneysListAdapter;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.LapsList;

import java.util.List;

/**
 * Created by ankit on 27/5/15.
 */
public class ActivejourneyList extends BaseActivity {

    private static final String TAG = "DASHBOARD";
    private ListView mListView;
    private AllJourneysListAdapter mAdapter;
    private List<Journey> mJourneysList;
    private Button mStartJourneyBtn;

    private boolean backPressedToExitOnce = false;
    private Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_journey_list);

        mListView = (ListView) findViewById(R.id.journeyList);
        mStartJourneyBtn = (Button) findViewById(R.id.newJourneyBtn);
        mJourneysList = JourneyDataSource.getAllActiveJourneys(this);
        mAdapter = new AllJourneysListAdapter(this, mJourneysList);
        Log.d(TAG, "listview " + mListView + " adapter " + mAdapter);
        mListView.setAdapter(mAdapter);

        mStartJourneyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
