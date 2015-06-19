package com.example.memories.checkin;

import android.graphics.Bitmap;
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
import com.example.memories.SQLitedatabase.CheckinDataSource;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.LikeDataSource;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Contact;
import com.example.memories.models.Like;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.MemoriesUtil;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ankit on 19/6/15.
 */
public class CheckinDetail extends AppCompatActivity {
    private static final String TAG = "<CheckInDetail>";
    List<String> likedBy;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private CheckIn mCheckIn;
    private TextView noLikesTxt;
    private TextView mCheckInCaptionTxt;
    private TextView mCheckInWithTxt;
    private TextView mCheckInInPlaceTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_detail);
        Log.d(TAG, "entrerd checkin details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateBig = (TextView) findViewById(R.id.checkin_detail_date_big);
        date = (TextView) findViewById(R.id.checkin_detail_date);
        time = (TextView) findViewById(R.id.checkin_detail_time);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.checkin_detail_profile_image);
        profileName = (TextView) findViewById(R.id.checkin_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        mCheckInCaptionTxt  = (TextView) findViewById(R.id.checkin_caption);
        mCheckInWithTxt = (TextView) findViewById(R.id.checkin_friends_names);
        mCheckInInPlaceTxt = (TextView) findViewById(R.id.checkin_place);

        Bundle extras = getIntent().getExtras();

        mCheckIn = CheckinDataSource.getCheckInById(extras.getString("CHECKIN_ID"), this);
        Log.d(TAG, "checkin fetched is" + mCheckIn);

        mCheckInCaptionTxt.setText(mCheckIn.getCaption());
        mCheckInInPlaceTxt.setText("@ " + mCheckIn.getCheckInPlaceName());
        /*Setting the names of the friends in the checkin*/
        List<Contact> checkInWith = ContactDataSource.getContactsListFromIds(this, mCheckIn.getCheckInWith());
        String checkInWithText = "";
        for(Contact contact : checkInWith){
            checkInWithText += contact.getName() + ", ";
        }
        mCheckInWithTxt.setText(checkInWithText);


        //Profile picture
        Log.d(TAG, "setting the profile picture" + mCheckIn.getCreatedBy());
        String profileImgPath;
        String createdBy;
        if (mCheckIn != null && !mCheckIn.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mCheckIn.getCreatedBy());
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

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mCheckIn.getLikes().size()));
        mFavBtn.setImageResource(mCheckIn.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);
/*        if (mCheckIn.getLikedBy() == null) {
            noLikesTxt.setText("0");
            mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
        } else {
            noLikesTxt.setText(String.valueOf(mCheckIn.getLikedBy().size()));
            if (mCheckIn.getLikedBy().contains(TJPreferences.getUserId(CheckinDetail.this))) {
                mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
            } else {
                mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
            }
        }*/
        setFavouriteBtnClickListener();

        dateBig.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.TIME_ONLY));

    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String likeId = mCheckIn.isMemoryLikedByCurrentUser(CheckinDetail.this);// Check if memory liked by current user
                Like like;
                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = new Like(null, null, mCheckIn.getjId(), mCheckIn.getIdOnServer(), TJPreferences.getUserId(CheckinDetail.this), mCheckIn.getMemType());
                    like.setId(String.valueOf(LikeDataSource.createLike(like, CheckinDetail.this)));
                    mCheckIn.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                    MemoriesUtil.likeMemory(CheckinDetail.this, like);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mCheckIn.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    LikeDataSource.deleteLike(CheckinDetail.this, like);
                    mCheckIn.getLikes().remove(like);
                    MemoriesUtil.unlikeMemory(CheckinDetail.this, like);
                }
                noLikesTxt.setText(String.valueOf(mCheckIn.getLikes().size()));
/*                List<String> likedBy = mCheckIn.getLikedBy();
                if (likedBy == null) {
                    likedBy = new ArrayList<>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(CheckinDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(CheckinDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(CheckinDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(CheckinDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mCheckIn.setLikedBy(likedBy);
                mCheckIn.updateLikedBy(CheckinDetail.this, mCheckIn.getId(), likedBy);*/
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
}
