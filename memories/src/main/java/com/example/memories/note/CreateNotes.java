package com.example.memories.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.Note;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.NotesUtil;
import com.example.memories.utility.TJPreferences;

public class CreateNotes extends Activity {

    private static final String TAG = "<CreateNotes>";
    EditText mNoteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes);

        mNoteContent = (EditText) findViewById(R.id.noteContent);
    }

    private void uploadAndSaveNote() {
        Log.d(TAG, "=" + TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "=" + mNoteContent.getText().toString().trim());
        Log.d(TAG, "=" + TJPreferences.getUserId(this));
        Log.d(TAG, "=" + System.currentTimeMillis());

        Note note = new Note("", TJPreferences.getActiveJourneyId(this), HelpMe.NOTE_TYPE, "Note",
                mNoteContent.getText().toString().trim(), TJPreferences.getUserId(this),
                System.currentTimeMillis(), System.currentTimeMillis(), null);
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
                Intent i = new Intent(getBaseContext(), Timeline.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
