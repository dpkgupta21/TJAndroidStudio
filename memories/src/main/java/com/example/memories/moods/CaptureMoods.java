package com.example.memories.moods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.models.Mood;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.MoodUtil;
import com.example.memories.utility.TJPreferences;

import java.util.List;

public class CaptureMoods extends Activity implements SelectMoodsDialog.OnEmoticonSelectListener {

    private static final String TAG = "<CaptureMoods>";
    private static int PICK_CONTACTS = 1;
    TextView noFriendsSelectedTxt;
    private List<String> selectedFriendsList;
    private ImageButton selectMoodImgBtn;
    private TextView moodText;
    private EditText moodReasonEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_mood);

        noFriendsSelectedTxt = (TextView) findViewById(R.id.noFriendsSelected);
        selectMoodImgBtn = (ImageButton) findViewById(R.id.mood_select_mood_imgbtn);
        moodText = (TextView) findViewById(R.id.mood_text);
        moodReasonEditTxt = (EditText) findViewById(R.id.mood_because_of_txt);
    }

    public void selectFriends(View v) {
        Intent i = new Intent(getBaseContext(), SelectFriends.class);
        startActivityForResult(i, PICK_CONTACTS);
    }

    public void selectMood(View v) {
        SelectMoodsDialog dialog = new SelectMoodsDialog();
        dialog.show(getFragmentManager(), "Show Moods");
    }

    private void createNewMoodIntoDB() {
        Log.d(TAG, "creating a new mood in local DB");

        String j_id = TJPreferences.getActiveJourneyId(this);
        String user_id = TJPreferences.getUserId(this);

        Mood newMood = new Mood(null, j_id, HelpMe.MOOD_TYPE, selectedFriendsList, moodText
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
                if (selectedFriendsList == null || selectedFriendsList.size() == 0) {
                    Toast.makeText(this, "please select at least one friend", Toast.LENGTH_SHORT)
                            .show();
                } else if (moodReasonEditTxt.getText().toString() == null) {
                    Toast.makeText(this, "please write the reason for the mood", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    createNewMoodIntoDB();
                    Intent i = new Intent(getBaseContext(), Timeline.class);
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
            if (resultCode == RESULT_OK) {
                selectedFriendsList = data.getStringArrayListExtra("SELECTED_FRIENDS");
                noFriendsSelectedTxt.setText(selectedFriendsList.size() + " friends selected");
            }
            if (resultCode == RESULT_CANCELED) {
                noFriendsSelectedTxt.setText(selectedFriendsList == null ? "No friends selected"
                        : "" + selectedFriendsList.size() + " friends selected");
            }
        }
    }

    @Override
    public void onEmoticonSelect(String name, int emoticonId) {
        selectMoodImgBtn.setImageResource(emoticonId);
        moodText.setText(name);
    }

}
