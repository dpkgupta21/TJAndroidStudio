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

public class JourneyInfo extends AppCompatActivity implements JourneyUtil.OnExitJourneyListener{

    private static final String TAG = "<JourneyInfo>";
    private RecyclerView mRecyclerView;
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

        List<Contact> contactList = ContactDataSource.getAllActiveContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "total contacts fetched from journey are " + contactList.size());
        for(Contact contact : contactList){
            Log.d(TAG, " " + contact);
        }

        setUpToolBar();
        journeyName = (TextView) findViewById(R.id.journey_info_journey_name);
        journeyCreatedBy = (TextView) findViewById(R.id.journey_info_created_by);
        journeyBuddyCount = (TextView) findViewById(R.id.journey_info_buddies_count);
        mJourney = JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(this));

        mExitGroup = (Button)findViewById(R.id.journey_info_exit_group);
        mEndJourney = (Button)findViewById(R.id.journey_info_end_journey);
        mCoverImage = (ImageView)findViewById(R.id.journey_info_cover_image);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCanceledOnTouchOutside(false);

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
                Log.d(TAG, "journey is created by " + journeyCreatedByContact.getProfileName());
                journeyCreatedBy.setText("CREATED BY " + journeyCreatedByContact.getProfileName());
            } else {
                Log.d(TAG, "unable to find contact with journey id " + mJourney.getIdOnServer());
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_buddies_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Journey Info");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JourneyInfo.this.finish();
            }
        });

        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        if(HelpMe.isAdmin(this)){
            toolbar.inflateMenu(R.menu.journey_info_with_add_buddy);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_buddy:
                        Log.d(TAG, "action_add_buddy clicked!");
                        Intent i = new Intent(JourneyInfo.this, JourneyInfoFriendsList.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }

    private int convertDpToPixels(int dp){
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int)px;
    }

    private void setCoverImage(){
        int[] drawables = new int[]{R.drawable.img_journey_info_1, R.drawable.img_journey_info_2, R.drawable.img_journey_info_3, R.drawable.img_journey_info_4, R.drawable.img_journey_info_5, R.drawable.img_journey_info_6,};
        Random rand = new Random();
        // nextInt is normally exclusive of the top value so add 1 to make it inclusive
        int randomNum = rand.nextInt((5) + 1);
        mCoverImage.setImageResource(drawables[randomNum]);
    }

    @Override
    public void onResume(){
        //allBuddiesList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        allBuddiesList = ContactDataSource.getAllActiveContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
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
            JourneyDataSource.updateUserActiveStatus(this, TJPreferences.getActiveJourneyId(this), false);
            Intent intent = new Intent(this, ActivejourneyList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }else {
            mProgressDialog.dismiss();
            Toast.makeText(this, "some error occured please try again later", Toast.LENGTH_SHORT).show();
        }
    }
}
