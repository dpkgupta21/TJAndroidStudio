package com.traveljar.memories.moods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.moods.adapters.SelectMoodsDialog;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

public class MoodCapture extends AppCompatActivity implements SelectMoodsDialog.OnEmoticonSelectListener {

    private static final String TAG = "<MoodCapture>";
    private LinearLayout moodDetailLayout;
    private static int PICK_CONTACTS = 1;
    TextView noFriendsSelectedTxt;
    private ImageButton selectMoodImgBtn;
    private TextView moodText;
    private EditText moodReasonEditTxt;
    private List<Contact> mContactsList;
    private List<Contact> selectedFriends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_capture);

        setUpToolBar();

        noFriendsSelectedTxt = (TextView) findViewById(R.id.noFriendsSelected);
        selectMoodImgBtn = (ImageButton) findViewById(R.id.mood_select_mood_imgbtn);
        moodText = (TextView) findViewById(R.id.mood_text);
        moodReasonEditTxt = (EditText) findViewById(R.id.mood_because_of_txt);
        moodDetailLayout = (LinearLayout) findViewById(R.id.mood_detail_layout);

        // To remove focus from reason editText when clicked outside this box
        moodDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        mContactsList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "buddies in journey are " + mContactsList.size());
    }

    private void setSelectedFriends() {
        for (Contact contact : mContactsList) {
            if (contact.isSelected()) {
                selectedFriends.add(contact);
            }
        }
        if (selectedFriends != null) {
            if (selectedFriends.size() == 0) {
                Log.d(TAG, "no selected friends");
                noFriendsSelectedTxt.setText("No friend Selected");
            } else if (selectedFriends.size() == 1) {
                noFriendsSelectedTxt.setText(selectedFriends.get(0).getProfileName());
            } else if (selectedFriends.size() > 1) {
                noFriendsSelectedTxt.setText(selectedFriends.get(0).getProfileName() + " and " + (selectedFriends.size() - 1) + " others");
            }
        }
    }

    public void selectFriends(View v) {
        Intent intent = new Intent(getBaseContext(), MoodSelectFriends.class);
        intent.putParcelableArrayListExtra("FRIENDS", mContactsList == null ? null : (ArrayList) mContactsList);
        startActivityForResult(intent, PICK_CONTACTS);
    }

    public void selectMood(View v) {
        SelectMoodsDialog dialog = new SelectMoodsDialog();
        dialog.show(getFragmentManager(), "Show Moods");
    }

    private void createNewMoodIntoDB() {
        Log.d(TAG, "creating a new mood in local DB");

        String j_id = TJPreferences.getActiveJourneyId(this);
        String user_id = TJPreferences.getUserId(this);

        //Getting the contact ids of the selected contacts
        List<String> selectedFriends = new ArrayList<>();
        for (Contact contact : mContactsList) {
            if (contact.isSelected()) {
                selectedFriends.add(contact.getIdOnServer());
            }
        }

        Double lat = 0.0d;
        Double longi = 0.0d;
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude(); // returns latitude
            longi = gps.getLongitude(); // returns longitude
        } else {
            Toast.makeText(getApplicationContext(), "Network issues. Try later.",
                    Toast.LENGTH_LONG).show();
        }

        Mood newMood = new Mood(null, j_id, HelpMe.MOOD_TYPE, selectedFriends, moodText
                .getText().toString(), moodReasonEditTxt.getText().toString(), user_id,
                HelpMe.getCurrentTime(), HelpMe.getCurrentTime(), null, lat, longi);

        Long id = MoodDataSource.createMood(newMood, this);
        newMood.setId(String.valueOf(id));

        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_MOOD, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if(HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_next);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MoodCapture.this.finish();
            }
        });

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Mood");

        TextView done = (TextView)toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.action_done:
                        if (selectedFriends.size() == 0) {
                            Toast.makeText(MoodCapture.this, "please select at least one friend", Toast.LENGTH_SHORT)
                                    .show();
                        } else if (moodReasonEditTxt.getText().toString() == null) {
                            Toast.makeText(MoodCapture.this, "please write the reason for the mood", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            createNewMoodIntoDB();
                            finish();
                        }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_with_done_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_done:
                if (selectedFriends.size() == 0) {
                    Toast.makeText(this, "please select at least one friend", Toast.LENGTH_SHORT)
                            .show();
                } else if (moodReasonEditTxt.getText().toString() == null) {
                    Toast.makeText(this, "please write the reason for the mood", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    createNewMoodIntoDB();
/*                    Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
                    finish();
                }
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "on activity result result code is " + resultCode + (resultCode == RESULT_OK));
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mContactsList = data.getParcelableArrayListExtra("FRIENDS");
            setSelectedFriends();
        }
    }

    @Override
    public void onEmoticonSelect(String name, int emoticonId) {
        selectMoodImgBtn.setImageResource(emoticonId);
        moodText.setText(name);
    }

}
