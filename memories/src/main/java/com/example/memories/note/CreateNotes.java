package com.example.memories.note;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Note;
import com.example.memories.services.GPSTracker;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.NotesUtil;
import com.example.memories.utility.TJPreferences;

public class CreateNotes extends AppCompatActivity {

    private static final String TAG = "<CreateNotes>";
    EditText mNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_capture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Note");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mNoteContent = (EditText) findViewById(R.id.noteContent);
    }

    private void uploadAndSaveNote() {
        Log.d(TAG, "=" + TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "=" + mNoteContent.getText().toString().trim());
        Log.d(TAG, "=" + TJPreferences.getUserId(this));
        Log.d(TAG, "=" + System.currentTimeMillis());

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

        Note note = new Note("", TJPreferences.getActiveJourneyId(this), HelpMe.NOTE_TYPE, "Note",
                mNoteContent.getText().toString().trim(), TJPreferences.getUserId(this),
                System.currentTimeMillis(), System.currentTimeMillis(), null, lat, longi);
        NoteDataSource.createNote(note, this);
        NotesUtil.uploadNotes(note, this);
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
                Log.d(TAG, "done clicked!");
                uploadAndSaveNote();
                /*Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
