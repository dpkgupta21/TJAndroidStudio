package com.example.memories.newjourney;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.example.memories.R;
import com.example.memories.models.Contact;
import com.example.memories.newjourney.adapters.SelectedFriendsListAdapter;
import com.example.memories.volley.AppController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedFriendsList extends Activity {

    private static final String TAG = "<SelectedFriendsList>";
    public static List<Contact> selectedList;
    public static SelectedFriendsListAdapter contactListViewAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_selected_friends_list);
        Log.d(TAG, "entered Add friends");

        actionBar = getActionBar();
        actionBar.setTitle("Add Friends");

        selectedList = new ArrayList<Contact>();

        ListView contactListView = (ListView) findViewById(R.id.addFriendsList);
        contactListViewAdapter = new SelectedFriendsListAdapter(this, selectedList);
        contactListView.setAdapter(contactListViewAdapter);

        // configure auto complete text view
        final MultiAutoCompleteTextView macTv = (MultiAutoCompleteTextView) findViewById(R.id.addFriendsContactSearch);
        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(
                R.array.contacts_list));
        macTv.setAdapter(aaStr);
        macTv.setThreshold(1);
        macTv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        macTv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", arg0.getItemAtPosition(arg2).toString());
                map.put("image", "R.drawable.ic_action_settings");

                // if (!selectedList.contains(map)) {
                // selectedList.add(map);
                // }

                contactListViewAdapter.notifyDataSetChanged();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNext() {
        // save all the selected friend's id in "buddyList" in AppCOntroller
        int len = selectedList.size();
        AppController.buddyList = new ArrayList<String>();
        for (int i = 0; i < len; i++) {
            AppController.buddyList.add(selectedList.get(i).getIdOnServer());
            Log.d(TAG, "ids are" + selectedList.get(i).getIdOnServer());
        }

        Intent i = new Intent(getBaseContext(), NewJourneyDetail.class);
        startActivity(i);
    }

}