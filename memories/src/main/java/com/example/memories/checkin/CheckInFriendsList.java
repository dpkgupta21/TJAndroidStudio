package com.example.memories.checkin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.models.Contact;
import com.example.memories.moods.FriendsGridAdapter;
import com.example.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;


public class CheckInFriendsList extends Activity {

    private static final String TAG = "CHECKIN_FRIENDS_LIST";
    GridView mGridView;
    List<Contact> mContactsList;
    FriendsGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_buddy_list);

        JourneyDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));

        mGridView = (GridView) findViewById(R.id.checkin_buddy_gridview);

        mContactsList = ContactDataSource.getContactsFromCurrentJourney(this);
        mAdapter = new FriendsGridAdapter(this, mContactsList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView mOverlayImg = (ImageView) view.findViewById(R.id.overlayImg);
                Log.d(TAG, "item clicked " + position);
                if (mContactsList.get(position).isSelected()) {
                    mContactsList.get(position).setSelected(false);
                    mOverlayImg.setVisibility(View.GONE);
                } else {
                    mContactsList.get(position).setSelected(true);
                    mOverlayImg.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "Done").setIcon(R.drawable.ic_done)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                ArrayList<String> selectedContacts = new ArrayList<String>();
                for (Contact contact : mContactsList) {
                    if (contact.isSelected()) {
                        selectedContacts.add(contact.getIdOnServer());
                    }
                }
                if (selectedContacts.size() > 0) {
                    Intent returnIntent = new Intent();
                    returnIntent.putStringArrayListExtra("SELECTED_FRIENDS", selectedContacts);
                    setResult(RESULT_OK, returnIntent);
                } else {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_CANCELED, returnIntent);
                }
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
