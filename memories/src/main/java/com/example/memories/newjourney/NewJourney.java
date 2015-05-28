package com.example.memories.newjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.currentjourney.CurrentJourneyBaseActivity;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.adapters.PendingJourneysListAdapter;
import com.example.memories.services.CustomResultReceiver;

import java.util.List;

public class NewJourney extends AppCompatActivity implements CustomResultReceiver.Receiver {

    private static final String TAG = "<NewJourney>";
    private ListView mListView;
    private PendingJourneysListAdapter mAdapter;
    private List<Journey> mPendingJourneysList;

    private boolean contactsFetched;
    private boolean memoriesFetched;
    private int REQUEST_FETCH_CONTACTS = 1;
    private int REQUEST_FETCH_MEMORIES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Capture Audio");
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.pendingRequestsList);
        mPendingJourneysList = JourneyDataSource.getPendingJourneys(this);
        mAdapter = new PendingJourneysListAdapter(this, mPendingJourneysList);
        mListView.setAdapter(mAdapter);
    }

    public void embarkNewJourney(View v) {
        Intent i = new Intent(getBaseContext(), AddLap.class);
        startActivity(i);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == REQUEST_FETCH_CONTACTS) {
            contactsFetched = true;
        } else if (resultCode == REQUEST_FETCH_MEMORIES) {
            memoriesFetched = true;
        }
        if (contactsFetched && memoriesFetched) {
            Intent i = new Intent(getApplicationContext(), CurrentJourneyBaseActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        Log.d(TAG, "sign in contacts fetched successfully");
    }
}