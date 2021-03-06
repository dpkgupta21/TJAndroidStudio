package com.traveljar.memories.audio;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.retrofit.TravelJarServices;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

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
    private boolean recording = false;
    private boolean playing = false;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private long createdAt;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.audio_capture);

        setUpToolBar();

        String dirPath = Constants.TRAVELJAR_FOLDER_AUDIO;
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        createdAt = HelpMe.getCurrentTime();
        mFileName = dirPath + "/aud_" + TJPreferences.getUserId(this) + "_" + TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".3gp";

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


        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.URL_BASE)
                .build();

        TravelJarServices service = restAdapter.create(TravelJarServices.class);
        service.uploadAudio(TJPreferences.getActiveJourneyId(this), new TypedString(TJPreferences.getApiKey(this)),
                new TypedString(String.valueOf(lat)), new TypedString(String.valueOf(longi)),
                new TypedString(TJPreferences.getUserId(this)),
                new TypedFile("audio/mpeg", new File(mFileName)),
                new Callback<JSONObject>() {
                    @Override
                    public void success(JSONObject object, Response response) {
                        Log.d(TAG, "retrofit response successful " + object);
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Log.d(TAG, "retrofit response error " + retrofitError);
                    }
                });


        Audio audio = new Audio(null, TJPreferences.getActiveJourneyId(this), HelpMe.AUDIO_TYPE,
                "3gp", (new File(mFileName)).length(), null, mFileName, TJPreferences.getUserId(this), createdAt,
                createdAt, null, audioDuration, lat, longi);
        Long id = AudioDataSource.createAudio(audio, this);
        audio.setId(String.valueOf(id));
        Log.d(TAG, "new audio added in local DB successfully");



        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_AUDIO, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if(HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }
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

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Audio");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioCapture.this.finish();
            }
        });
        toolbar.inflateMenu(R.menu.action_bar_with_done_icon);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_done:
                        saveAndUploadAudio();
                        finish();
                        break;
                }
                return true;
            }
        });
/*        TextView done = (TextView)toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndUploadAudio();
                finish();
            }
        });*/
    }
}