package com.example.memories.audio;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.models.Audio;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.AudioUtil;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.io.File;
import java.io.IOException;

public class CaptureAudio extends Activity {

    private static final String TAG = "CaptureVoice";
    private static String mFileName = null;
    TextView timerView;
    Handler timerHandler = new Handler();
    long startTime = 0;
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            timerView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };
    private LinearLayout startRecording;
    private LinearLayout playRecording;
    private ImageView recordImg;
    private TextView recordTxt;
    private TextView playTxt;
    private boolean recording = false;
    private boolean playing = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    public CaptureAudio() {

        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/TravelJar/Audio";
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        mFileName = dirPath + "/voice_" + System.currentTimeMillis() + ".3gp";
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.capture_audio);

        startRecording = (LinearLayout) findViewById(R.id.startRecording);
        playRecording = (LinearLayout) findViewById(R.id.playRecording);
        timerView = (TextView) findViewById(R.id.timerView);
        playTxt = (TextView) findViewById(R.id.playTxt);
        recordImg = (ImageView) findViewById(R.id.recordImg);
        recordTxt = (TextView) findViewById(R.id.recordTxt);

        startRecording.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recording) {
                    Log.d("TAG", "condition 1");
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    recordTxt.setText("Stop Recording");
                    startRecording();
                } else {
                    Log.d("TAG", "condition 2");
                    recordTxt.setText("Start Recording");
                    timerHandler.removeCallbacks(timerRunnable);
                    stopRecording();
                }
                recording = !recording;
            }
        });

        playRecording.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playing) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    playTxt.setText("Stop playing");
                    startPlaying();
                } else {
                    playTxt.setText("Start playing");
                    timerHandler.removeCallbacks(timerRunnable);
                    stopPlaying();
                }
                playing = !playing;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void startRecording() {
        // using inbuilt media player
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    private void saveAndUploadAudio() {
        Audio audio = new Audio(null, TJPreferences.getActiveJourneyId(this), HelpMe.AUDIO_TYPE,
                "3gp", (new File(mFileName)).length(), null, mFileName,
                TJPreferences.getUserId(this), System.currentTimeMillis(),
                System.currentTimeMillis(), null);
        AudioDataSource.createAudio(audio, this);
        Log.d(TAG, "new video added in local DB successfully");
        AudioUtil.uploadAudio(this, audio);
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_done:
                saveAndUploadAudio();
                Intent i = new Intent(getBaseContext(), Timeline.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_with_done_only, menu);
        return super.onCreateOptionsMenu(menu);
    }
}