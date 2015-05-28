package com.example.memories;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.LapsList;

import java.util.List;

/**
 * Created by ankit on 27/5/15.
 */
public class DashBoard extends AppCompatActivity {

    private static final String TAG = "DASHBOARD";
    private ListView mListView;
    private AllJourneysListAdapter mAdapter;
    private List<Journey> mJourneysList;
    private Button mStartJourneyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Capture Audio");
        setSupportActionBar(toolbar);

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
}
