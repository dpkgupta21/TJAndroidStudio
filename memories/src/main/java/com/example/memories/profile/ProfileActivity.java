package com.example.memories.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.R;
import com.example.memories.customviews.MyCircularImageView;
import com.example.memories.retrofit.StringConverter;
import com.example.memories.retrofit.TravelJarServices;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "<PROFILEACTIVITY>";
    private static final int REQUEST_CODE_UPDATE_PROFILE = 2;
    private static int PICK_IMAGE = 1;
    private MyCircularImageView mProfileImg;
    private String mProfileDir;
    private ImageView mChangePwdImg;
    private ImageView mChooseInterestImg;
    private TextView mUserName;
    private TextView mStatus;
    private EditText mUserNameEdit;
    private EditText mStatusEdit;
    private Boolean isProfilePicUpdated;
    private String mProfileImgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Capture Audio");
        setSupportActionBar(toolbar);

        mProfileImg = (MyCircularImageView) findViewById(R.id.profileImg);
        mChangePwdImg = (ImageView) findViewById(R.id.changePwd);
        mChooseInterestImg = (ImageView) findViewById(R.id.chooseInterest);
        mUserName = (TextView) findViewById(R.id.userName);
        mStatus = (TextView) findViewById(R.id.status);
        mUserNameEdit = (EditText) findViewById(R.id.userNameEdit);
        mStatusEdit = (EditText) findViewById(R.id.statusEdit);

        mUserName.setText(TJPreferences.getUserName(this));
        mStatusEdit.setText(TJPreferences.getUserStatus(this));
        mUserNameEdit.setText(TJPreferences.getUserName(this));

        mProfileDir = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES;

        setProfileImage();

        mProfileImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        mChangePwdImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View clickedView) {
                Intent intent = new Intent(ProfileActivity.this, ChangePassword.class);
                startActivity(intent);
            }
        });

        mChooseInterestImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ProfileActivity.this, ChooseInterest.class);
                startActivity(intent);
            }
        });
    }

    private void setProfileImage() {
        String profileImgPath = TJPreferences.getProfileImgPath(this);
        File imgFile = new File(profileImgPath);
        if (imgFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(profileImgPath);
            mProfileImg.setImageBitmap(bmp);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mStatusEdit.getText().toString().equals(TJPreferences.getUserStatus(this))) {
            TJPreferences.setUserStatus(this, mStatusEdit.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra("PROFILE_PICTURE_UPDATED", isProfilePicUpdated);
        if (!mUserNameEdit.getText().toString().equals(TJPreferences.getUserName(this))) {
            TJPreferences.setUserName(this, mUserNameEdit.getText().toString());
            intent.putExtra("USER_NAME_UPDATED", true);
        } else {
            intent.putExtra("USER_NAME_UPDATED", false);
        }
        setResult(RESULT_OK, intent);
        Log.d(TAG, "on back pressed called " + intent + RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            mProfileImgPath = HelpMe.getRealPathFromURI(data.getData(), this);
            Log.d(TAG, "New profile Image Path" + mProfileImgPath);
            try {
                TJPreferences.setProfileImgPath(this, mProfileImgPath);
                Bitmap profileImgThumbnail = HelpMe.decodeSampledBitmapFromPath(this,
                        mProfileImgPath, 110, 110);
                mProfileImg.setImageBitmap(profileImgThumbnail);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAdapter restAdapter = new RestAdapter.Builder().setConverter(new StringConverter())
                    .setEndpoint(Constants.TRAVELJAR_API_BASE_URL).build();
            TravelJarServices myService = restAdapter.create(TravelJarServices.class);

            isProfilePicUpdated = true;
            myService.updateProfilePicture(TJPreferences.getUserId(this),
                    new TypedString(TJPreferences.getApiKey(this)), new TypedFile("image/*", new File(
                            mProfileImgPath)), new Callback<String>() {
                        @Override
                        public void success(String str, retrofit.client.Response response) {
                            Log.d(TAG, "image uploaded successfully " + str);
                            Toast.makeText(ProfileActivity.this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void failure(RetrofitError retrofitError) {
                            Log.d(TAG, "error in uploading picture" + retrofitError);
                            Toast.makeText(ProfileActivity.this, "Profile picture could not be updated", Toast.LENGTH_SHORT).show();
                            retrofitError.printStackTrace();
                        }
                    });
            File sourceFile = new File(mProfileImgPath);
            File destinationFile = new File(mProfileDir + "/profile.jpg");
            try {
                FileUtils.copyFile(sourceFile, destinationFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
