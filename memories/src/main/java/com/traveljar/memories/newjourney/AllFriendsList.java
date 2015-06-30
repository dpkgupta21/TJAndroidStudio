package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.newjourney.adapters.AllFriendsListAdapter;
import com.traveljar.memories.services.PullContactsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AllFriendsList extends AppCompatActivity {
    private static final String TAG = "AllFriendsList";
    //CustomResultReceiver mReceiver;
    private AllFriendsListAdapter mAdapter;
    private List<Contact> list;
    private List<Contact> selectedList;
    private ListView listView;
    private ProgressDialog mDialog;

    // For the request bus receive event to discard the received event which is not meant for this activity
    private static int ACTIVITY_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_traveljar_contact_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Friends");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("please wait while we fetch your contacts from server");

/*        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);*/

        Log.d(TAG, "1");
        // fetch all names from phone address book stored in local DB-- COntact
        list = new ArrayList<>();
        list = ContactDataSource.getAllContacts(this);
        Collections.sort(list);

        Log.d(TAG, "2");
        listView = (ListView) findViewById(R.id.all_contacts_listview);
        mAdapter = new AllFriendsListAdapter(this, list);
        listView.setAdapter(mAdapter);

        Log.d(TAG, "3");
        // Search functionality to filter list
        EditText query = (EditText) findViewById(R.id.all_contacts_searchview);
        query.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                Log.d(TAG, "search query = " + cs);
                AllFriendsList.this.mAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_journey_selected_friends, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_next:
                goToNext();
                return true;
            case R.id.action_refresh:
                Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                Log.d(TAG, "starting intent for pull contacts service");
                //intent.putExtra("RECEIVER", mReceiver);
                intent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
                startService(intent);
                mDialog.show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNext() {
        Log.d(TAG, "done is clicked!!!");
        selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isSelected()) {
                selectedList.add(list.get(i));
            }
        }

        for (Contact x : selectedList) {
            if (!SelectedFriendsList.selectedList.contains(x))
                SelectedFriendsList.selectedList.add(x);
        }

        // MDShareFile.contactList.addAll(selectedList);
        SelectedFriendsList.contactListViewAdapter.notifyDataSetChanged();
        AllFriendsList.this.finish();
    }

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
            mDialog.dismiss();
            list = ContactDataSource.getAllContacts(this);
            Collections.sort(list);
            if (mAdapter == null) {
                Log.d(TAG, "on receive result called with null");
                mAdapter = new AllFriendsListAdapter(this, list);
                listView.setAdapter(mAdapter);
            } else {
                Log.d(TAG, "on receive result called with not null");
                mAdapter.updateList(list);
                mAdapter.notifyDataSetChanged();
            }
        }
    }


/*    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mDialog.dismiss();
        list = ContactDataSource.getAllContacts(this);
        Collections.sort(list);
        if (mAdapter == null) {
            Log.d(TAG, "on receive result called with null");
            mAdapter = new AllFriendsListAdapter(this, list);
            listView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "on receive result called with not null");
            mAdapter.updateList(list);
            mAdapter.notifyDataSetChanged();
        }
    }*/
}