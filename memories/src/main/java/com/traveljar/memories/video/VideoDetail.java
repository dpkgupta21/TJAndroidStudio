package com.traveljar.memories.video;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;


public class VideoDetail extends AppCompatActivity implements DownloadVideoAsyncTask.OnVideoDownloadListener {

    private static final String TAG = "<VideoDetail>";
    private ImageView video;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private ImageButton mFavBtn;
    private Video mVideo;
    private TextView noLikesTxt;
    private ProgressDialog pDialog;
    private TextView createdByName;
    private TextView mVideoCaption;

    private static final int ACTION_ITEM_DELETE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_detail);
        Log.d(TAG, "entrerd video details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Video Detail");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        video = (ImageView) findViewById(R.id.thumbnail);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        createdByName = (TextView) findViewById(R.id.photo_detail_profile_name);
        mVideoCaption = (TextView) findViewById(R.id.video_detail_caption);

        Bundle extras = getIntent().getExtras();
        mVideo = VideoDataSource.getVideoById(extras.getString("VIDEO_ID"), this);

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mVideo.getLikes().size()));
        mFavBtn.setImageResource(mVideo.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);

        video.setImageBitmap(BitmapFactory.decodeFile(mVideo.getLocalThumbPath()));
        mVideoCaption.setText(String.valueOf(mVideo.getCaption()));

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
        try {
            if (profileImgPath != null) {
                Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, profileImgPath, 100, 100);
                mProfileImg.setImageBitmap(bitmap);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        createdByName.setText(createdBy);

        setFavouriteBtnClickListener();
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

                String likeId = mVideo.isMemoryLikedByCurrentUser(VideoDetail.this);// Check if memory liked by current user
                Like like;
                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = MemoriesUtil.createLikeRequest(mVideo.getId(), Request.CATEGORY_TYPE_VIDEO, VideoDetail.this, HelpMe.VIDEO_TYPE);
                    mVideo.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mVideo.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    mVideo.getLikes().remove(like);
                    MemoriesUtil.createUnlikeRequest(like, Request.CATEGORY_TYPE_VIDEO, VideoDetail.this);
                }
                noLikesTxt.setText(String.valueOf(mVideo.getLikes().size()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(mVideo.getCreatedBy().equals(TJPreferences.getUserId(this))){
            menu.add(0, ACTION_ITEM_DELETE, 0, "Delete").setIcon(R.drawable.ic_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case ACTION_ITEM_DELETE:
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to remove this item from your memories")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Request request = new Request(null, mVideo.getId(), mVideo.getjId(), Request.OPERATION_TYPE_DELETE,
                                        Request.CATEGORY_TYPE_VIDEO, Request.REQUEST_STATUS_NOT_STARTED, 0);
                                RequestQueueDataSource.createRequest(request, VideoDetail.this);
                                if(HelpMe.isNetworkAvailable(VideoDetail.this)) {
                                    Intent intent = new Intent(VideoDetail.this, MakeServerRequestsService.class);
                                    startService(intent);
                                }
                                finish();
//                                MemoriesUtil.getInstance().deleteMemory(VideoDetail.this, mVideo.getIdOnServer());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
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




