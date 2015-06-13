package com.example.memories.currentjourney;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.currentjourney.adapters.JourneyInfoBuddiesListAdapter;
import com.example.memories.models.Contact;
import com.example.memories.models.Journey;
import com.example.memories.pastjourney.PastJourneyList;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by abhi on 29/05/15.
 */
public class JourneyInfo extends AppCompatActivity {

    private static final String TAG = "<JourneyInfo>";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_journey_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Info");
        setSupportActionBar(toolbar);

        journeyName = (TextView) findViewById(R.id.journey_info_journey_name);
        journeyCreatedBy = (TextView) findViewById(R.id.journey_info_created_by);
        journeyBuddyCount = (TextView) findViewById(R.id.journey_info_buddies_count);
        mJourney = JourneyDataSource.getJourneyById(this, TJPreferences.getActiveJourneyId(this));

        mExitGroup = (Button)findViewById(R.id.journey_info_exit_group);
        mEndJourney = (Button)findViewById(R.id.journey_info_end_journey);
        mCoverImage = (ImageView)findViewById(R.id.journey_info_cover_image);

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
                        .setMessage("Under Construction")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
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
                                endJourneyOnServer();
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

        List<Contact> allBuddiesList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "total buddies are = " + allBuddiesList.size());


        journeyBuddyCount.setText(String.valueOf(allBuddiesList.size()));
        mRecyclerView = (RecyclerView) findViewById(R.id.journey_info_buddies_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new JourneyInfoBuddiesListAdapter(allBuddiesList);
        mRecyclerView.getLayoutParams().height = convertDpToPixels(allBuddiesList.size() * 110);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.journey_info_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_add_buddy:
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

    private void endJourneyOnServer(){
        String url = Constants.URL_CREATE_JOURNEY + "/" +TJPreferences.getActiveJourneyId(this);
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(this));
        params.put("journey[completed_at]", "2015-06-09T06:09:06.258Z");

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "journey with id " + TJPreferences.getActiveJourneyId(JourneyInfo.this) + "completed successfully on server" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "journey could not be completed at server" + error);
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest);
    }

}
