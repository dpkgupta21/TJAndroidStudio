package com.traveljar.memories.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.gallery.adapters.NoteGalleryAdapter;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

public class GalleryNotesFragment extends Fragment {

    private static final String TAG = "<GalleryNotesFragment>";
    private RecyclerView mRecyclerView;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private NoteGalleryAdapter mAdapter;
    private List<Memories> mNotesList;
    private RelativeLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_notes, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "enetred gallery photos fragment!!");
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.galleryNoteRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mLayout = (RelativeLayout)rootView.findViewById(R.id.layout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mNotesList = NoteDataSource.getAllNotesList(
                getActivity(), TJPreferences.getActiveJourneyId(getActivity()));

        // specify an adapter (see also next example)
        mAdapter = new NoteGalleryAdapter((ArrayList)mNotesList);


        if(mNotesList != null && mNotesList.size() > 0 ){
            mRecyclerView.setAdapter(mAdapter);
        }else {
            mLayout.setBackgroundResource(R.drawable.img_no_note);
        }

    }

}
