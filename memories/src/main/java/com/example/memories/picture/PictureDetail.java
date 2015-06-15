package com.example.memories.picture;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Picture;
import com.example.memories.services.GPSTracker;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PictureDetail extends AppCompatActivity implements DownloadPicture.OnPictureDownloadListener {

    private static final String TAG = "<PhotoDetail>";
    List<String> likedBy;
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private EditText caption;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private long currenTime;
    private String imagePath;
    private Picture mPicture;
    private boolean isNewPic;
    private TextView noLikesTxt;

    private ProgressDialog pDialog;

    private String localThumbnailPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);
        Log.d(TAG, "entrerd photo details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black_semi_transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currenTime = HelpMe.getCurrentTime();
        photo = (ImageView) findViewById(R.id.photo_detail_photo);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        caption = (EditText) findViewById(R.id.photo_detail_caption);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        profileName = (TextView) findViewById(R.id.photo_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();

        //If the activity is started for an already clicked picture
        if (extras.getString("PICTURE_ID") != null) {
            Log.d(TAG, "running for an already created picture");
            mPicture = PictureDataSource.getPictureById(this, extras.getString("PICTURE_ID"));
            Log.d(TAG, "picture fetched is" + mPicture);
            imagePath = mPicture.getDataLocalURL(); //path to image
            localThumbnailPath = mPicture.getPicThumbnailPath();
            profileName.setText(ContactDataSource.getContactById(getBaseContext(), mPicture.getCreatedBy()).getName());
            //setup the state of favourite button
            if (mPicture.getLikedBy() == null) {
                noLikesTxt.setText("0");
                mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
            } else {
                noLikesTxt.setText(String.valueOf(mPicture.getLikedBy().size()));
                if (mPicture.getLikedBy().contains(TJPreferences.getUserId(PictureDetail.this))) {
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                } else {
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                }
            }
            caption.setText(mPicture.getCaption());
        }

        //If the activity is started for a newly clicked picture
        if (extras.getString("imagePath") != null) {
            Log.d(TAG, "running for a newly clicked picture");
            isNewPic = true;
            profileName.setText(TJPreferences.getUserName(getBaseContext()));
            imagePath = extras.getString("imagePath");
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(imagePath), 512, 384);
            localThumbnailPath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + System.currentTimeMillis() + ".jpg";
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(localThumbnailPath);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
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
        }

        // If the picture is created by someone else than remove the caption field
        Log.d(TAG, "video created by ->" + mPicture.getCreatedBy() + "user id ->" + TJPreferences.getUserId(this));
        if(!mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))){
            Log.d(TAG, "the picture has not been created by the logged in user hence removing caption option");
            caption.setVisibility(View.GONE);
        }

        //Setting fields common in both the cases

        // setup Image taking path from imagePath variable
//        try {
//            Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, imagePath, 680, 250);
        photo.setImageBitmap(BitmapFactory.decodeFile(localThumbnailPath));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


        //Profile picture
        Log.d(TAG, "setting the profile picture" + mPicture.getCreatedBy());
        String profileImgPath;
        if (mPicture != null && !mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mPicture.getCreatedBy());
            Log.d(TAG, "contact is " + contact);
            profileImgPath = contact.getPicLocalUrl();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
        }
        if (profileImgPath != null) {
            try {
                Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, profileImgPath, 100, 100);
                mProfileImg.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "profile picture set successfully");

        setFavouriteBtnClickListener();

        // set other details
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat fullTime = new SimpleDateFormat("hh:mm aaa, EEE");
        Date resultdate = new Date(currenTime);

        dateBig.setText(onlyDate.format(resultdate).toString());
        date.setText(fullDate.format(resultdate).toString());
        time.setText(fullTime.format(resultdate).toString());

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPicture.getDataLocalURL() == null) {
                    pDialog.setMessage("Please wait while the picture is getting downloaded");
                    pDialog.show();
                    new DownloadPicture(mPicture, PictureDetail.this, null).startDownloadingPic();
                } else {
                    Log.d(TAG, "profile pic is already present in the local so displaying it");
                    Intent intent = new Intent(PictureDetail.this, DisplayPicture.class);
                    intent.putExtra("PICTURE_PATH", mPicture.getDataLocalURL());
                    startActivity(intent);
                }
            }
        });

    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> likedBy = mPicture.getLikedBy();
                if (likedBy == null) {
                    likedBy = new ArrayList<>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(PictureDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(PictureDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(PictureDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(PictureDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mPicture.setLikedBy(likedBy);
                if (!isNewPic) {
                    mPicture.updateLikedBy(PictureDetail.this, mPicture.getId(), likedBy);
                }
            }
        });
    }

    private void saveAndUploadPic() {

        if (likedBy != null) {
            mPicture.setLikedBy(likedBy);
        }
        mPicture.setCaption(caption.getText().toString());
        //PictureDataSource.createPicture(mPicture, this);
        PictureUtilities.uploadPicture(this, mPicture);
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
                if (isNewPic) {
                    saveAndUploadPic();
                }
                //Check if the text of the caption has been changed. If yes than make a request to the server
                else if(caption.getText().toString() != mPicture.getCaption()){
                    Log.d(TAG, "the picture's caption has been changed so updating on server" + mPicture);
                    PictureUtilities.updateCaption(mPicture, caption.getText().toString(), getBaseContext());
                }
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
    public void onDownloadPicture(Picture picture, ImageView imgView) {
        PictureDataSource.updatePicLocalPath(this, picture.getDataLocalURL(), picture.getId());
        Log.d(TAG, "picture downloaded successfully now displaying it");
        pDialog.dismiss();
        Intent intent = new Intent(this, DisplayPicture.class);
        intent.putExtra("PICTURE_PATH", picture.getDataLocalURL());
        startActivity(intent);
    }
}
