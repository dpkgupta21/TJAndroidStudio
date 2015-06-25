package com.traveljar.memories.moods;

import android.content.DialogInterface;
import android.graphics.Bitmap;
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
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by ankit on 19/6/15.
 */
public class MoodDetail extends AppCompatActivity {

    private static final String TAG = "<MoodDetail>";
    private static final int ACTION_ITEM_DELETE = 0;
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
        toolbar.setTitle("Mood Detail");
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
                    Log.d(TAG, "mood is not already liked so liking it");
                    like = MemoriesUtil.createLikeRequest(mMood.getId(), Request.CATEGORY_TYPE_MOOD, MoodDetail.this, HelpMe.MOOD_TYPE);
                    mMood.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "mood is not already liked so removing the like");
                    like = mMood.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    mMood.getLikes().remove(like);
                    MemoriesUtil.createUnlikeRequest(like, Request.CATEGORY_TYPE_MOOD, MoodDetail.this);
                }
                noLikesTxt.setText(String.valueOf(mMood.getLikes().size()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(mMood.getCreatedBy().equals(TJPreferences.getUserId(this))){
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
                                MemoriesUtil.getInstance().deleteMemory(MoodDetail.this, mMood.getIdOnServer());
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
