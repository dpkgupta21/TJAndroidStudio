package com.example.memories.newjourney;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.customviews.MyFABView;
import com.example.memories.newjourney.adapters.LapsListAdapter;
import com.example.memories.volley.AppController;

public class LapsList extends Activity {

    protected static final String TAG = "<LapsList>";
    private LapsListAdapter lapsListViewAdapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_laps_list);

        actionBar = getActionBar();
        actionBar.setTitle("Travel Itinerary");

        // Add lap FAB Button
        final MyFABView fabButton = new MyFABView.Builder(this)
                .withDrawable(getResources().getDrawable(R.drawable.plus79))
                .withButtonColor(getResources().getColor(R.color.tj_orange))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT).withMargins(0, 0, 16, 16).create();

        fabButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FAB clicked");
                Intent i = new Intent(getBaseContext(), AddLap.class);
                startActivity(i);

            }
        });

        ListView lapsListView = (ListView) findViewById(R.id.new_journey_location_listview);
        lapsListViewAdapter = new LapsListAdapter(this,
                ((AppController) getApplicationContext()).lapsList);
        lapsListView.setAdapter(lapsListViewAdapter);

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
        Intent i = new Intent(getBaseContext(), SelectedFriendsList.class);
        startActivity(i);
    }

}