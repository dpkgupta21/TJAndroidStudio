package com.traveljar.memories.picture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
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
import java.text.DecimalFormat;

public class PicturePreview extends AppCompatActivity {

    private static final String TAG = "<PhotoDetail>";
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private EditText caption;
    private ImageView mProfileImg;
    private TextView profileName;
    private String imagePath;
    private Picture mPicture;
    private long createdAt;
    private TextView placeTxt;
    private double latitude = 0.0d;
    private double longitude = 0.0d;

    private ProgressDialog pDialog;

    private String localThumbnailPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_preview);
        Log.d(TAG, "entrerd photo details");

        setUpToolBar();

        photo = (ImageView) findViewById(R.id.photo_detail_photo);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.photo_detail_caption);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        profileName = (TextView) findViewById(R.id.photo_detail_profile_name);
        placeTxt = (TextView) findViewById(R.id.photo_detail_place);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();
        boolean isPicFromGallery = false;
        if (extras.containsKey("IS_PIC_FROM_GALLERY")) {
            isPicFromGallery = true;
        }
        Log.d(TAG, "running for a newly clicked picture");
        profileName.setText(TJPreferences.getUserName(getBaseContext()));
        imagePath = extras.getString("imagePath");
        createdAt = HelpMe.getCurrentTime();

        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 512, 384);
        localThumbnailPath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + TJPreferences.getUserId(this) + "_" +
                TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".jpg";
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

        setLocation(isPicFromGallery);

        mPicture = new Picture(null, TJPreferences.getActiveJourneyId(this), HelpMe.PICTURE_TYPE, caption.getText().toString()
                .trim(), "jpg", 1223, null, imagePath, TJPreferences.getUserId(this), createdAt, createdAt,
                null, localThumbnailPath, latitude, longitude);

        photo.setImageBitmap(BitmapFactory.decodeFile(localThumbnailPath));

        String place = "Lat " + new DecimalFormat("#.##").format(mPicture.getLatitude()) + " Lon " +
                new DecimalFormat("#.##").format(mPicture.getLongitude());
        ;
        placeTxt.setText(place);

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

    private void setLocation(boolean isPicFromGallery) {
        if (isPicFromGallery) {
            ExifInterface exifInterface = null;
            try {
                exifInterface = new ExifInterface(imagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String lat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lat_ref = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String longi = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String longi_ref = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if ((lat != null)
                    && (lat_ref != null)
                    && (longi != null)
                    && (longi_ref != null)) {

                if (lat_ref.equals("N")) {
                    latitude = convertToDegree(lat);
                } else {
                    latitude = 0 - convertToDegree(lat);
                }

                if (longi_ref.equals("E")) {
                    longitude = convertToDegree(longi);
                } else {
                    longitude = 0 - convertToDegree(longi);
                }
            }
            Log.d(TAG, "latitude and longitude from exif are " + latitude + " " + longitude);
        }
        if (latitude == 0.0d || longitude == 0.0d) {
            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude(); // returns latitude
                longitude = gps.getLongitude(); // returns longitude
            } else {
                Toast.makeText(getApplicationContext(), "Network issues. Try later.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("Picture Preview");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PicturePreview.this.finish();
            }
        });
        // toolbar.inflateMenu(R.menu.toolbar_with_done_text);
        TextView done = (TextView) toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "done clicked!");
                saveAndUploadPic();
                finish();
            }
        });
    }

    private void saveAndUploadPic() {
        mPicture.setCaption(caption.getText().toString());
        long id = PictureDataSource.createPicture(mPicture, this);
        mPicture.setId(String.valueOf(id));
        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_PICTURE, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if (HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        } else {
            Log.d(TAG, "since no network not starting service RQ");
        }
    }

    // Converts String(Latitude and longitude) obtained from exifInterface to double values (original you get as lat = 30/1,12/1,34/1)
    private Double convertToDegree(String stringDMS) {
        Double result = null;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        Double D0 = new Double(stringD[0]);
        Double D1 = new Double(stringD[1]);
        Double FloatD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        Double M0 = new Double(stringM[0]);
        Double M1 = new Double(stringM[1]);
        Double FloatM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        Double S0 = new Double(stringS[0]);
        Double S1 = new Double(stringS[1]);
        Double FloatS = S0 / S1;

        result = new Double(FloatD + (FloatM / 60) + (FloatS / 3600));

        return result;


    }

}
