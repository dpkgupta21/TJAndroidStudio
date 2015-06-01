package com.example.memories.audio;

import android.graphics.Bitmap;
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

import com.example.memories.R;
import com.example.memories.SQLitedatabase.AudioDataSource;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Audio;
import com.example.memories.models.Contact;
import com.example.memories.utility.AudioUtil;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AudioDetail extends AppCompatActivity {

    private static final String TAG = "<AudioDetail>";
    List<String> likedBy;
    private TextView dateBig;
    private TextView date;
    private TextView time;
    private TextView place;
    private TextView weather;
    private ImageView audioThumbnail;
    private EditText caption;
    private ImageView mProfileImg;
    private ImageButton mFavBtn;
    private long currenTime;
    private String audioPath;
    private Audio mAudio;
    private boolean isNewAudio;
    private TextView noLikesTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_detail);
        Log.d(TAG, "entrerd audio details");


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Log.d(TAG, "found toolbar" + toolbar);
        toolbar.setTitle("Audio Detail");
        setSupportActionBar(toolbar);


        currenTime = HelpMe.getCurrentTime();
        audioThumbnail = (ImageView) findViewById(R.id.playAudio);
        dateBig = (TextView) findViewById(R.id.photo_detail_date_big);
        date = (TextView) findViewById(R.id.photo_detail_date);
        time = (TextView) findViewById(R.id.photo_detail_time);
        place = (TextView) findViewById(R.id.photo_detail_place);
        weather = (TextView) findViewById(R.id.photo_detail_weather);
        caption = (EditText) findViewById(R.id.voice_detail_caption);
        mFavBtn = (ImageButton) findViewById(R.id.favBtn);
        mProfileImg = (ImageView) findViewById(R.id.profilePic);
        noLikesTxt = (TextView) findViewById(R.id.no_likes);

        Bundle extras = getIntent().getExtras();
        //If the activity is started for an already clicked picture
        if (extras.getString("AUDIO_ID") != null) {
            Log.d(TAG, "running for an already created audio");
            mAudio = AudioDataSource.getAudioById(this, extras.getString("AUDIO_ID"));
            audioPath = mAudio.getDataLocalURL(); //path to image
            //setup the state of favourite button
            if (mAudio.getLikedBy() == null){
                noLikesTxt.setText("0");
                mFavBtn.setImageResource(R.drawable.heart_empty);
            }else{
                noLikesTxt.setText(String.valueOf(mAudio.getLikedBy().size()));
                if (mAudio.getLikedBy().contains(TJPreferences.getUserId(AudioDetail.this))){
                    mFavBtn.setImageResource(R.drawable.heart_full);
                }else {
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                }
            }
        }
        //If the activity is started for a newly clicked picture
        if (extras.getString("imagePath") != null) {
            isNewAudio = true;
            audioPath = extras.getString("imagePath");
            mAudio = new Audio(null, TJPreferences.getActiveJourneyId(this), HelpMe.AUDIO_TYPE, "3gp", 1223, null, audioPath, TJPreferences.getUserId(this), currenTime, currenTime, null);
        }

        //Setting fields common in both the cases
        //Profile picture
        String profileImgPath;
        if (!mAudio.getCreatedBy().equals(TJPreferences.getUserId(this))) {
            Contact contact = ContactDataSource.getContactById(this, mAudio.getCreatedBy());
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
        Log.d(TAG, "running for an already created audio 4");
    }

    private void setFavouriteBtnClickListener() {
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> likedBy = mAudio.getLikedBy();
                if(likedBy == null){
                    likedBy = new ArrayList<String>();
                }
                Log.d(TAG,
                        "fav button clicked position " + likedBy + TJPreferences.getUserId(AudioDetail.this));
                if (likedBy.contains(TJPreferences.getUserId(AudioDetail.this))) {
                    likedBy.remove(TJPreferences.getUserId(AudioDetail.this));
                    Log.d(TAG, "heart empty");
                    mFavBtn.setImageResource(R.drawable.heart_empty);
                } else {
                    likedBy.add(TJPreferences.getUserId(AudioDetail.this));
                    Log.d(TAG, "heart full");
                    mFavBtn.setImageResource(R.drawable.heart_full);
                }

                // update the value in the list and database
                noLikesTxt.setText(String.valueOf(likedBy.size()));
                if (likedBy.size() == 0) {
                    likedBy = null;
                }
                mAudio.setLikedBy(likedBy);
                if (!isNewAudio) {
                    mAudio.updateLikedBy(AudioDetail.this, mAudio.getId(), likedBy);
                }
            }
        });
    }

    private void saveAndUploadPic() {
        Log.d(TAG, "creating a new audio in local DB");

        if (likedBy != null) {
            mAudio.setLikedBy(likedBy);
        }
//        mAudio.setCaption(caption.getText().toString());
        AudioDataSource.createAudio(mAudio, this);
        AudioUtil.uploadAudio(this, mAudio);
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
                if (isNewAudio) {
                    saveAndUploadPic();
                }
                /*Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
