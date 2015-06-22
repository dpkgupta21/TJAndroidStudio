package com.traveljar.memories.picture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ankit on 15/6/15.
 */
public class PicturePreview extends AppCompatActivity {

    private static final String TAG = "<PhotoDetail>";
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private EditText caption;
    private ImageView mProfileImg;
    private TextView profileName;
    private long currenTime;
    private String imagePath;
    private Picture mPicture;

    private ProgressDialog pDialog;

    private String localThumbnailPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        Log.d(TAG, "entrerd photo details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currenTime = HelpMe.getCurrentTime();
        photo = (ImageView) findViewById(R.id.photo_detail_photo);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.photo_detail_caption);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        profileName = (TextView) findViewById(R.id.photo_detail_profile_name);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();

            Log.d(TAG, "running for a newly clicked picture");
            profileName.setText(TJPreferences.getUserName(getBaseContext()));
            imagePath = extras.getString("imagePath");
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 512, 384);
            localThumbnailPath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + System.currentTimeMillis() + ".jpg";
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(localThumbnailPath);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
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

            mPicture = new Picture(null, TJPreferences.getActiveJourneyId(this), HelpMe.PICTURE_TYPE, caption.getText().toString()
                    .trim(), "jpg", 1223, null, imagePath, TJPreferences.getUserId(this), currenTime, currenTime, null, localThumbnailPath, lat, longi);

        photo.setImageBitmap(BitmapFactory.decodeFile(localThumbnailPath));

        //Profile picture
        Log.d(TAG, "setting the profile picture" + mPicture.getCreatedBy());
        if (TJPreferences.getProfileImgPath(this) != null) {
            try {
                Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, TJPreferences.getProfileImgPath(this), 100, 100);
                mProfileImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        dateBig.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.TIME_ONLY));

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PicturePreview.this, DisplayPicture.class);
                intent.putExtra("PICTURE_PATH", mPicture.getDataLocalURL());
                startActivity(intent);
            }
        });

    }

    private void saveAndUploadPic() {
/*        mPicture.setCaption(caption.getText().toString());
        long id = PictureDataSource.createPicture(mPicture, this);
        mPicture.setId(String.valueOf(id));
        Log.d(TAG, "id of picture is " + String.valueOf(id) + mPicture.getId());
        Intent intent = new Intent(this, UploadPictureService.class);
        intent.putExtra("PICTURE", mPicture);
        startService(intent);*/
        mPicture.setCaption(caption.getText().toString());
        long id = PictureDataSource.createPicture(mPicture, this);
        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_UPLOAD, Request.CATEGORY_TYPE_PICTURE, Request.REQUEST_STATUS_NOT_STARTED);
        RequestQueueDataSource.createRequest(request, this);
        Intent intent = new Intent(this, MakeServerRequestsService.class);
        startService(intent);
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
                saveAndUploadPic();
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
