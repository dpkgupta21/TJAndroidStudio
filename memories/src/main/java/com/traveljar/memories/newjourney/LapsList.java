package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.traveljar.memories.R;
import com.traveljar.memories.customviews.MyFABView;
import com.traveljar.memories.newjourney.adapters.LapsListAdapter;
import com.traveljar.memories.services.CustomResultReceiver;
import com.traveljar.memories.services.PullContactsService;
import com.traveljar.memories.volley.AppController;

public class LapsList extends AppCompatActivity implements CustomResultReceiver.Receiver{

    protected static final String TAG = "<LapsList>";
    private LapsListAdapter lapsListViewAdapter;
    private ImageView noLapsPlaceholderImg;
    private ImageView getStartedImg;
    ListView lapsListView;
    private ImageButton mEditJourney;
    ProgressDialog mDialog;
    CustomResultReceiver mReceiver;

    //Id for the menu item 'next'
    private static final int ID_ACTION_ITEM_NEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_laps_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Travel Itinerary");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(100);

        mEditJourney = (ImageButton) findViewById(R.id.edit_journey_lap);

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
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        lapsListView = (ListView) findViewById(R.id.new_journey_location_listview);
        noLapsPlaceholderImg = (ImageView) findViewById(R.id.no_laps_placeholder);
        getStartedImg = (ImageView) findViewById(R.id.no_laps_get_started);

        if (AppController.lapsList.size() == 0) {
            noLapsPlaceholderImg.setVisibility(View.VISIBLE);
            getStartedImg.setVisibility(View.VISIBLE);
            lapsListView.setVisibility(View.GONE);
        } else {
            lapsListViewAdapter = new LapsListAdapter(this, AppController.lapsList);
            lapsListView.setAdapter(lapsListViewAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(AppController.lapsList.size() > 0) {
            menu.add(0, ID_ACTION_ITEM_NEXT, 0, "Next").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case ID_ACTION_ITEM_NEXT:
                goToNext();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToNext() {
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Please wait while we are fetching your contacts");
        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(getBaseContext(), PullContactsService.class);
        intent.putExtra("RECEIVER", mReceiver);
        startService(intent);
        mDialog.show();

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mDialog.dismiss();
        Intent i = new Intent(getBaseContext(), SelectedFriendsList.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    @Override
    public void onResume(){
        //Invalidate the menu for the visibility of the next option in the menu
        invalidateOptionsMenu();
        super.onResume();
    }
}