package com.example.memories.currentjourney;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.currentjourney.adapters.JourneyInfoFriendsListAdapter;
import com.example.memories.models.Contact;
import com.example.memories.utility.TJPreferences;

import java.util.List;

/**
 * Created by abhi on 06/06/15.
 */
public class JourneyInfoFriendsList extends AppCompatActivity {

    private static final String TAG = "<JInfoFriendsList>";
    private List<Contact> allContactsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoFriendsListAdapter mAdapter;
    private List<Contact> allBuddiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_info_friends_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Friend");

        allBuddiesList = ContactDataSource.getContactsFromJourney(getBaseContext(), TJPreferences.getActiveJourneyId(getBaseContext()));
        allContactsList = ContactDataSource.getAllContacts(getBaseContext());
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());
        allContactsList.removeAll(allBuddiesList);
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());

        if (allContactsList.size() > 0) {
            mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_friends_list_recycler_view);
            mRecyclerView.setVisibility(View.VISIBLE);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter (see also next example)
            mAdapter = new JourneyInfoFriendsListAdapter(allContactsList, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);

        } else {
            TextView noContactsMsg = (TextView) findViewById(R.id.journey_info_friends_list_no_buddies_msg);
            noContactsMsg.setVisibility(View.VISIBLE);
        }
    }
}