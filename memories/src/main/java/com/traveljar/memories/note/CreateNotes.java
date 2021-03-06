package com.traveljar.memories.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

        setUpToolBar();

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
            Log.d(TAG, "netowrk availabkle...so making a create note request with note id = " + note.getId());
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("New Note");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNotes.this.finish();
            }
        });
        toolbar.inflateMenu(R.menu.action_bar_with_done_icon);
        TextView done = (TextView)toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mNoteContent.getText().toString().equals("")){
                    uploadAndSaveNote();
                    finish();
                }else {
                    Toast.makeText(CreateNotes.this, "note cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

/*    @Override
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
    }*/
}
