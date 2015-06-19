package com.example.memories.picture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.memories.SQLitedatabase.LikeDataSource;
import com.example.memories.SQLitedatabase.PictureDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Like;
import com.example.memories.models.Picture;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.MemoriesUtil;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.List;

public class PictureDetail extends AppCompatActivity implements DownloadPicture.OnPictureDownloadListener {

    private static final String TAG = "<PhotoDetail>";
    List<String> likedBy;
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private long currenTime;
    private String imagePath;
    private Picture mPicture;
    private TextView noLikesTxt;
    private TextView mPictureCaption;

    private ProgressDialog pDialog;

    private String localThumbnailPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);
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
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        profileName = (TextView) findViewById(R.id.photo_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        mPictureCaption = (TextView) findViewById(R.id.photo_detail_caption);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();

        //If the activity is started for an already clicked picture
        Log.d(TAG, "running for an already created picture");
        mPicture = PictureDataSource.getPictureById(this, extras.getString("PICTURE_ID"));
        Log.d(TAG, "picture fetched is" + mPicture);
        imagePath = mPicture.getDataLocalURL(); //path to image
        localThumbnailPath = mPicture.getPicThumbnailPath();

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mPicture.getLikes().size()));
        mFavBtn.setImageResource(mPicture.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);
 /*       if (mPicture.getLikedBy() == null) {
            noLikesTxt.setText("0");
            mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
        } else {
            noLikesTxt.setText(String.valueOf(mPicture.getLikedBy().size()));
            if (mPicture.getLikedBy().contains(TJPreferences.getUserId(PictureDetail.this))) {
                mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
            } else {
                mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
            }
        }*/

        photo.setImageBitmap(BitmapFactory.decodeFile(localThumbnailPath));
        mPictureCaption.setText(mPicture.getCaption());

        //Profile picture
        Log.d(TAG, "setting the profile picture" + mPicture.getCreatedBy());
        String profileImgPath;
        String createdBy;
        if (mPicture != null && !mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mPicture.getCreatedBy());
            Log.d(TAG, "contact is " + contact);
            profileImgPath = contact.getPicLocalUrl();
            createdBy = contact.getName();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
            createdBy = TJPreferences.getUserName(this);
        }
        profileName.setText(createdBy);

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

        dateBig.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.TIME_ONLY));

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
                String likeId = mPicture.isMemoryLikedByCurrentUser(PictureDetail.this);// Check if memory liked by current user
                Like like;
                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = new Like(null, null, mPicture.getjId(), mPicture.getIdOnServer(), TJPreferences.getUserId(PictureDetail.this), mPicture.getMemType());
                    like.setId(String.valueOf(LikeDataSource.createLike(like, PictureDetail.this)));
                    mPicture.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                    MemoriesUtil.likeMemory(PictureDetail.this, like);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mPicture.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    LikeDataSource.deleteLike(PictureDetail.this, like);
                    mPicture.getLikes().remove(like);
                    MemoriesUtil.unlikeMemory(PictureDetail.this, like);
                }
                noLikesTxt.setText(String.valueOf(mPicture.getLikes().size()));
/*                List<String> likedBy = mPicture.getLikedBy();
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
                mPicture.updateLikedBy(PictureDetail.this, mPicture.getId(), likedBy);
            }*/
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
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
