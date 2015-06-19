package com.example.memories.moods;

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
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.LikeDataSource;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.models.Contact;
import com.example.memories.models.Like;
import com.example.memories.models.Mood;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.MemoriesUtil;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ankit on 19/6/15.
 */
public class MoodDetail extends AppCompatActivity {

    private static final String TAG = "<MoodDetail>";
    List<String> likedBy;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private Mood mMood;
    private TextView noLikesTxt;
    private TextView mMoodFriendsTxt;
    private TextView mMoodReason;
    private ImageView mMoodImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_detail);
        Log.d(TAG, "entered mood details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateBig = (TextView) findViewById(R.id.mood_detail_date_big);
        date = (TextView) findViewById(R.id.mood_detail_date);
        time = (TextView) findViewById(R.id.mood_detail_time);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.mood_detail_profile_image);
        profileName = (TextView) findViewById(R.id.mood_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        mMoodFriendsTxt  = (TextView) findViewById(R.id.mood_friends_names);
        mMoodReason = (TextView) findViewById(R.id.mood_reason);
        mMoodImg = (ImageView) findViewById(R.id.mood_img);

        Bundle extras = getIntent().getExtras();

        mMood = MoodDataSource.getMoodById(extras.getString("MOOD_ID"), this);
        Log.d(TAG, "mood fetched is" + mMood);

        mMoodReason.setText(mMood.getReason());
        /*Setting the names of the friends in the mood*/
        List<Contact> moodFriends = ContactDataSource.getContactsListFromIds(this, mMood.getBuddyIds());
        String moodFriendsTxt = "";
        for(Contact contact : moodFriends){
            moodFriendsTxt += contact.getName() + ", ";
        }
        mMoodFriendsTxt.setText(moodFriendsTxt);

        int resourceId = this.getResources().getIdentifier(mMood.getMood(), "drawable",
                this.getPackageName());
        mMoodImg.setImageResource(resourceId);

        //Profile picture
        Log.d(TAG, "setting the profile picture" + mMood.getCreatedBy());
        String profileImgPath;
        String createdBy;
        if (mMood != null && !mMood.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mMood.getCreatedBy());
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

        noLikesTxt.setText(String.valueOf(mMood.getLikes().size()));
        mFavBtn.setImageResource(mMood.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);
/*        if (mMood.getLikedBy() == null) {
            noLikesTxt.setText("0");
            mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
        } else {
            noLikesTxt.setText(String.valueOf(mMood.getLikedBy().size()));
            if (mMood.getLikedBy().contains(TJPreferences.getUserId(MoodDetail.this))) {
                mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
            } else {
                mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
            }
        }*/
        setFavouriteBtnClickListener();

        dateBig.setText(HelpMe.getDate(mMood.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mMood.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mMood.getCreatedAt(), HelpMe.TIME_ONLY));

    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String likeId = mMood.isMemoryLikedByCurrentUser(MoodDetail.this);// Check if memory liked by current user
                Like like;
                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = new Like(null, null, mMood.getjId(), mMood.getIdOnServer(), TJPreferences.getUserId(MoodDetail.this), mMood.getMemType());
                    like.setId(String.valueOf(LikeDataSource.createLike(like, MoodDetail.this)));
                    mMood.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                    MemoriesUtil.likeMemory(MoodDetail.this, like);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mMood.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    LikeDataSource.deleteLike(MoodDetail.this, like);
                    mMood.getLikes().remove(like);
                    MemoriesUtil.unlikeMemory(MoodDetail.this, like);
                }
                noLikesTxt.setText(String.valueOf(mMood.getLikes().size()));
/*                List<String> likedBy = mMood.getLikedBy();
                if (likedBy == null) {
                    likedBy = new ArrayList<>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(MoodDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(MoodDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(MoodDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(MoodDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mMood.setLikedBy(likedBy);
                mMood.updateLikedBy(MoodDetail.this, mMood.getId(), likedBy);*/
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
