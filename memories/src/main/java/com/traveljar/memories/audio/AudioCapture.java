package com.traveljar.memories.audio;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.utility.AudioUtil;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.IOException;

public class AudioCapture extends AppCompatActivity {

    private static final String TAG = "CaptureVoice";
    private static String mFileName = null;
    Handler timerHandler = new Handler();
    long startTime = 0;
    long audioDuration;
    private ProgressBar mProgressBar;
    private ImageButton mRecordBtn;
    private ImageButton mStopBtn;
    private ImageButton mPreviewBtn;
    private ImageButton mRetryBtn;
    private TextView timerView;
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
            audioDuration = seconds;
        }
    };
    /*private LinearLayout startRecording;
    private LinearLayout playRecording;
    private ImageView recordImg;
    private TextView recordTxt;
    private TextView playTxt;*/
    private boolean recording = false;
    private boolean playing = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;


    public AudioCapture() {

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
        setContentView(R.layout.audio_capture);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Audio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mRecordBtn = (ImageButton) findViewById(R.id.audio_capture_record_btn);
        mStopBtn = (ImageButton) findViewById(R.id.audio_capture_stop_btn);
        mPreviewBtn = (ImageButton) findViewById(R.id.audio_capture_preview_btn);
        mRetryBtn = (ImageButton) findViewById(R.id.audio_capture_retry_btn);
        timerView = (TextView) findViewById(R.id.timerView);

        setLayoutForAudioRecord();

        mRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutForAudioStop();
                if (!recording) {
                    Log.d("TAG", "condition 1");
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    startRecording();
                }
                recording = true;
            }
        });

        mStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                recording = false;
                timerHandler.removeCallbacks(timerRunnable);
                Log.d(TAG, "audio duration is " + audioDuration);
                stopRecording();
                setLayoutForAudioPreview();
            }
        });

        mPreviewBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playing) {
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    startPlaying();
                }
                playing = !playing;
            }
        });

        mRetryBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setLayoutForAudioRecord();
                if (playing) {
                    stopPlaying();
                    playing = false;
                }
                audioDuration = 0;
                timerHandler.removeCallbacks(timerRunnable);
                timerView.setText(String.format("00:00:00"));
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

        Double lat = 0.0d;
        Double longi = 0.0d;
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude(); // returns latitude
            longi = gps.getLongitude(); // returns longitude
        } else {
            Toast.makeText(getApplicationContext(), "Network issues. Try later.",
                    Toast.LENGTH_LONG).show();
        }
        Audio audio = new Audio(null, TJPreferences.getActiveJourneyId(this), HelpMe.AUDIO_TYPE,
                "3gp", (new File(mFileName)).length(), null, mFileName,
                TJPreferences.getUserId(this), HelpMe.getCurrentTime(),
                HelpMe.getCurrentTime(), null, audioDuration, lat, longi);
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
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playing = false;
                    timerHandler.removeCallbacks(timerRunnable);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void setLayoutForAudioRecord() {
        mRecordBtn.setVisibility(View.VISIBLE);
        mStopBtn.setVisibility(View.GONE);
        mPreviewBtn.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
    }

    private void setLayoutForAudioStop() {
        mRecordBtn.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.VISIBLE);
        mPreviewBtn.setVisibility(View.GONE);
        mRetryBtn.setVisibility(View.GONE);
    }

    private void setLayoutForAudioPreview() {
        mRecordBtn.setVisibility(View.GONE);
        mStopBtn.setVisibility(View.GONE);
        mPreviewBtn.setVisibility(View.VISIBLE);
        mRetryBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_done:
                saveAndUploadAudio();
                finish();
                return true;
            case android.R.id.home:
                this.finish();
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