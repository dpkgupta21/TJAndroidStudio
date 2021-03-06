package com.traveljar.memories.picture;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.text.DecimalFormat;


public class PictureDetail extends AppCompatActivity implements DownloadPicture.OnPictureDownloadListener {

    private static final String TAG = "<PhotoDetail>";
    private ImageView photo;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private Picture mPicture;
    private TextView noLikesTxt;
    private TextView mPictureCaption;
    private TextView placeTxt;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_detail);
        Log.d(TAG, "entrerd photo details");

        photo = (ImageView) findViewById(R.id.photo_detail_photo);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.photo_detail_profile_image);
        profileName = (TextView) findViewById(R.id.photo_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);
        mPictureCaption = (TextView) findViewById(R.id.photo_detail_caption);
        placeTxt = (TextView) findViewById(R.id.photo_detail_place);

        pDialog = new ProgressDialog(this);
        pDialog.setCanceledOnTouchOutside(false);

        Bundle extras = getIntent().getExtras();

        //If the activity is started for an already clicked picture
        mPicture = PictureDataSource.getPictureById(this, extras.getString("PICTURE_ID"));
        setUpToolBar();

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mPicture.getLikes().size()));
        mFavBtn.setImageResource(mPicture.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);

        photo.setImageBitmap(BitmapFactory.decodeFile(mPicture.getPicThumbnailPath()));
        mPictureCaption.setText(mPicture.getCaption());

        //Profile picture
        Log.d(TAG, "setting the profile picture" + mPicture.getCreatedBy());
        String profileImgPath;
        String createdBy;
        if (mPicture != null && !mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mPicture.getCreatedBy());
            Log.d(TAG, "contact is " + contact);
            profileImgPath = contact.getPicLocalUrl();
            createdBy = contact.getProfileName();
        } else {
            profileImgPath = TJPreferences.getProfileImgPath(this);
            createdBy = TJPreferences.getUserName(this);
        }
        if (profileImgPath != null) {
            Glide.with(this).load(Uri.fromFile(new File(profileImgPath))).asBitmap().into(mProfileImg);
        }
        profileName.setText(createdBy);
        Log.d(TAG, "profile picture set successfully");

        String place = "Lat " + new DecimalFormat("#.##").format(mPicture.getLatitude()) + " Lon " +
                new DecimalFormat("#.##").format(mPicture.getLongitude());;
        placeTxt.setText(place);
        setFavouriteBtnClickListener();

        setFavouriteBtnClickListener();

        dateBig.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mPicture.getCreatedAt(), HelpMe.TIME_ONLY));

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PictureDetail.this, PictureFullScreen.class);
                intent.putExtra("PICTURE_PATH", mPicture.getPicThumbnailPath());
                startActivity(intent);
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
                    Log.d(TAG, "picture is not already liked so liking it");
                    like = MemoriesUtil.createLikeRequest(mPicture.getId(), Request.CATEGORY_TYPE_PICTURE, PictureDetail.this, HelpMe.PICTURE_TYPE);
                    mPicture.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);

                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like for likeId = " + likeId);
                    like = mPicture.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    mPicture.getLikes().remove(like);
                    MemoriesUtil.createUnlikeRequest(like, Request.CATEGORY_TYPE_PICTURE, PictureDetail.this);
                }
                noLikesTxt.setText(String.valueOf(mPicture.getLikes().size()));
            }
        });
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Picture");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDetail.this.finish();
            }
        });
        if(mPicture.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            toolbar.inflateMenu(R.menu.action_bar_with_delete);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        new AlertDialog.Builder(PictureDetail.this)
                                .setTitle("Delete")
                                .setMessage("Are you sure you want to remove this item from your memories")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Request request = new Request(null, mPicture.getId(), mPicture.getjId(), Request.OPERATION_TYPE_DELETE,
                                                Request.CATEGORY_TYPE_PICTURE, Request.REQUEST_STATUS_NOT_STARTED, 0);
                                        PictureDataSource.updateDeleteStatus(PictureDetail.this, mPicture.getId(), true);
                                        RequestQueueDataSource.createRequest(request, PictureDetail.this);
                                        if (HelpMe.isNetworkAvailable(PictureDetail.this)) {
                                            Intent intent = new Intent(PictureDetail.this, MakeServerRequestsService.class);
                                            startService(intent);
                                        }
                                        finish();
                                        //MemoriesUtil.getInstance().deleteMemory(PictureDetail.this, mPicture.getIdOnServer());
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

    @Override
    public void onDownloadPicture(Picture picture, boolean success) {
        PictureDataSource.updatePicLocalPath(this, picture.getDataLocalURL(), picture.getId());
        Log.d(TAG, "picture downloaded successfully now displaying it");
        pDialog.dismiss();
        Intent intent = new Intent(this, PictureFullScreen.class);
        intent.putExtra("PICTURE_PATH", picture.getDataLocalURL());
        startActivity(intent);
    }

}