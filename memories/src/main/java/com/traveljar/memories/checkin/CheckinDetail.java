package com.traveljar.memories.checkin;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class CheckinDetail extends AppCompatActivity {
    private static final String TAG = "<CheckInDetail>";
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
    private TextView placeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_detail);
        Log.d(TAG, "entrerd checkin details");

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
        placeTxt = (TextView) findViewById(R.id.checkin_detail_place);

        Bundle extras = getIntent().getExtras();

        mCheckIn = CheckinDataSource.getCheckInById(extras.getString("CHECKIN_ID"), this);
        Log.d(TAG, "checkin fetched is" + mCheckIn);
        setUpToolBar();

        mCheckInCaptionTxt.setText(mCheckIn.getCaption());
        mCheckInInPlaceTxt.setText("@ " + mCheckIn.getCheckInPlaceName());
        List<Contact> checkInWith = ContactDataSource.getAllContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        String checkInWithText = "";
        for(Contact contact : checkInWith){
            checkInWithText += contact.getProfileName() + ", ";
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
            createdBy = contact.getProfileName();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
            createdBy = TJPreferences.getUserName(this);
        }
        profileName.setText(createdBy);

        if(profileImgPath != null) {
            Glide.with(this).load(Uri.fromFile(new File(profileImgPath))).asBitmap().into(mProfileImg);
        }
        Log.d(TAG, "profile picture set successfully");

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mCheckIn.getLikes().size()));
        mFavBtn.setImageResource(mCheckIn.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);

        String place = "Lat " + new DecimalFormat("#.##").format(mCheckIn.getLatitude()) + " Lon " +
                new DecimalFormat("#.##").format(mCheckIn.getLongitude());;
        placeTxt.setText(place);
        setFavouriteBtnClickListener();

        dateBig.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mCheckIn.getCreatedAt(), HelpMe.TIME_ONLY));

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("TravelJar");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckinDetail.this.finish();
            }
        });
        if(mCheckIn.getCreatedBy().equals(TJPreferences.getUserId(this))){
            toolbar.inflateMenu(R.menu.action_bar_with_delete);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "toolbar item clicked");
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        Log.d(TAG, "delete checkin");
                        new AlertDialog.Builder(CheckinDetail.this)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to remove this item from your memories")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Request request = new Request(null, mCheckIn.getId(), mCheckIn.getjId(), Request.OPERATION_TYPE_DELETE,
                                                Request.CATEGORY_TYPE_CHECKIN, Request.REQUEST_STATUS_NOT_STARTED, 0);
                                        CheckinDataSource.updateDeleteStatus(CheckinDetail.this, mCheckIn.getId(), true);
                                        RequestQueueDataSource.createRequest(request, CheckinDetail.this);
                                        if(HelpMe.isNetworkAvailable(CheckinDetail.this)) {
                                            Intent intent = new Intent(CheckinDetail.this, MakeServerRequestsService.class);
                                            startService(intent);
                                        }
                                        //MemoriesUtil.getInstance().deleteMemory(CheckinDetail.this, mCheckIn.getIdOnServer());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return true;
                }
                return false;
            }
        });



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

/*    @Override
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
                                Request request = new Request(null, mCheckIn.getId(), mCheckIn.getjId(), Request.OPERATION_TYPE_DELETE,
                                        Request.CATEGORY_TYPE_CHECKIN, Request.REQUEST_STATUS_NOT_STARTED, 0);
                                CheckinDataSource.updateDeleteStatus(CheckinDetail.this, mCheckIn.getId(), true);
                                RequestQueueDataSource.createRequest(request, CheckinDetail.this);
                                if(HelpMe.isNetworkAvailable(CheckinDetail.this)) {
                                    Intent intent = new Intent(CheckinDetail.this, MakeServerRequestsService.class);
                                    startService(intent);
                                }
                                //MemoriesUtil.getInstance().deleteMemory(CheckinDetail.this, mCheckIn.getIdOnServer());
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
    }*/

}
