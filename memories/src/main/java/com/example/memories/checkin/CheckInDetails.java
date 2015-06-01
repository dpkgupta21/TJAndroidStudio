package com.example.memories.checkin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.CheckinDataSource;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.models.CheckIn;
import com.example.memories.models.Contact;
import com.example.memories.utility.CheckinUtil;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CheckInDetails extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "<CheckInDetails>";
    private static final int REQUEST_CODE_SELECT_FRIENDS = 2;
    private Uri fileUri;
    private String placeName;
    private EditText checkinDetailsCaption;
    private TextView checkinDetailsPlace;
    private TextView checkinDetailsBuddies;
    private double lat;
    private double longi;
    private List<String> mSelectedFriends;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        Log.d(TAG, "1");
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        Log.d(TAG, "2");
        // To be safe, you should check that the SDCard is mounted
        Log.d(TAG, "isSDcardmounted = " + Environment.getExternalStorageState());

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "TravelJar");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.d(TAG, "3");
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "4");
                Log.d("TravelJar", "failed to create directory");
                return null;
            }
        }

        Log.d(TAG, "5");
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp
                + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Checkin");
        setSupportActionBar(toolbar);

        // get the name of the place
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeName = extras.getString("placeName");
            lat = extras.getDouble("latitude");
            longi = extras.getDouble("longitude");
            Log.d(TAG, "latitude, longitude " + lat + " " + longi);
        }
        //Get all the friends
        String buddyIds[] = JourneyDataSource.getBuddyIdsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        if (buddyIds != null) {
            mSelectedFriends = Arrays.asList(buddyIds);
        }

        // get the friends list
/*        Integer buddyCount = getResources().getStringArray(R.array.journey_buddies_list).length - 1;
        String buddiesList = getResources().getStringArray(R.array.journey_buddies_list)[0]
                + " and " + buddyCount.toString() + " others";*/

        // update the textview in the layout
        checkinDetailsCaption = (EditText) findViewById(R.id.checkin_details_caption);
        checkinDetailsPlace = (TextView) findViewById(R.id.checkin_details_location);
        checkinDetailsBuddies = (TextView) findViewById(R.id.checkin_friends);

        checkinDetailsPlace.append(placeName);
        setSelectedFriends();

    }

    private void setSelectedFriends() {
        if (mSelectedFriends != null) {
            String checkinWithText = "";
            if (mSelectedFriends.size() > 0) {
                Contact contact = ContactDataSource.getContactById(this, mSelectedFriends.get(0));
                if (mSelectedFriends.size() == 1) {
                    checkinWithText += "- with " + ((contact == null) ? "" : contact.getName());
                } else {
                    checkinWithText += "- with " + ((contact == null) ? "" : contact.getName()) + " and " + (mSelectedFriends.size() - 1) + " others";
                }
            }
            checkinDetailsBuddies.setText(checkinWithText);
        }
    }

    private void createNewCheckinIntoDB() {
        Log.d(TAG, "creating a new checkin in local DB");

        String j_id = TJPreferences.getActiveJourneyId(this);
        String user_id = TJPreferences.getUserId(this);

        CheckIn newCheckIn = new CheckIn(null, j_id, HelpMe.CHECKIN_TYPE, checkinDetailsCaption
                .getText().toString().trim(), lat, longi, placeName, null, mSelectedFriends, user_id,
                HelpMe.getCurrentTime(), HelpMe.getCurrentTime(), null);

        CheckinDataSource.createCheckIn(newCheckIn, this);
        CheckinUtil.uploadCheckin(newCheckIn, this);
    }

    public void goToPlaceList(View v) {
        Intent i = new Intent(getApplicationContext(), CheckInPlacesList.class);
        i.putExtra("placeName", placeName);
        startActivity(i);
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
                createNewCheckinIntoDB();
                /*Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void goToBuddyList(View v) {
        Intent intent = new Intent(getApplicationContext(), CheckInFriendsList.class);
        intent.putStringArrayListExtra("SELECTED_FRIENDS", mSelectedFriends == null ? null : new ArrayList<String>(mSelectedFriends));
        startActivityForResult(intent, REQUEST_CODE_SELECT_FRIENDS);
    }

    public void goToMoods(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "6" + requestCode + "--" + resultCode + "--" + data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                ImageButton img = (ImageButton) findViewById(R.id.checkin_details_image);
                Log.d(TAG, fileUri.toString());
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(photo);

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        } else if (requestCode == REQUEST_CODE_SELECT_FRIENDS) {
            if (resultCode == RESULT_OK) {
                mSelectedFriends = data.getStringArrayListExtra("SELECTED_FRIENDS");
                setSelectedFriends();
            }
        }
    }

    // CAMERA METHODS
    // -----------------------------------------------------------------
    public void goToCamera(View v) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // create a file to save the image
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

}
