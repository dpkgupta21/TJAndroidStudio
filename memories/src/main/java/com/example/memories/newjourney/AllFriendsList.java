package com.example.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.newjourney.adapters.AllFriendsListAdapter;
import com.example.memories.services.CustomResultReceiver;
import com.example.memories.services.PullContactsService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AllFriendsList extends AppCompatActivity implements CustomResultReceiver.Receiver {
    private static final String TAG = "AllFriendsList";
    CustomResultReceiver mReceiver;
    private AllFriendsListAdapter mAdapter;
    private List<Contact> list;
    private List<Contact> selectedList;
    private ListView listView;
    private ProgressDialog mDialog;

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

        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        Log.d(TAG, "1");
        // fetch all names from phone address book stored in local DB-- COntact
        list = new ArrayList<Contact>();
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
                intent.putExtra("RECEIVER", mReceiver);
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
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mDialog.dismiss();
        list = ContactDataSource.getAllContacts(this);
        Collections.sort(list);
        if (mAdapter == null) {
            mAdapter = new AllFriendsListAdapter(this, list);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.updateList(list);
            mAdapter.notifyDataSetChanged();
        }
    }
}