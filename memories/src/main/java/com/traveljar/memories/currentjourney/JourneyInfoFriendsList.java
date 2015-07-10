package com.traveljar.memories.currentjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.currentjourney.adapters.JourneyInfoFriendsListAdapter;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.services.PullContactsService;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;

import de.greenrobot.event.EventBus;

public class JourneyInfoFriendsList extends AppCompatActivity {

    private static final String TAG = "<JInfoFriendsList>";
    //CustomResultReceiver mReceiver;
    private List<Contact> allContactsList;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoFriendsListAdapter mAdapter;
    private List<Contact> allBuddiesList;
    private ProgressDialog mDialog;

    // For the request bus receive event to discard the received event which is not meant for this activity
    private static int ACTIVITY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_info_friends_list);

        setUpToolBar();

/*        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);*/

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait while we fetch your contacts from server");
        mDialog.setCanceledOnTouchOutside(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_friends_list_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //allBuddiesList = ContactDataSource.getContactsFromJourney(getBaseContext(), TJPreferences.getActiveJourneyId(getBaseContext()));
        allBuddiesList = ContactDataSource.getAllContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
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

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Add Friend");
        toolbar.inflateMenu(R.menu.toolbar_with_reload);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_buddy:
                        Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                        intent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
                        startService(intent);
                        mDialog.show();
                        return true;
                }
                return false;
            }
        });
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //add(int groupId, int itemId, int order, String titleRes)
        menu.add(0, 0, 0, "Refresh").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case 0:
                Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                //intent.putExtra("RECEIVER", mReceiver);
                intent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
                startService(intent);
                mDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ContactsFetchEvent event){
        //Discard the event if the event's activity code is not similar to its own activity code
        if(event.getActivityCode() == ACTIVITY_CODE) {
            Log.d(TAG, "contacts fetched successfully, now dismiss the dialog");
            mDialog.dismiss();
            //allBuddiesList = ContactDataSource.getContactsFromJourney(getBaseContext(), TJPreferences.getActiveJourneyId(getBaseContext()));
            allBuddiesList = ContactDataSource.getAllContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
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

/*    @Override
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
    }*/
}