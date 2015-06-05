package com.example.memories.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.gallery.adapters.AudioGalleryAdapter;
import com.example.memories.models.Audio;

import java.util.List;

public class GalleryAudiosFragment extends Fragment {

    private static final String TAG = "GalleryAudioFragment";
    private View rootView;
    private List<Audio> mAudioList;
    private AudioGalleryAdapter mAdapter;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.gallery_audios, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (ListView) rootView.findViewById(R.id.gallery_audio_listview);

        mAudioList = AudioDataSource.getAllAudios(getActivity());
        Log.d(TAG, "audio list size " + mAudioList.size());
        mAdapter = new AudioGalleryAdapter(getActivity(), mAudioList);

        mListView.setAdapter(mAdapter);

    }
}
