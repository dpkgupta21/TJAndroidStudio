package com.traveljar.memories.checkin;

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
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by abhi on 19/06/15.
 */
public class CheckinDetail extends AppCompatActivity {
    private static final String TAG = "<CheckInDetail>";
    private static final int ACTION_ITEM_DELETE = 0;
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
                    Log.d(TAG, "checkIn is not already liked so liking it");
                    like = MemoriesUtil.createLikeRequest(mCheckIn.getId(), Request.CATEGORY_TYPE_CHECKIN, CheckinDetail.this, HelpMe.CHECKIN_TYPE);
                    mCheckIn.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "checkin is not already liked so removing the like");
                    like = mCheckIn.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    mCheckIn.getLikes().remove(like);
                    MemoriesUtil.createUnlikeRequest(like, Request.CATEGORY_TYPE_CHECKIN, CheckinDetail.this);
                }
                noLikesTxt.setText(String.valueOf(mCheckIn.getLikes().size()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        if(mCheckIn.getCreatedBy().equals(TJPreferences.getUserId(this))){
            menu.add(0, ACTION_ITEM_DELETE, 0, "Delete").setIcon(R.drawable.ic_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;case ACTION_ITEM_DELETE:
                new AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to remove this item from your memories")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                MemoriesUtil.deleteMemory(CheckinDetail.this, mCheckIn.getIdOnServer());
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
