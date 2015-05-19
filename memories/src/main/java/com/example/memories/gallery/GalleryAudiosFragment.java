package com.example.memories.gallery;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.gallery.adapters.AudioGalleryAdapter;
import com.example.memories.models.Audio;

import java.io.IOException;
import java.util.List;

public class GalleryAudiosFragment extends Fragment {

    private static final String TAG = "GalleryAudioFragment";
    private View rootView;
    private ImageButton mPlayBtn;
    private ImageButton mPreviousBtn;
    private ImageButton mNextBtn;
    private List<Audio> mAudioList;
    private AudioGalleryAdapter mAdapter;
    private GridView mGridView;
    private int lastAudio = 0;

    private MediaPlayer mPlayer = null;
    private String lastPlayed;
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
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mAudioList = AudioDataSource.getAllAudios(getActivity());
        Log.d(TAG, "audio list size " + mAudioList.size());
        mAdapter = new AudioGalleryAdapter(getActivity(), mAudioList);
        mNextBtn = (ImageButton) rootView.findViewById(R.id.next_audio);
        mPlayBtn = (ImageButton) rootView.findViewById(R.id.play_audio);
        mPreviousBtn = (ImageButton) rootView.findViewById(R.id.previous_audio);

        mGridView.setAdapter(mAdapter);

        if (mAudioList.size() > 0) {
            lastPlayed = mAudioList.get(0).getDataLocalURL();

            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (isPlaying) {
                        stopPlaying();
                    }
                    startPlaying(mAudioList.get(position).getDataLocalURL());
                    lastAudio = position;
                    mPlayBtn.setImageResource(R.drawable.ic_pause_black);
                }
            });

            mPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        mPlayBtn.setImageResource(R.drawable.ic_play_black);
                        stopPlaying();
                        isPlaying = !isPlaying;
                    }
                }
            });

            mPreviousBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastAudio > 0) {
                        startPlaying(mAudioList.get(lastAudio - 1).getDataLocalURL());
                        lastAudio -= 1;
                    }
                }
            });

            mNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (lastAudio < mAudioList.size() - 1) {
                        startPlaying(mAudioList.get(lastAudio + 1).getDataLocalURL());
                        lastAudio += 1;
                    }
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
