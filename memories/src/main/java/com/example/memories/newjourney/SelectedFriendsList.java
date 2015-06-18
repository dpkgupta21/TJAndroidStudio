package com.example.memories.newjourney;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.newjourney.adapters.MultiAutoCompleteViewAdapter;
import com.example.memories.newjourney.adapters.SelectedFriendsListAdapter;
import com.example.memories.volley.AppController;

import java.util.ArrayList;
import java.util.List;

public class SelectedFriendsList extends AppCompatActivity {

    private static final String TAG = "<SelectedFriendsList>";
    public static List<Contact> selectedList;

    private List<Contact> allContactsList;
    public static SelectedFriendsListAdapter contactListViewAdapter;
    // handler and runnable are used to check(every 1 second) from PullContactsService.java whether all the contacts have been fetched
    Handler timerHandler = new Handler();
    private ActionBar actionBar;
    private ListView contactListView;
    private ProgressDialog mProgressDialog;

    private MultiAutoCompleteViewAdapter  adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_selected_friends_list);
        Log.d(TAG, "entered Add friends");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Friends");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedList = new ArrayList<>();

        mProgressDialog = new ProgressDialog(this);

        contactListView = (ListView) findViewById(R.id.addFriendsList);

        initializeData();
    }

    private void initializeData() {
        mProgressDialog.dismiss();
        contactListViewAdapter = new SelectedFriendsListAdapter(this, selectedList);
        contactListView.setAdapter(contactListViewAdapter);

        // configure auto complete text view
        final MultiAutoCompleteTextView macTv = (MultiAutoCompleteTextView) findViewById(R.id.addFriendsContactSearch);
        /*ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(
                R.array.contacts_list));*/

        allContactsList = ContactDataSource.getAllContacts(this);

        adapter = new MultiAutoCompleteViewAdapter(this, allContactsList);
        macTv.setAdapter(adapter);
        macTv.setThreshold(1);
        macTv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        macTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Contact contact = adapter.getFilteredContactAtPosition(position);
                if (!selectedList.contains(contact)) {
                    selectedList.add(contact);
                    contactListViewAdapter.notifyDataSetChanged();
                }
                macTv.setText(null);
            }
        });
    }

    public void goToAllContactList(View v) {
        Intent i = new Intent(this, AllFriendsList.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items_laps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_next:
                goToNext();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNext() {
        AppController.buddyList = new ArrayList<>();
        // save all the selected friend's id in "buddyList" in AppCOntroller
        int len = selectedList.size();

        if (len > 0) {
            for (int i = 0; i < len; i++) {
                AppController.buddyList.add(selectedList.get(i).getIdOnServer());
                Log.d(TAG, "ids are" + selectedList.get(i).getIdOnServer());
            }
        }

        Intent i = new Intent(getBaseContext(), NewJourneyDetail.class);
        startActivity(i);
    }

    @Override
    public void onResume(){
        allContactsList = ContactDataSource.getAllContacts(this);
        adapter.updateList(allContactsList);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

}