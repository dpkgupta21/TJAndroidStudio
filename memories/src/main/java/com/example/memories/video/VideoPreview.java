package com.example.memories.video;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.VideoDataSource;
import com.example.memories.models.Video;
import com.example.memories.services.GPSTracker;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;
import com.example.memories.utility.VideoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoPreview extends AppCompatActivity {

    private static final String TAG = "<VideoDetail>";
    List<String> likedBy;
    private ImageView video;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private TextView place;
    private TextView weather;
    private EditText caption;
    private ImageView mProfileImg;
    private ImageButton mFavBtn;
    private long currenTime;
    private String videoPath;
    private Video mVideo;
    private TextView noLikesTxt;
    private ProgressDialog pDialog;
    private TextView createdByName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_preview);
        Log.d(TAG, "entrerd video details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Video");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        currenTime = HelpMe.getCurrentTime();
        video = (ImageView) findViewById(R.id.thumbnail);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.video_detail_caption);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        createdByName = (TextView) findViewById(R.id.photo_detail_profile_name);

        String thumbnailPath;
        Bundle extras = getIntent().getExtras();
        videoPath = extras.getString("VIDEO_PATH");
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
            Toast.makeText(getApplicationContext(), "Network issues. Try later.",
                    Toast.LENGTH_LONG).show();
        }
        mVideo = new Video(null, TJPreferences.getActiveJourneyId(this), HelpMe.VIDEO_TYPE, caption.getText().toString()
                .trim(), "png", 1223, null, videoPath, TJPreferences.getUserId(this), currenTime, currenTime, null, thumbnailPath, lat, longi);


        video.setImageBitmap(BitmapFactory.decodeFile(thumbnailPath));
        try {
            if (TJPreferences.getProfileImgPath(this) != null) {
                Bitmap profileBitmap = HelpMe.decodeSampledBitmapFromPath(this, TJPreferences.getProfileImgPath(this), 100, 100);
                mProfileImg.setImageBitmap(profileBitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setFavouriteBtnClickListener();
        setThumbnailClickListener();

        // set other details
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat fullTime = new SimpleDateFormat("hh:mm aaa, EEE");
        Date resultdate = new Date(currenTime);

        dateBig.setText(onlyDate.format(resultdate).toString());
        date.setText(fullDate.format(resultdate).toString());
        time.setText(fullTime.format(resultdate).toString());
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
        /*Intent intent = new Intent(this, CurrentJourneyBaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
        finish();
    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> likedBy = mVideo.getLikedBy();
                if (likedBy == null) {
                    likedBy = new ArrayList<>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(VideoPreview.this));
                if (likedBy.contains(TJPreferences.getUserId(VideoPreview.this))) {
                    likedBy.remove(TJPreferences.getUserId(VideoPreview.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(VideoPreview.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mVideo.setLikedBy(likedBy);
                mVideo.updateLikedBy(VideoPreview.this, mVideo.getId(), likedBy);
            }
        });
    }

    private void saveAndUploadVideo() {
        Log.d(TAG, "creating a new video in local DB");
        if (likedBy != null) {
            mVideo.setLikedBy(likedBy);
        }
        mVideo.setCaption(caption.getText().toString());
        VideoDataSource.createVideo(mVideo, this);
        VideoUtil.uploadVideo(this, mVideo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_with_done_only, menu);
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
    }
}
