package com.traveljar.memories.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.gallery.adapters.VideoAlbumsGalleryAdapter;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Video;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GalleryVideoAlbumsFragment extends Fragment {
    private static final String TAG = "GalleryVideoAlbumsFrag";
    private static GridView mGridView;
    private View rootView;
    private List<Journey> mJourneysList;
    private Map<Journey, Video> mAlbumsList;
    private VideoAlbumsGalleryAdapter mAdapter;
    private LinearLayout mLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_albums, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d(TAG, "entered video gallery albums fragment!!");

        mGridView = (GridView) rootView.findViewById(R.id.albums_grid_view);

        populateAlbumsList();
        mAdapter = new VideoAlbumsGalleryAdapter(getActivity(), mAlbumsList, mJourneysList);

        mLayout = (LinearLayout)rootView.findViewById(R.id.gallery_albums_layout);

        if (mAlbumsList.size() > 0) {
            mGridView.setAdapter(mAdapter);
        }else {
            mLayout.setBackgroundResource(R.drawable.img_no_pic);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Intent intent  = new Intent(getActivity(), GalleryVideos.class);
                intent.putExtra("JOURNEY_ID", mJourneysList.get(position).getIdOnServer());
                startActivity(intent);
            }
        });

    }

    private void populateAlbumsList(){
        mAlbumsList = new HashMap<>();
        mJourneysList = JourneyDataSource.getAllActiveJourneys(getActivity());
        Collections.sort(mJourneysList);
        for(Journey journey : mJourneysList){
            mAlbumsList.put(journey, VideoDataSource.getRandomVideoOfJourney(journey.getIdOnServer(), getActivity()));
        }
    }
}
