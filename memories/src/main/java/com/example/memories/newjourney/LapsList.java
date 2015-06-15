package com.example.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.customviews.MyFABView;
import com.example.memories.newjourney.adapters.LapsListAdapter;
import com.example.memories.services.CustomResultReceiver;
import com.example.memories.services.PullContactsService;
import com.example.memories.volley.AppController;

public class LapsList extends AppCompatActivity implements CustomResultReceiver.Receiver{

    protected static final String TAG = "<LapsList>";
    private LapsListAdapter lapsListViewAdapter;
    private ImageView noLapsPlaceholderImg;
    private ImageView getStartedImg;
    ListView lapsListView;
    private ImageButton mEditJourney;
    ProgressDialog mDialog;
    CustomResultReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_laps_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Travel Plan");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items_laps, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_next:
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
        startActivity(i);
    }

}