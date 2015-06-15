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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.VideoDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Video;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VideoDetail extends AppCompatActivity implements DownloadVideoAsyncTask.OnVideoDownloadListener {

    private static final String TAG = "<VideoDetail>";
    List<String> likedBy;
    private ImageView video;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private TextView place;
    private TextView weather;
    private ImageView mProfileImg;
    private ImageButton mFavBtn;
    private long currenTime;
    private String videoPath;
    private Video mVideo;
    private TextView noLikesTxt;
    private ProgressDialog pDialog;
    private TextView createdByName;
    private TextView mVideoCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);
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
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        createdByName = (TextView) findViewById(R.id.photo_detail_profile_name);
        mVideoCaption = (TextView) findViewById(R.id.video_detail_caption);

        String thumbnailPath;
        Bundle extras = getIntent().getExtras();
        mVideo = VideoDataSource.getVideoById(extras.getString("VIDEO_ID"), this);
        videoPath = mVideo.getDataLocalURL(); //path to image
        thumbnailPath = mVideo.getLocalThumbPath();
        //setup the state of favourite button
        if (mVideo.getLikedBy() == null) {
            noLikesTxt.setText("0");
            mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
        } else {
            noLikesTxt.setText(String.valueOf(mVideo.getLikedBy().size()));
            if (mVideo.getLikedBy().contains(TJPreferences.getUserId(VideoDetail.this))) {
                mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
            } else {
                mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
            }
        }

        //Setting fields common in both the cases
        video.setImageBitmap(BitmapFactory.decodeFile(thumbnailPath));
        mVideoCaption.setText(mVideo.getCaption());


        //Profile picture and name
        String profileImgPath;
        String createdBy;
        if (!mVideo.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mVideo.getCreatedBy());
            profileImgPath = contact.getPicLocalUrl();
            createdBy = contact.getName();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
            createdBy = TJPreferences.getUserName(VideoDetail.this);
        }
        createdByName.setText(createdBy);
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
    public void onBackPressed() {
        super.onBackPressed();
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
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(VideoDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(VideoDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(VideoDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(VideoDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mVideo.setLikedBy(likedBy);
                mVideo.updateLikedBy(VideoDetail.this, mVideo.getId(), likedBy);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_done:
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
    public void onVideoDownload(String videoLocalUrl, Video video) {
        VideoDataSource.updateVideoLocalUrl(this, video.getId(), video.getDataLocalURL());
        Log.d(TAG, "video downloaded successfully now displaying it");
        pDialog.dismiss();
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(new File(mVideo.getDataLocalURL())));
        mediaIntent.setDataAndType(Uri.fromFile(new File(mVideo.getDataLocalURL())), "video/*");
        startActivity(mediaIntent);
    }
}




