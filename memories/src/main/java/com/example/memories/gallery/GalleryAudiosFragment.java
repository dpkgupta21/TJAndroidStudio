package com.example.memories.gallery;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.gallery.adapters.AudioGalleryAdapter;
import com.example.memories.models.Audio;

import java.io.IOException;
import java.util.List;

public class GalleryAudiosFragment extends Fragment {

    private static final String TAG = "GalleryAudioFragment";
    private View rootView;
    private List<Audio> mAudioList;
    private AudioGalleryAdapter mAdapter;
    private ListView mListView;
    private ImageView pauseAudio;
    private ImageView playAudio;
    private MediaPlayer mPlayer = null;
    private boolean isPlaying = false;

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
        playAudio = (ImageView) rootView.findViewById(R.id.gallery_audio_list_item_play);
        pauseAudio = (ImageView) rootView.findViewById(R.id.gallery_audio_list_item_pause);

        mAudioList = AudioDataSource.getAllAudios(getActivity());
        Log.d(TAG, "audio list size " + mAudioList.size());
        mAdapter = new AudioGalleryAdapter(getActivity(), mAudioList);

        mListView.setAdapter(mAdapter);

        if (mAudioList.size() > 0) {

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isPlaying) {
                        stopPlaying();
                    }
                    startPlaying(mAudioList.get(position).getDataLocalURL());
                    pauseAudio.setVisibility(View.VISIBLE);
                    playAudio.setVisibility(View.INVISIBLE);
                }
            });
        }

    }

    private void startPlaying(String filePath) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

}
