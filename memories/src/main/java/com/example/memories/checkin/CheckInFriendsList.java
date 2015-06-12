package com.example.memories.checkin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.memories.R;
import com.example.memories.checkin.adapter.CheckInFriendsListAdapter;
import com.example.memories.models.Contact;

import java.util.ArrayList;
import java.util.List;


public class CheckInFriendsList extends AppCompatActivity {

    private static final String TAG = "CHECKIN_FRIENDS_LIST";
    GridView mGridView;
    List<Contact> mContactsList;
    CheckInFriendsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_buddy_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Friends");
        setSupportActionBar(toolbar);

        mContactsList = getIntent().getExtras().getParcelableArrayList("FRIENDS");
        // To avoid Nullpoiner exception in adapter
        if (mContactsList == null) {
            mContactsList = new ArrayList<>();
        }

        mGridView = (GridView) findViewById(R.id.checkin_buddy_gridview);

        mAdapter = new CheckInFriendsListAdapter(this, mContactsList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
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
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("FRIENDS", (ArrayList) mContactsList);
                Log.d(TAG, "from friends list" + mContactsList);
                setResult(RESULT_OK, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
