package com.example.memories.newjourney;

import android.app.ActionBar;
import android.app.ActivityManager;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.example.memories.R;
import com.example.memories.models.Contact;
import com.example.memories.newjourney.adapters.SelectedFriendsListAdapter;
import com.example.memories.services.PullContactsService;
import com.example.memories.volley.AppController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedFriendsList extends AppCompatActivity {

    private static final String TAG = "<SelectedFriendsList>";
    public static List<Contact> selectedList;
    public static SelectedFriendsListAdapter contactListViewAdapter;
    // handler and runnable are used to check(every 1 second) from PullContactsService.java whether all the contacts have been fetched
    Handler timerHandler = new Handler();
    private ActionBar actionBar;
    private ListView contactListView;
    private ProgressDialog mProgressDialog;
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (PullContactsService.isServiceFinished()) {
                //If fetching of contacts is finished, initialize the adapter and listview as well as remove the handler callback
                Log.d(TAG, "service is finished" + PullContactsService.isServiceFinished());
                timerHandler.removeCallbacks(timerRunnable);
                initializeData();
            } else {
                timerHandler.postDelayed(this, 10000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_selected_friends_list);
        Log.d(TAG, "entered Add friends");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Friends");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectedList = new ArrayList<Contact>();

        mProgressDialog = new ProgressDialog(this);

        contactListView = (ListView) findViewById(R.id.addFriendsList);

        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.memories.services.PullContactsService".equals(service.service.getClassName())) {
                isServiceRunning = true;

            }
        }
        if (!isServiceRunning) {
            //The service has already completed
            Log.d(TAG, "pull contacts service has already finished");
            initializeData();
        } else {
            //Service is not yet complete so start the handler to check if the service is finished (time period 1 second)
            Log.d(TAG, "pull contacts service is still running so waiting for the service to stop");
            mProgressDialog.setMessage("Please wait while we check who all from your contacts are on traveljar");
            mProgressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(false);
            timerHandler.postDelayed(timerRunnable, 0);
        }
    }

    private void initializeData() {
        mProgressDialog.dismiss();
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
        macTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNext() {
        // save all the selected friend's id in "buddyList" in AppCOntroller
        int len = selectedList.size();

        if (len > 0) {
            AppController.buddyList = new ArrayList<String>();
            for (int i = 0; i < len; i++) {
                AppController.buddyList.add(selectedList.get(i).getIdOnServer());
                Log.d(TAG, "ids are" + selectedList.get(i).getIdOnServer());
            }
        }

        Intent i = new Intent(getBaseContext(), NewJourneyDetail.class);
        startActivity(i);
    }

}