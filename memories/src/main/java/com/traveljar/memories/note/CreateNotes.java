package com.traveljar.memories.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

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
                HelpMe.getCurrentTime(), HelpMe.getCurrentTime(), null, lat, longi);
        Long id = NoteDataSource.createNote(note, this);
        note.setId(String.valueOf(id));

        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_NOTE, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if(HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }

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
                if(!mNoteContent.getText().toString().equals("")){
                    uploadAndSaveNote();
                    finish();
                }else {
                    Toast.makeText(this, "note cannot be empty", Toast.LENGTH_SHORT).show();
                }
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
