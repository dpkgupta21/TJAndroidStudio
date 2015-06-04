package com.example.memories.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.memories.BaseActivity;
import com.example.memories.R;
import com.example.memories.customviews.MyCircularImageView;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    private static final String TAG = "<PROFILEACTIVITY>";
    private static final int REQUEST_CODE_UPDATE_PROFILE = 2;
    private static int PICK_IMAGE = 1;
    private MyCircularImageView mProfileImg;
    private ImageView mCoverImg;
    private TextView mUserName;
    private TextView mStatus;
    private EditText mEditName;
    private EditText mEditStatus;

    private boolean isProfilePicUpdated;
    private boolean isNameUpdated;
    private boolean isStatusUpdated;

    private String mProfileImgPath;

    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_new);

        mProfileImg = (MyCircularImageView) findViewById(R.id.profile_img);
        mCoverImg = (ImageView)findViewById(R.id.cover_image);
        mUserName = (TextView) findViewById(R.id.profile_name);
        mStatus = (TextView) findViewById(R.id.profile_status);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditStatus = (EditText) findViewById(R.id.edit_status);

        mUserName.setText(TJPreferences.getUserName(this));
        mStatus.setText(TJPreferences.getUserStatus(this));
        
        setProfileImage();

        mProfileImg.setOnClickListener(null);
    }

    private void setProfileImage() {
        String profileImgPath = TJPreferences.getProfileImgPath(this);
        File imgFile = new File(profileImgPath);
        if (imgFile.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(profileImgPath);
            mProfileImg.setImageBitmap(bmp);
            mCoverImg.setImageBitmap(bmp);
        }
    }

    @Override
    public void onBackPressed() {
        if (!mEditStatus.getText().toString().equals(TJPreferences.getUserStatus(this))) {
            TJPreferences.setUserStatus(this, mEditStatus.getText().toString());
        }

        Intent intent = new Intent();
        intent.putExtra("PROFILE_PICTURE_UPDATED", isProfilePicUpdated);
        if (!mEditName.getText().toString().equals(TJPreferences.getUserName(this))) {
            TJPreferences.setUserName(this, mEditName.getText().toString());
            intent.putExtra("USER_NAME_UPDATED", true);
        } else {
            intent.putExtra("Status_UPDATED", false);
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
                Bitmap profileImgThumbnail = HelpMe.decodeSampledBitmapFromPath(this,
                        mProfileImgPath, 110, 110);
                mProfileImg.setImageBitmap(profileImgThumbnail);
                isProfilePicUpdated = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //int groupId, int itemId, int order, int titleRes
        menu.add(0, 0, 0, "Done").setIcon(R.drawable.ic_done).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, 1, 1, "Edit").setIcon(R.drawable.ic_edit).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(0).setVisible(false);
        mMenu = menu;
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case 0:
                if(!HelpMe.isNetworkAvailable(this)){
                    Toast.makeText(this, "Network unavailable please try after some time", Toast.LENGTH_SHORT).show();
                }
                if (!mEditName.getText().toString().equals(TJPreferences.getUserName(this))) {
                    TJPreferences.setUserName(this, mEditName.getText().toString());
                    isNameUpdated = true;
                }
                if(!mEditStatus.getText().toString().equals(TJPreferences.getUserStatus(this))){
                    TJPreferences.setUserStatus(this, mEditStatus.getText().toString());
                    isStatusUpdated = true;
                }
                if(isProfilePicUpdated){
                    TJPreferences.setProfileImgPath(this, mProfileImgPath);
                }
                if(isNameUpdated || isStatusUpdated || isProfilePicUpdated){
                    new UpdateProfileAsyncTask().execute();
                }

                finish();
                return true;
            case 1:
                LinearLayout layout = (LinearLayout)findViewById(R.id.edit_layout);
                layout.setVisibility(View.VISIBLE);
                mEditName.setText(TJPreferences.getUserName(this));
                mEditStatus.setText(TJPreferences.getUserStatus(this));
                mProfileImg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intent, PICK_IMAGE);
                    }
                });
                item.setVisible(false);
                mMenu.findItem(0).setVisible(true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UpdateProfileAsyncTask extends AsyncTask<Map<String, String>, Void, String> {

        @Override
        protected String doInBackground(Map<String, String>... maps) {
            Log.d(TAG, "api key" + TJPreferences.getApiKey(ProfileActivity.this));
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            if(isProfilePicUpdated){
                entityBuilder.addPart("user[profile_picture]", new FileBody(new File(mProfileImgPath)));
            }
            if (isNameUpdated) {
                entityBuilder.addTextBody("user[name]", mEditName.getText().toString());
            }
            if(isStatusUpdated){
                entityBuilder.addTextBody("user[status]", mEditStatus.getText().toString());
            }
            entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(getBaseContext()));

            String url = Constants.URL_UPDATE_USER_DETAILS + TJPreferences.getUserId(getBaseContext());

            HttpPut updateProfileRequest = new HttpPut(url);

            updateProfileRequest.setEntity(entityBuilder.build());
            HttpResponse response;

            try {
                response = new DefaultHttpClient().execute(updateProfileRequest);
                Log.d("User", "response on profile Update" + response.getStatusLine());
            } catch (IOException e) {
                Log.d("User", "error in updating profile" + e.getMessage());
            }
            return null;
        }
    }

}

