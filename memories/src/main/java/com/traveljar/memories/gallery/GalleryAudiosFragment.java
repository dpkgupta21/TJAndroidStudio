package com.traveljar.memories.gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.gallery.adapters.AudioGalleryAdapter;
import com.traveljar.memories.models.Audio;

import java.util.List;

public class GalleryAudiosFragment extends Fragment {

    private static final String TAG = "GalleryAudioFragment";
    private View rootView;
    private List<Audio> mAudioList;
    private AudioGalleryAdapter mAdapter;
    private ListView mListView;
    private RelativeLayout mLayout;

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
        mLayout = (RelativeLayout)rootView.findViewById(R.id.gallery_audio_layout);

        mAudioList = AudioDataSource.getAllAudios(getActivity());
        if(mAudioList.size() == 0){
            mLayout.setBackgroundResource(R.drawable.img_no_audio);
        }else {
            mAdapter = new AudioGalleryAdapter(getActivity(), mAudioList);
            mListView.setAdapter(mAdapter);
            mLayout.setBackgroundColor(getResources().getColor(R.color.white));
        }
        Log.d(TAG, "audio list size " + mAudioList.size());

    }
}
