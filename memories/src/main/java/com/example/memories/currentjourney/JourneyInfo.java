package com.example.memories.currentjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.currentjourney.adapters.JourneyInfoBuddiesListAdapter;
import com.example.memories.models.Contact;
import com.example.memories.models.Journey;
import com.example.memories.utility.TJPreferences;

import java.util.List;

/**
 * Created by abhi on 29/05/15.
 */
public class JourneyInfo extends AppCompatActivity {

    private static final String TAG = "<JourneyInfo>";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoBuddiesListAdapter mAdapter;

    private TextView journeyName;
    private TextView journeyCreatedBy;
    private Journey mJourney;
    private TextView journeyBuddyCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Info");
        setSupportActionBar(toolbar);

        journeyName = (TextView) findViewById(R.id.journey_info_journey_name);
        journeyCreatedBy = (TextView) findViewById(R.id.journey_info_created_by);
        journeyBuddyCount = (TextView) findViewById(R.id.journey_info_buddies_count);
        mJourney = JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(this));

        if (mJourney != null) {
            journeyName.setText(mJourney.getName());

            Contact journeyCreatedByContact = ContactDataSource.getContactById(this, mJourney.getCreatedBy());
            if (journeyCreatedByContact != null) {
                Log.d(TAG, "journey is created by " + journeyCreatedByContact.getName());
                journeyCreatedBy.setText("CREATED BY " + journeyCreatedByContact.getName());
            } else {
                Log.d(TAG, "unable to find contact with journey id " + mJourney.getIdOnServer());
            }
        }

        List<Contact> allBuddiesList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "total buddies are = " + allBuddiesList.size());


        journeyBuddyCount.setText(String.valueOf(allBuddiesList.size()));
        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_buddies_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new JourneyInfoBuddiesListAdapter(allBuddiesList);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.journey_info_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_add_buddy:
                Log.d(TAG, "action_add_buddy clicked!");
                Intent i = new Intent(this, JourneyInfoFriendsList.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
