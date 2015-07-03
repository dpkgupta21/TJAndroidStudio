package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.customviews.MyFABView;
import com.traveljar.memories.newjourney.adapters.LapsListAdapter;
import com.traveljar.memories.services.PullContactsService;
import com.traveljar.memories.volley.AppController;

import de.greenrobot.event.EventBus;

public class LapsList extends AppCompatActivity {

    protected static final String TAG = "<LapsList>";
    private LapsListAdapter lapsListViewAdapter;
    private ImageView noLapsPlaceholderImg;
    private ImageView getStartedImg;
    ListView lapsListView;
    ProgressDialog mDialog;
    //CustomResultReceiver mReceiver;

    // For the request bus receive event to discard the received event which is not meant for this activity
    private static int ACTIVITY_CODE = 2;

    //Id for the menu item 'next'
    private static final int ID_ACTION_ITEM_NEXT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_laps_list);

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Travel Itinerary");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(100);*/
        setUpToolBar();

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

        if(AppController.lapList.size() == 0){
            noLapsPlaceholderImg.setVisibility(View.VISIBLE);
            getStartedImg.setVisibility(View.VISIBLE);
            lapsListView.setVisibility(View.GONE);
        } else {
            lapsListViewAdapter = new LapsListAdapter(this, AppController.lapList);
            lapsListView.setAdapter(lapsListViewAdapter);
        }
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Travel Itinerary");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LapsList.this.finish();
            }
        });

        TextView next = (TextView)toolbar.findViewById(R.id.action_done);
        next.setText("NEXT");
        if(AppController.lapList.size() == 0){
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNext();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(AppController.lapList.size() > 0) {
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
//        mReceiver = new CustomResultReceiver(new Handler());
//        mReceiver.setReceiver(this);
        Intent intent = new Intent(getBaseContext(), PullContactsService.class);
//        intent.putExtra("RECEIVER", mReceiver);
        intent.putExtra("ACTIVITY_CODE", ACTIVITY_CODE);
        startService(intent);
        mDialog.show();

    }

/*    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        mDialog.dismiss();
        Intent i = new Intent(getBaseContext(), SelectedFriendsList.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }*/

    @Override
    public void onResume(){
        //Invalidate the menu for the visibility of the next option in the menu
        invalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onBackPressed(){
//        LapDataSource.deleteLapsList(this, AppController.lapList);
        AppController.lapList.clear();
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEvent(ContactsFetchEvent event){
        //Discard the event if the event's activity code is not similar to its own activity code
        if(event.getActivityCode() == ACTIVITY_CODE) {
            mDialog.dismiss();
            Intent i = new Intent(getBaseContext(), SelectedFriendsList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

}