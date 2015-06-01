package com.example.memories.currentjourney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.currentjourney.adapters.JourneyInfoBuddiesListAdapter;
import com.example.memories.utility.TJPreferences;

/**
 * Created by abhi on 29/05/15.
 */
public class JourneyInfo extends AppCompatActivity {

    private static final String TAG = "<JourneyInfo>";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoBuddiesListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Info");
        setSupportActionBar(toolbar);

        Log.d(TAG, "looking for buddie slist");
        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_buddies_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Log.d(TAG, "buddies lisr === " + ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this)));
        // specify an adapter (see also next example)
        mAdapter = new JourneyInfoBuddiesListAdapter(ContactDataSource.getContactsFromJourney(this,TJPreferences.getActiveJourneyId(this)));
        mRecyclerView.setAdapter(mAdapter);

    }

}
