package com.traveljar.memories.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class VideoPreview extends AppCompatActivity {

    private static final String TAG = "<VideoDetail>";
    private ImageView video;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private EditText caption;
    private ImageView mProfileImg;
    private long currenTime;
    private String videoPath;
    private Video mVideo;
    private ProgressDialog pDialog;
    private TextView createdByName;
    private long createdAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_preview);
        Log.d(TAG, "entrerd video details");

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Video");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        setUpToolBar();

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        currenTime = HelpMe.getCurrentTime();
        video = (ImageView) findViewById(R.id.thumbnail);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.video_detail_caption);
//        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
//        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        createdByName = (TextView) findViewById(R.id.photo_detail_profile_name);

        //Extract thumbnail and save it
        String thumbnailPath;
        Bundle extras = getIntent().getExtras();
        videoPath = extras.getString("VIDEO_PATH");
        createdAt = extras.getLong("CREATED_AT");
        Bitmap bitmap = HelpMe.getVideoThumbnail(videoPath);
        FileOutputStream out = null;
        thumbnailPath = Constants.TRAVELJAR_FOLDER_VIDEO + "vid_" + System.currentTimeMillis() + ".mp4";
        try {
            out = new FileOutputStream(thumbnailPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        createdByName.setText(TJPreferences.getUserName(this));

        Double lat = 0.0d;
        Double longi = 0.0d;
        GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            lat = gps.getLatitude(); // returns latitude
            longi = gps.getLongitude(); // returns longitude
        } else {
            Toast.makeText(getApplicationContext(), "Network issues. Try later.", Toast.LENGTH_LONG).show();
        }
        mVideo = new Video(null, TJPreferences.getActiveJourneyId(this), HelpMe.VIDEO_TYPE, caption.getText().toString()
                .trim(), "png", 1223, null, videoPath, TJPreferences.getUserId(this), createdAt, createdAt, null, thumbnailPath, lat, longi);

        video.setImageBitmap(BitmapFactory.decodeFile(thumbnailPath));
        try {
            if (TJPreferences.getProfileImgPath(this) != null) {
                Bitmap profileBitmap = HelpMe.decodeSampledBitmapFromPath(this, TJPreferences.getProfileImgPath(this), 100, 100);
                mProfileImg.setImageBitmap(profileBitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setThumbnailClickListener();

        dateBig.setText(HelpMe.getDate(mVideo.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mVideo.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mVideo.getCreatedAt(), HelpMe.TIME_ONLY));
    }

    private void setThumbnailClickListener() {
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "inside thumbnail onclick listener");
                Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(mVideo.getDataLocalURL())));
                mediaIntent.setDataAndType(Uri.fromFile(new File(mVideo.getDataLocalURL())), "video/*");
                startActivity(mediaIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void saveAndUploadVideo() {
/*        Log.d(TAG, "creating a new video in local DB");
        mVideo.setCaption(caption.getText().toString());
        long id = VideoDataSource.createVideo(mVideo, this);
        mVideo.setId(String.valueOf(id));
        Intent intent = new Intent(this, UploadVideoService.class);
        intent.putExtra("VIDEO", mVideo);
        startService(intent);*/
        mVideo.setCaption(caption.getText().toString());
        long id = VideoDataSource.createVideo(mVideo, this);
        mVideo.setId(String.valueOf(id));
        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_VIDEO, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if(HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Video Preview");

        toolbar.setNavigationIcon(R.drawable.ic_next);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPreview.this.finish();
            }
        });
        TextView done = (TextView) toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done clicked!");
                saveAndUploadVideo();
                finish();
            }
        });
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_with_done_text, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_done:
                Log.d(TAG, "done clicked!");
                saveAndUploadVideo();
                finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}
