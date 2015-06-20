package com.traveljar.memories.currentjourney;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.currentjourney.adapters.JourneyInfoBuddiesListAdapter;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.pastjourney.PastJourneyList;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.JourneyUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.util.List;
import java.util.Random;

/**
 * Created by abhi on 29/05/15.
 */
public class JourneyInfo extends AppCompatActivity implements JourneyUtil.OnExitJourneyListener{

    private static final String TAG = "<JourneyInfo>";
    private static final int MENU_ADD_BUDDY_ID = 0;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private JourneyInfoBuddiesListAdapter mAdapter;

    private TextView journeyName;
    private TextView journeyCreatedBy;
    private Journey mJourney;
    private TextView journeyBuddyCount;

    private Button mExitGroup;
    private Button mEndJourney;
    private ImageView mCoverImage;

    private ProgressDialog mProgressDialog;


    List<Contact> allBuddiesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Info");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);

        journeyName = (TextView) findViewById(R.id.journey_info_journey_name);
        journeyCreatedBy = (TextView) findViewById(R.id.journey_info_created_by);
        journeyBuddyCount = (TextView) findViewById(R.id.journey_info_buddies_count);
        mJourney = JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(this));

        mExitGroup = (Button)findViewById(R.id.journey_info_exit_group);
        mEndJourney = (Button)findViewById(R.id.journey_info_end_journey);
        mCoverImage = (ImageView)findViewById(R.id.journey_info_cover_image);

        mProgressDialog = new ProgressDialog(this);

        setCoverImage();

        if(HelpMe.isAdmin(this)){
            Log.d(TAG, "user is admin");
            mExitGroup.setVisibility(View.GONE);
            mEndJourney.setVisibility(View.VISIBLE);
        }else {
            Log.d(TAG, "user is not admin");
            mExitGroup.setVisibility(View.VISIBLE);
            mEndJourney.setVisibility(View.GONE);
        }

        mExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(JourneyInfo.this)
                        .setTitle("Exit Group")
                        .setMessage("Are you sure you want to exit this journey")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mProgressDialog.show();
                                JourneyUtil.getInstance().setExitJourneyListener(JourneyInfo.this);
                                JourneyUtil.getInstance().exitJourney(JourneyInfo.this, TJPreferences.getUserId(JourneyInfo.this));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        mEndJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(JourneyInfo.this)
                        .setTitle("Finish Journey?")
                        .setMessage("Are you sure you finish this journey? You will not be able to resume the journey again. It will stop for all your friends in this journey as well. ")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mProgressDialog.setMessage("Please wait while we are processing your request");
                                JourneyUtil.endJourneyOnServer(JourneyInfo.this);
                                JourneyDataSource.updateJourneyStatus(JourneyInfo.this, TJPreferences.getActiveJourneyId(JourneyInfo.this), Constants.JOURNEY_STATUS_FINISHED);
                                Intent intent = new Intent(JourneyInfo.this, PastJourneyList.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        if (mJourney != null) {
            journeyName.setText(mJourney.getName());

            Contact journeyCreatedByContact = ContactDataSource.getContactById(this, mJourney.getCreatedBy());
            if (journeyCreatedByContact != null) {
                Log.d(TAG, "journey is created by " + journeyCreatedByContact.getName());
                journeyCreatedBy.setText("CREATED BY " + journeyCreatedByContact.getName());
            } else {
                Log.d(TAG, "unable to find contact with journey id " + mJourney.getIdOnServer());
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_buddies_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(HelpMe.isAdmin(this)){
            menu.add(0, MENU_ADD_BUDDY_ID, 0, "Add Friend").setIcon(R.drawable.add70).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case MENU_ADD_BUDDY_ID:
                Log.d(TAG, "action_add_buddy clicked!");
                Intent i = new Intent(this, JourneyInfoFriendsList.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int convertDpToPixels(int dp){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int)px;
    }

    private void setCoverImage(){
        int[] drawables = new int[]{R.drawable.img_journey_info_1, R.drawable.img_journey_info_2, R.drawable.img_journey_info_3, R.drawable.img_journey_info_4, R.drawable.img_journey_info_5, R.drawable.img_journey_info_6,};
        Random rand = new Random();
        // nextInt is normally exclusive of the top value so add 1 to make it inclusive
        int randomNum = rand.nextInt((5 - 0) + 1) + 0;
        mCoverImage.setImageResource(drawables[randomNum]);
    }

    @Override
    public void onResume(){
        allBuddiesList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        mRecyclerView.getLayoutParams().height = convertDpToPixels(allBuddiesList.size() * 90);
        journeyBuddyCount.setText(String.valueOf(allBuddiesList.size()));
        if(mAdapter == null){
            mAdapter = new JourneyInfoBuddiesListAdapter(allBuddiesList, this);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.updateList(allBuddiesList);
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public void onExitJourney(int resultCode, String contactId) {
        if(resultCode == 0) {
            mProgressDialog.dismiss();
            JourneyDataSource.deleteJourney(this, TJPreferences.getActiveJourneyId(this));
            Intent intent = new Intent(this, ActivejourneyList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            mProgressDialog.dismiss();
            Toast.makeText(this, "some error occured please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
