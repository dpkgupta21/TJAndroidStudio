package com.traveljar.memories.pastjourney;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.pastjourney.adapters.PastJourneyListAdapter;

public class PastJourneyList extends BaseActivity {

    private static final String TAG = null;
    private PastJourneyListAdapter pastJourneyListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.past_journey_list);

        setUpToolBar();

        Cursor c = JourneyDataSource.getAllPastJourneys(this);

        Log.d(TAG, "cursor length is " + c.getCount());

        ListView pastJourneyListView = (ListView) findViewById(R.id.pastJourneyList);
        pastJourneyListViewAdapter = new PastJourneyListAdapter(getBaseContext(), c);
        pastJourneyListView.setAdapter(pastJourneyListViewAdapter);
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Past Journeys");
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, ActivejourneyList.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
