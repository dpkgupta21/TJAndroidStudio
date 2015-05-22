package com.example.memories.newjourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.login.CustomResultReceiver;
import com.example.memories.models.Journey;
import com.example.memories.newjourney.adapters.PendingJourneysListAdapter;
import com.example.memories.timeline.Timeline;

import java.util.List;

public class NewJourney extends Activity implements CustomResultReceiver.Receiver{

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

        mListView = (ListView)findViewById(R.id.pendingRequestsList);
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
        if(resultCode == REQUEST_FETCH_CONTACTS){
            contactsFetched = true;
        }else if(resultCode == REQUEST_FETCH_MEMORIES){
            memoriesFetched = true;
        }
        if(contactsFetched && memoriesFetched){
            Intent i = new Intent(getApplicationContext(), Timeline.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        Log.d(TAG, "sign in contacts fetched successfully");
    }
}