package com.traveljar.memories.currentjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.currentjourney.adapters.JourneyInfoFriendsListAdapter;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.services.CustomResultReceiver;
import com.traveljar.memories.services.PullContactsService;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;

/**
 * Created by abhi on 06/06/15.
 */
public class JourneyInfoFriendsList extends AppCompatActivity implements CustomResultReceiver.Receiver {

    private static final String TAG = "<JInfoFriendsList>";
    CustomResultReceiver mReceiver;
    private List<Contact> allContactsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoFriendsListAdapter mAdapter;
    private List<Contact> allBuddiesList;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_info_friends_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Friend");
        setSupportActionBar(toolbar);

        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait while we fetch your contacts from server");
        //mDialog.setCanceledOnTouchOutside(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_friends_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        allBuddiesList = ContactDataSource.getContactsFromJourney(getBaseContext(), TJPreferences.getActiveJourneyId(getBaseContext()));
        allContactsList = ContactDataSource.getAllContacts(getBaseContext());
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());
        allContactsList.removeAll(allBuddiesList);
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());

        if (allContactsList.size() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);

            // specify an adapter (see also next example)
            mAdapter = new JourneyInfoFriendsListAdapter(allContactsList, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);

        } else {
            TextView noContactsMsg = (TextView) findViewById(R.id.journey_info_friends_list_no_buddies_msg);
            noContactsMsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add(int groupId, int itemId, int order, String titleRes)
        menu.add(0, 0, 0, "Refresh").setIcon(R.drawable.ic_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                intent.putExtra("RECEIVER", mReceiver);
                startService(intent);
                mDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        Log.d(TAG, "contacts fetched successfully, now dismiss the dialog");
        mDialog.dismiss();
        allBuddiesList = ContactDataSource.getContactsFromJourney(getBaseContext(), TJPreferences.getActiveJourneyId(getBaseContext()));
        allContactsList = ContactDataSource.getAllContacts(getBaseContext());
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());
        allContactsList.removeAll(allBuddiesList);
        Log.d(TAG, allBuddiesList.size() + "====" + allContactsList.size());
        if (mAdapter == null) {
            mAdapter = new JourneyInfoFriendsListAdapter(allContactsList, getApplicationContext());
        } else {
            mAdapter.updateContactsList(allContactsList);
            mAdapter.notifyDataSetChanged();
        }
    }
}