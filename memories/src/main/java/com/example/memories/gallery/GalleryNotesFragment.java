package com.example.memories.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.gallery.adapters.NoteGalleryAdapter;
import com.example.memories.models.Memories;
import com.example.memories.utility.TJPreferences;

import java.util.ArrayList;

public class GalleryNotesFragment extends Fragment {

    private static final String TAG = "<GalleryNotesFragment>";
    private RecyclerView mRecyclerView;
    private View rootView;
    private LinearLayoutManager mLayoutManager;
    private NoteGalleryAdapter mAdapter;

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

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new NoteGalleryAdapter((ArrayList<Memories>) NoteDataSource.getAllNotesList(
                getActivity(), TJPreferences.getActiveJourneyId(getActivity())));
        mRecyclerView.setAdapter(mAdapter);

    }

}
