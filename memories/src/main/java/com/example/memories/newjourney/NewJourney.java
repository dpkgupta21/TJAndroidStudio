package com.example.memories.newjourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.adapters.PendingJourneysListAdapter;

import java.util.List;

public class NewJourney extends Activity {

    private static final String TAG = "<NewJourney>";
    private ListView mListView;
    private PendingJourneysListAdapter mAdapter;
    private List<Journey> mPendingJourneysList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey);

        mListView = (ListView)findViewById(R.id.pendingRequestsList);
        mPendingJourneysList = JourneyDataSource.getPendingJourneys(this);
        mAdapter = new PendingJourneysListAdapter(this, mPendingJourneysList);
        mListView.setAdapter(mAdapter);
    }

    public void embarkNewJourney(View v) {
        Intent i = new Intent(getBaseContext(), AddLap.class);
        startActivity(i);
    }
}