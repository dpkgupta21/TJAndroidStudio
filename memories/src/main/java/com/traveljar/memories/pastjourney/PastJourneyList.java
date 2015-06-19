package com.traveljar.memories.pastjourney;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;

import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.pastjourney.adapters.PastJourneyListAdapter;

public class PastJourneyList extends BaseActivity {

    private static final String TAG = null;
    private PastJourneyListAdapter pastJourneyListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_journey_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Past Journeys");

        Cursor c = JourneyDataSource.getAllPastJourneys(this);

        Log.d(TAG, "cursor length is " + c.getCount());

        ListView pastJourneyListView = (ListView) findViewById(R.id.pastJourneyList);
        pastJourneyListViewAdapter = new PastJourneyListAdapter(getBaseContext(), c);
        pastJourneyListView.setAdapter(pastJourneyListViewAdapter);
    }
}
