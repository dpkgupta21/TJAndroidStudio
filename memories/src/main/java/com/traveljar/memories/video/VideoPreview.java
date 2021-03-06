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

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.SQLitedatabase.VideoDataSource;
import com.traveljar.memories.currentjourney.CurrentJourneyBaseActivity;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class VideoPreview extends AppCompatActivity {

    private static final String TAG = "<VideoDetail>";
    private ImageView video;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private EditText caption;
    private ImageView mProfileImg;
    private String videoPath;
    private Video mVideo;
    private ProgressDialog pDialog;
    private TextView createdByName;
    private long createdAt;
    private TextView placeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_preview);
        Log.d(TAG, "entrerd video details");

        setUpToolBar();

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        video = (ImageView) findViewById(R.id.thumbnail);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.video_detail_caption);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        createdByName = (TextView) findViewById(R.id.photo_detail_profile_name);
        placeTxt = (TextView) findViewById(R.id.video_detail_place);

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
        if (TJPreferences.getProfileImgPath(this) != null) {
            Glide.with(this).load(Uri.fromFile(new File(TJPreferences.getProfileImgPath(this)))).asBitmap().into(mProfileImg);
        }


        String place = "Lat " + new DecimalFormat("#.##").format(mVideo.getLatitude()) + " Lon " +
                new DecimalFormat("#.##").format(mVideo.getLongitude());
        ;
        placeTxt.setText(place);

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
        if (HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        } else {
            Log.d(TAG, "since no network not starting service RQ");
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Video Preview");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoPreview.this.finish();
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_with_done_text);
        TextView done = (TextView) toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done clicked!");
                if (!caption.getText().toString().isEmpty()) {
                    mVideo.setCaption(caption.getText().toString());
                }
                saveAndUploadVideo();
                Intent intent = new Intent(VideoPreview.this, CurrentJourneyBaseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
