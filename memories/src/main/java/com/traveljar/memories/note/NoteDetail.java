package com.traveljar.memories.note;

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
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.MemoriesUtil;
import com.traveljar.memories.utility.TJPreferences;

import java.io.FileNotFoundException;

/**
 * Created by abhi on 19/06/15.
 */
public class NoteDetail extends AppCompatActivity implements MemoriesUtil.OnMemoryDeleteListener{
    private static final String TAG = "<NoteDetail>";
    private static final int ACTION_ITEM_DELETE = 0;
    private TextView noteContent;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private ImageView mProfileImg;
    private TextView profileName;
    private ImageButton mFavBtn;
    private Note mNote;
    private TextView noLikesTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_detail);
        Log.d(TAG, "entrerd notes details");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        noteContent = (TextView) findViewById(R.id.note_detail_note);
        dateBig = (TextView) findViewById(R.id.note_detail_date_big);
        date = (TextView) findViewById(R.id.note_detail_date);
        time = (TextView) findViewById(R.id.note_detail_time);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.note_detail_profile_image);
        profileName = (TextView) findViewById(R.id.note_detail_profile_name);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        Bundle extras = getIntent().getExtras();

        //If the activity is started for an already note
        Log.d(TAG, "running for an already created note");
        mNote = NoteDataSource.getNote(extras.getString("NOTE_ID"), this);
        Log.d(TAG, "note fetched is" + mNote);

        //setup the state of favourite button
        noLikesTxt.setText(String.valueOf(mNote.getLikes().size()));
        mFavBtn.setImageResource(mNote.isMemoryLikedByCurrentUser(this) != null ? R.drawable.ic_favourite_filled : R.drawable.ic_favourite_empty);

        noteContent.setText(mNote.getContent());

        //Profile picture
        Log.d(TAG, "setting the profile picture" + mNote.getCreatedBy());
        String profileImgPath;
        String createdBy;
        if (mNote != null && !mNote.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mNote.getCreatedBy());
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

        setFavouriteBtnClickListener();

        dateBig.setText(HelpMe.getDate(mNote.getCreatedAt(), HelpMe.DATE_ONLY));
        date.setText(HelpMe.getDate(mNote.getCreatedAt(), HelpMe.DATE_FULL));
        time.setText(HelpMe.getDate(mNote.getCreatedAt(), HelpMe.TIME_ONLY));

    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String likeId = mNote.isMemoryLikedByCurrentUser(NoteDetail.this);// Check if memory liked by current user
                Like like;
                if (likeId == null) {
                    //If not liked, create a new like object, save it to local, update on server
                    Log.d(TAG, "video is not already liked so liking it");
                    like = new Like(null, null, mNote.getjId(), mNote.getIdOnServer(), TJPreferences.getUserId(NoteDetail.this), mNote.getMemType());
                    like.setId(String.valueOf(LikeDataSource.createLike(like, NoteDetail.this)));
                    mNote.getLikes().add(like);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_filled);
                    MemoriesUtil.likeMemory(NoteDetail.this, like);
                } else {
                    // If already liked, delete from local database, delete from server
                    Log.d(TAG, "memory is not already liked so removing the like");
                    like = mNote.getLikeById(likeId);
                    mFavBtn.setImageResource(R.drawable.ic_favourite_empty);
                    LikeDataSource.deleteLike(NoteDetail.this, like);
                    mNote.getLikes().remove(like);
                    MemoriesUtil.unlikeMemory(NoteDetail.this, like);
                }
                noLikesTxt.setText(String.valueOf(mNote.getLikes().size()));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mNote.getCreatedBy().equals(TJPreferences.getUserId(this))) {
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
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDeleteMemory(int resultCode) {
        if(resultCode == 0){
            finish();
        }else {
            Toast.makeText(this, "Unable to delete delete your memory please try after some time", Toast.LENGTH_LONG).show();
        }
    }

}
