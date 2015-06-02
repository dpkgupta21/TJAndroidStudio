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

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.VideoDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Video;
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

public class VideoDetail extends AppCompatActivity implements DownloadVideoAsyncTask.OnVideoDownloadListener{

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
    private boolean isNewVideo;
    private TextView noLikesTxt;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);
        Log.d(TAG, "entrerd video details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Video");
        setSupportActionBar(toolbar);

        pDialog = new ProgressDialog(this);

        currenTime = HelpMe.getCurrentTime();
        video = (ImageView) findViewById(R.id.thumbnail);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        place = (TextView) findViewById(R.id.photo_detail_place);
        weather = (TextView) findViewById(R.id.photo_detail_weather);
        caption = (EditText) findViewById(R.id.video_detail_caption);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        String thumbnailPath = null;
        Bundle extras = getIntent().getExtras();
        //If the activity is started for an already clicked video
        if (extras.getString("VIDEO_ID") != null) {
            Log.d(TAG, "running for an already existing video");
            mVideo = VideoDataSource.getVideoById(extras.getString("VIDEO_ID"), this);
            videoPath = mVideo.getDataLocalURL(); //path to image
            thumbnailPath = mVideo.getLocalThumbPath();
            //setup the state of favourite button
            if (mVideo.getLikedBy() == null){
                noLikesTxt.setText("0");
                mFavBtn.setImageResource(R.drawable.heart_empty);
            }else{
                noLikesTxt.setText(String.valueOf(mVideo.getLikedBy().size()));
                if (mVideo.getLikedBy().contains(TJPreferences.getUserId(VideoDetail.this))){
                    mFavBtn.setImageResource(R.drawable.heart_full);
                }else {
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                }
            }
        }
        //If the activity is started for a newly clicked picture
        if (extras.getString("VIDEO_PATH") != null) {
            Log.d(TAG, "running for a already clicked video");
            videoPath = extras.getString("VIDEO_PATH");
            Bitmap bitmap = HelpMe.getVideoThumbnail(videoPath);
            FileOutputStream out = null;
            thumbnailPath = Constants.TRAVELJAR_FOLDER_VIDEO + "vid_" + System.currentTimeMillis() + ".mp4";
            try {
                out = new FileOutputStream(thumbnailPath);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
            isNewVideo = true;
            videoPath = extras.getString("VIDEO_PATH");
            mVideo = new Video(null, TJPreferences.getActiveJourneyId(this), HelpMe.VIDEO_TYPE, caption.getText().toString()
                    .trim(), "png", 1223, null, videoPath, TJPreferences.getUserId(this), currenTime, currenTime, null, thumbnailPath);
        }

        //Setting fields common in both the cases
        video.setImageBitmap(BitmapFactory.decodeFile(thumbnailPath));


        //Profile picture
        String profileImgPath;
        if (!mVideo.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mVideo.getCreatedBy());
            profileImgPath = contact.getPicLocalUrl();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
        }
        try {
            if (profileImgPath != null) {
                Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, profileImgPath, 100, 100);
                mProfileImg.setImageBitmap(bitmap);
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
                if (mVideo.getDataLocalURL() != null) {
                    Log.d(TAG, "video url is not null");
                    Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(mVideo.getDataLocalURL())));
                    mediaIntent.setDataAndType(Uri.fromFile(new File(mVideo.getDataLocalURL())), "video/*");
                    startActivity(mediaIntent);
                } else {
                    pDialog.setMessage("downloading video please wait");
                    new DownloadVideoAsyncTask(VideoDetail.this, mVideo).execute();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
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
                if(likedBy == null){
                    likedBy = new ArrayList<String>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(VideoDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(VideoDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(VideoDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(VideoDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.heart_full);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mVideo.setLikedBy(likedBy);
                if (!isNewVideo) {
                    mVideo.updateLikedBy(VideoDetail.this, mVideo.getId(), likedBy);
                }
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
                if (isNewVideo) {
                    saveAndUploadVideo();
                }
                /*Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onVideoDownload(String videoLocalUrl, Video video) {
        VideoDataSource.updateVideoLocalUrl(this, video.getDataLocalURL(), video.getId());
        Log.d(TAG, "video downloaded successfully now displaying it");
        pDialog.dismiss();
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(mVideo.getDataLocalURL())));
        mediaIntent.setDataAndType(Uri.fromFile(new File(mVideo.getDataLocalURL())), "video/*");
        startActivity(mediaIntent);
    }
}




