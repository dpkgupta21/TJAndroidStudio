package com.example.memories.pastjourney;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.pastjourney.adapters.PastJourneyListAdapter;

public class PastJourneyList extends BaseActivity {

    private static final String TAG = null;
    private PastJourneyListAdapter pastJourneyListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_journey_list);

        Cursor c = JourneyDataSource.getAllPastJourneys(this);

        Log.d(TAG, "cursor length is " + c.getCount());

        ListView pastJourneyListView = (ListView) findViewById(R.id.pastJourneyList);
        pastJourneyListViewAdapter = new PastJourneyListAdapter(getBaseContext(), c);
        pastJourneyListView.setAdapter(pastJourneyListViewAdapter);
    }
}
