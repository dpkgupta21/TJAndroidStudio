package com.traveljar.memories.checkin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.models.Request;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckInPreview extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "<CheckInPreview>";
    private static final int REQUEST_CODE_SELECT_FRIENDS = 2;
    private String placeName;
    private EditText checkinDetailsCaption;
    private TextView checkinDetailsPlace;
    private TextView checkinDetailsBuddies;
    private ImageButton img;
    private double lat;
    private double longi;
    private List<Contact> mContactsList;
    private long createdAt;
    private String picUrl;
    private String picThumbnailPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_preview);

        setUpToolBar();

        // get the name of the place
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            placeName = extras.getString("placeName");
            lat = extras.getDouble("latitude");
            longi = extras.getDouble("longitude");
            Log.d(TAG, "latitude, longitude " + lat + " " + longi);
        }

//        mContactsList = ContactDataSource.getContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        mContactsList = ContactDataSource.getAllContactsFromJourney(this, TJPreferences.getActiveJourneyId(this));
        Log.d(TAG, "buddies in journey are " + mContactsList.size());

        // update the textview in the layout
        checkinDetailsCaption = (EditText) findViewById(R.id.checkin_details_caption);
        checkinDetailsPlace = (TextView) findViewById(R.id.checkin_details_location);
        checkinDetailsBuddies = (TextView)findViewById(R.id.checkin_friends);
        checkinDetailsPlace.append(placeName);
        img = (ImageButton) findViewById(R.id.checkin_details_image);

        for(Contact contact : mContactsList){
            contact.setSelected(true);
        }
        setSelectedFriends();

    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Checkin");

        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        TextView done = (TextView)toolbar.findViewById(R.id.action_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.action_done:
                        Log.d(TAG, "done clicked!");
                        createNewCheckinIntoDB();
                        finish();
                }
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckInPreview.this.finish();
            }
        });
    }

    private void setSelectedFriends() {
        List<Contact> selectedFriends = new ArrayList<>();
        for (Contact contact : mContactsList) {
            if (contact.isSelected()) {
                selectedFriends.add(contact);
            }
        }
        String checkinWithText = "";
        if (selectedFriends.size() > 0) {
            Contact contact = ContactDataSource.getContactById(this, selectedFriends.get(0).getIdOnServer());
            if (selectedFriends.size() == 1) {
                checkinWithText += "- with " + ((contact == null) ? "" : contact.getProfileName());
            } else {
                checkinWithText += "- with " + ((contact == null) ? "" : contact.getProfileName()) + " and " + (selectedFriends.size() - 1) + " others";
            }
        }
        checkinDetailsBuddies.setText(checkinWithText);

    }

    private void createNewCheckinIntoDB() {
        Log.d(TAG, "creating a new checkin in local DB");

        //Getting the contact ids of the selected contacts
        List<String> selectedFriends = new ArrayList<>();
        for (Contact contact : mContactsList) {
            if (contact.isSelected()) {
                selectedFriends.add(contact.getIdOnServer());
            }
        }

        if(picUrl != null) {
            createThumbnail();
        }

        String j_id = TJPreferences.getActiveJourneyId(this);
        String user_id = TJPreferences.getUserId(this);

        CheckIn newCheckIn = new CheckIn(null, j_id, HelpMe.CHECKIN_TYPE, checkinDetailsCaption.getText().toString().trim(),
                lat, longi, placeName, picUrl, null, picThumbnailPath, selectedFriends, user_id, HelpMe.getCurrentTime(),
                HelpMe.getCurrentTime());

        Log.d(TAG, "latitude -> " + newCheckIn.getLatitude() + " longitude -> " + longi + newCheckIn.getLongitude());
        Long id = CheckinDataSource.createCheckIn(newCheckIn, this);
        newCheckIn.setId(String.valueOf(id));

        Request request = new Request(null, String.valueOf(id), TJPreferences.getActiveJourneyId(this),
                Request.OPERATION_TYPE_CREATE, Request.CATEGORY_TYPE_CHECKIN, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, this);
        if(HelpMe.isNetworkAvailable(this)) {
            Intent intent = new Intent(this, MakeServerRequestsService.class);
            startService(intent);
        }
        else{
            Log.d(TAG, "since no network not starting service RQ");
        }
    }

    /* Create a File for saving an image or video*/
    private File getOutputMediaFile() {
        createdAt = System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String fileName = "/pic_" + TJPreferences.getUserId(this) + "_" + TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".jpg";
        File file = new File(storageDir, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        picUrl = file.getAbsolutePath();
        Log.d(TAG, picUrl + "  " + file.getAbsolutePath());
        return file;
    }

    public void goToPlaceList(View v) {
        Intent i = new Intent(getApplicationContext(), CheckInPlacesList.class);
        i.putExtra("placeName", placeName);
        startActivity(i);
    }

    public void goToBuddyList(View v) {
        Intent intent = new Intent(getApplicationContext(), CheckInFriendsList.class);
        intent.putParcelableArrayListExtra("FRIENDS", mContactsList == null ? null : (ArrayList<Contact>) mContactsList);
        startActivityForResult(intent, REQUEST_CODE_SELECT_FRIENDS);
    }

    public void goToCamera(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = getOutputMediaFile();
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    private void createThumbnail(){
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(picUrl), 512, 384);
        picThumbnailPath = Constants.TRAVELJAR_FOLDER_PICTURE + "thumb_" + TJPreferences.getUserId(this) + "_" +
                TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".jpg";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picThumbnailPath);
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "result code = " + resultCode + " request code = " + requestCode);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(picUrl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Log.d(TAG, "pic url is 1" + picUrl);
            int rotation = getImageRotationInDegrees();
            if (rotation != 0) {
                bitmap = getAdjustedBitmap(bitmap, rotation);
                Log.d(TAG, "calling replace image");
                replaceImg(bitmap);
                Log.d("TAG", "bitmap compressed successfully");
            }
            img.setImageBitmap(bitmap);
        }

        if (requestCode == REQUEST_CODE_SELECT_FRIENDS && resultCode == RESULT_OK) {
            mContactsList = data.getParcelableArrayListExtra("FRIENDS");
            Log.d(TAG, "from on activity result" + mContactsList);
            for(Contact c : mContactsList){
                Log.d(TAG, "is selected" + c.isSelected());
            }
            setSelectedFriends();
        }
    }

    private int getImageRotationInDegrees(){
        try {
            Log.d(TAG, "pic url is " + picUrl);
            ExifInterface exif = new ExifInterface(picUrl);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
            else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Bitmap getAdjustedBitmap(Bitmap bitmap, int rotate) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bitmap;
    }

    private void replaceImg(Bitmap bitmap){
        File file = new File(picUrl);
        file.delete();
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
