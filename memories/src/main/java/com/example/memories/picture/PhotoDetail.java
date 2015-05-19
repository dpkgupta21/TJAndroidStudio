package com.example.memories.picture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Picture;
import com.example.memories.models.Video;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.PictureUtilities;
import com.example.memories.utility.TJPreferences;
import com.google.common.base.Joiner;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PhotoDetail extends Activity {

    private static final String TAG = "<PhotoDetail>";
    List<String> likedBy = new ArrayList<String>();
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private TextView place;
    private TextView weather;
    private EditText caption;
    private ImageView mProfileImg;
    private ImageButton mFavBtn;
    private long currenTime;
    private String imagePath;
    private Picture mPicture;
    private boolean isNewPic;
    private TextView noLikesTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);
        Log.d(TAG, "entrerd photo details");

        currenTime = HelpMe.getCurrentTime();
        photo = (ImageView) findViewById(R.id.photo_detail_photo);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        place = (TextView) findViewById(R.id.photo_detail_place);
        weather = (TextView) findViewById(R.id.photo_detail_weather);
        caption = (EditText) findViewById(R.id.photo_detail_caption);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        Bundle extras = getIntent().getExtras();
        //If the activity is started for an already clicked picture
        if (extras.getString("PICTURE_ID") != null) {
            Log.d(TAG, "running for an already created picture");
            mPicture = PictureDataSource.getPictureById(this, extras.getString("PICTURE_ID"));
            imagePath = mPicture.getDataLocalURL(); //path to image
            //setup the state of favourite button
            if (mPicture.getLikedBy() != null) {
                List<String> likedBy = Arrays.asList((mPicture.getLikedBy()).split(","));
                //mFavBtn.setText(String.valueOf(likedBy.size()));
                if (likedBy.contains(TJPreferences.getUserId(PhotoDetail.this))) {
                    mFavBtn.setImageResource(R.drawable.heart_full);
                } else {
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                }
                noLikesTxt.setText(String.valueOf(likedBy.size()));
            }
        }
        //If the activity is started for a newly clicked picture
        if (extras.getString("imagePath") != null) {
            Log.d(TAG, "running for a newly clicked picture");
            isNewPic = true;
            imagePath = extras.getString("imagePath");
            mPicture = new Picture(null, TJPreferences.getActiveJourneyId(this), HelpMe.PICTURE_TYPE, caption.getText().toString()
                    .trim(), "png", 1223, null, imagePath, TJPreferences.getUserId(this), currenTime, currenTime, null);
        }

        //Setting fields common in both the cases

        // setup Image taking path from imagePath variable
        try {
            Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, imagePath, 680, 250);
            photo.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        //Profile picture
        String profileImgPath;
        if (mPicture != null && !mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mPicture.getCreatedBy());
            profileImgPath = contact.getPicLocalUrl();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
        }
        try {
            Bitmap bitmap = HelpMe.decodeSampledBitmapFromPath(this, profileImgPath, 100, 100);
            mProfileImg.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        setFavouriteBtnClickListener();

        // set other details
        SimpleDateFormat onlyDate = new SimpleDateFormat("dd");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat fullTime = new SimpleDateFormat("hh:mm aaa, EEE");
        Date resultdate = new Date(currenTime);

        dateBig.setText(onlyDate.format(resultdate).toString());
        date.setText(fullDate.format(resultdate).toString());
        time.setText(fullTime.format(resultdate).toString());
    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(likedBy == null){
                    likedBy = new ArrayList<String>();
                    if(mPicture.getLikedBy() != null) {
                        String array[] = mPicture.getLikedBy().split(",");
                        for (String s : array) {
                            likedBy.add(s);
                        }
                    }
                }
                if (likedBy.contains(TJPreferences.getUserId(PhotoDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(PhotoDetail.this));
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(PhotoDetail.this));
                    mFavBtn.setImageResource(R.drawable.heart_full);
                }
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                String finalValue;
                if (likedBy.size() == 0) {
                    finalValue = null;
                } else {
                    finalValue = Joiner.on(",").join(likedBy);
                }
                mPicture.setLikedBy(finalValue);
                if(!isNewPic) {
                    mPicture.updateLikedBy(PhotoDetail.this, mPicture.getId(), finalValue);
                }
            }
        });
    }

    private void saveAndUploadPic() {
        Log.d(TAG, "creating a new picture in local DB");

        if(likedBy != null) {
            mPicture.setLikedBy(Joiner.on(",").join(likedBy));
        }
        mPicture.setCaption(caption.getText().toString());
        PictureDataSource.createPicture(mPicture, this);
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
                Intent i = new Intent(getBaseContext(), Timeline.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
