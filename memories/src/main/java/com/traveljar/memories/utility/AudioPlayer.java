package com.traveljar.memories.utility;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer {

    private static final String TAG = "AUDIO_PLAYER.JAVA";
    private MediaPlayer mPlayer = null;
    private String mFileName;

    public AudioPlayer(String fileName) {
        mFileName = fileName;
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
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

}
