package com.example.memories.moods;

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
import com.example.memories.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class SelectFriends extends Activity {

    public static final String TAG = "<SelectFriends>";
    GridView mGridView;
    List<String> mSelectedFriends;
    List<Contact> mContactsList = new ArrayList<Contact>();
    FriendsGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_friends_list);

        //JourneyDataSource.getBuddyIdsFromJourney(this, TJPreferences.getActiveJourneyId(this));

        mSelectedFriends = getIntent().getExtras().getStringArrayList("SELECTED_FRIENDS");
        mGridView = (GridView) findViewById(R.id.friends_list);

        if(mSelectedFriends == null) {
            mContactsList = new ArrayList<Contact>();
        }else if(mSelectedFriends.size() > 0){
            mContactsList = ContactDataSource.getContactsListFromIds(this, mSelectedFriends);
        }
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
                mSelectedFriends = new ArrayList<String>();
                for (Contact contact : mContactsList) {
                    if (!contact.isSelected()) {
                        mSelectedFriends.remove(contact.getIdOnServer());
                    }else{
                        if(!mSelectedFriends.contains(contact.getIdOnServer())){
                            mSelectedFriends.add(contact.getIdOnServer());
                        }
                    }
                }
                Intent returnIntent = new Intent();
                returnIntent.putStringArrayListExtra("SELECTED_FRIENDS", (ArrayList<String>)mSelectedFriends);
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}