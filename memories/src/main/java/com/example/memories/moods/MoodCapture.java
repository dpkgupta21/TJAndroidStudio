package com.example.memories.moods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.currentjourney.CurrentJourneyBaseActivity;
import com.example.memories.models.Mood;
import com.example.memories.moods.adapters.SelectMoodsDialog;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.MoodUtil;
import com.example.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoodCapture extends AppCompatActivity implements SelectMoodsDialog.OnEmoticonSelectListener {

    private static final String TAG = "<CaptureMoods>";
    private static int PICK_CONTACTS = 1;
    TextView noFriendsSelectedTxt;
    private ImageButton selectMoodImgBtn;
    private TextView moodText;
    private EditText moodReasonEditTxt;
    private List<String> mSelectedFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_capture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Mood");
        setSupportActionBar(toolbar);

        noFriendsSelectedTxt = (TextView) findViewById(R.id.noFriendsSelected);
        selectMoodImgBtn = (ImageButton) findViewById(R.id.mood_select_mood_imgbtn);
        moodText = (TextView) findViewById(R.id.mood_text);
        moodReasonEditTxt = (EditText) findViewById(R.id.mood_because_of_txt);
        String[] friendIds = JourneyDataSource.getBuddyIdsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        if (friendIds != null) {
            mSelectedFriends = Arrays.asList();
        }
    }

    private void setSelectedFriends() {
        if (mSelectedFriends != null) {
            if (mSelectedFriends.size() == 0) {
                noFriendsSelectedTxt.setText("No friend Selected");
            } else if (mSelectedFriends.size() == 1) {
                noFriendsSelectedTxt.setText(ContactDataSource.getContactById(this, mSelectedFriends.get(0)).getName());
            } else if (mSelectedFriends.size() > 1) {
                noFriendsSelectedTxt.setText(ContactDataSource.getContactById(this, mSelectedFriends.get(0)).getName() + " and " + (mSelectedFriends.size() - 1) + " others");
            }
        }
    }

    public void selectFriends(View v) {
        Intent intent = new Intent(getBaseContext(), MoodSelectFriends.class);
        intent.putStringArrayListExtra("SELECTED_FRIENDS", mSelectedFriends == null ? null : new ArrayList<String>(mSelectedFriends));
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

        Mood newMood = new Mood(null, j_id, HelpMe.MOOD_TYPE, mSelectedFriends, moodText
                .getText().toString(), moodReasonEditTxt.getText().toString(), user_id,
                HelpMe.getCurrentTime(), HelpMe.getCurrentTime(), null);

        MoodDataSource.createMood(newMood, this);
        MoodUtil.uploadMood(newMood, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_with_done_only, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_done:
                if (mSelectedFriends.size() == 0) {
                    Toast.makeText(this, "please select at least one friend", Toast.LENGTH_SHORT)
                            .show();
                } else if (moodReasonEditTxt.getText().toString() == null) {
                    Toast.makeText(this, "please write the reason for the mood", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    createNewMoodIntoDB();
                    Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            mSelectedFriends = data.getStringArrayListExtra("SELECTED_FRIENDS");
            setSelectedFriends();
        }
    }

    @Override
    public void onEmoticonSelect(String name, int emoticonId) {
        selectMoodImgBtn.setImageResource(emoticonId);
        moodText.setText(name);
    }

}
