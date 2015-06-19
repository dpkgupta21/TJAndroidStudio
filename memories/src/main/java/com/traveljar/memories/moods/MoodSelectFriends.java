package com.traveljar.memories.moods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.moods.adapters.FriendsGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class MoodSelectFriends extends AppCompatActivity {

    public static final String TAG = "<SelectFriends>";
    GridView mGridView;
    List<Contact> mContactsList;
    FriendsGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_select_friends_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Mood");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContactsList = getIntent().getExtras().getParcelableArrayList("FRIENDS");
        // To avoid Nullpoiner exception in adapter
        if (mContactsList == null) {
            mContactsList = new ArrayList<>();
        }

        if (mContactsList.size() == 0) {
            TextView noBuddiesMsg = (TextView) findViewById(R.id.friends_list_no_buddies_msg);
            noBuddiesMsg.setVisibility(View.VISIBLE);
        } else {
            mGridView = (GridView) findViewById(R.id.friends_list);
            mGridView.setVisibility(View.VISIBLE);

            Log.d(TAG, "buddies in journey are " + mContactsList.size());
            mAdapter = new FriendsGridAdapter(this, mContactsList);
            mGridView.setAdapter(mAdapter);

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageView mOverlayImg = (ImageView) view.findViewById(R.id.overlayImg);
                    Log.d(TAG, "item clicked " + position);
                    boolean selected = mContactsList.get(position).isSelected();
                    if (selected) {
                        mOverlayImg.setVisibility(View.GONE);
                    } else {
                        mOverlayImg.setVisibility(View.VISIBLE);
                    }
                    mContactsList.get(position).setSelected(!selected);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_with_done_only, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_done:
                Log.d(TAG, "done clicked!");
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("FRIENDS", (ArrayList) mContactsList);
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }*/

    // whenever the activity is called with a list ids of selected friends, fetch all the contacts from the current journey and
    // and mark the contacts as unselected whose ids are not present in the mSelectedFriends

}