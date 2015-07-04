package com.traveljar.memories.gallery;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.customviews.DividerItemDecoration;
import com.traveljar.memories.gallery.adapters.NoteGalleryAdapter;
import com.traveljar.memories.models.Memories;

import java.util.ArrayList;
import java.util.List;

public class GalleryNotes extends AppCompatActivity {

    private static final String TAG = "<GalleryNotes>";
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NoteGalleryAdapter mAdapter;
    private List<Memories> mNotesList;
    private LinearLayout mLayout;
    private String journeyId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_notes);
        Log.d(TAG, "enetred gallery notes !!");

        journeyId = getIntent().getStringExtra("JOURNEY_ID");
        setUpToolBar();

        mRecyclerView = (RecyclerView) findViewById(R.id.galleryNoteRecyclerView);
        // Add a divider between all list items ( not possible through XML like LIstVIew )
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayout = (LinearLayout)findViewById(R.id.layout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mNotesList = NoteDataSource.getAllNotesList(this, journeyId);
        Log.d(TAG, "total notes fetched " + mNotesList.size());

        // specify an adapter (see also next example)
        mAdapter = new NoteGalleryAdapter((ArrayList)mNotesList);

        if(mNotesList != null && mNotesList.size() > 0 ){
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mLayout.setBackgroundResource(R.drawable.img_no_note);
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText(JourneyDataSource.getJourneyById(this, journeyId).getName());
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
