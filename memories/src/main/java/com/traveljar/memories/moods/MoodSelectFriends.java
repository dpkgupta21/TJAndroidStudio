package com.traveljar.memories.moods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

        setUpToolBar();

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Mood");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

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

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Select Friends");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoodSelectFriends.this.finish();
            }
        });

        toolbar.inflateMenu(R.menu.action_bar_with_done_icon);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_done:
                        Intent returnIntent = new Intent();
                        returnIntent.putParcelableArrayListExtra("FRIENDS", (ArrayList) mContactsList);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                        break;
                }
                return true;
            }
        });

/*        TextView done = (TextView)toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                returnIntent.putParcelableArrayListExtra("FRIENDS", (ArrayList) mContactsList);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });*/
    }

}