package com.traveljar.memories.utility;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer {

    private static final String TAG = "AUDIO_PLAYER.JAVA";
    private MediaPlayer mPlayer = null;
    private String mFileName;
    private OnAudioCompleteListener completionListener;

    public AudioPlayer(String fileName, OnAudioCompleteListener completionListener) {
        mFileName = fileName;
        this.completionListener = completionListener;
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completionListener.onAudioComplete();
            }
        });
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public interface OnAudioCompleteListener{
        void onAudioComplete();
    }

}
