package com.traveljar.memories.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.traveljar.memories.BaseActivity;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.SQLitedatabase.MySQLiteHelper;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends BaseActivity {

    public ProfileActivity(){
        super(3);
    }

    private static final String TAG = "<PROFILEACTIVITY>";
    private static int PICK_IMAGE = 1;
    private ImageView mProfileImg;
    private ImageView mCoverImg;
    private TextView mUserName;
    private TextView mStatus;
    private EditText mEditName;
    private EditText mEditStatus;
    private ProgressDialog mDialog;

    private boolean isProfilePicUpdated;
    private boolean isNameUpdated;
    private boolean isStatusUpdated;

    private boolean isEditMode;

    private String mProfileImgPath;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_new);

        setUpToolbar();

        mProfileImg = (ImageView)findViewById(R.id.profile_img);
        mCoverImg = (ImageView) findViewById(R.id.cover_image);
        mUserName = (TextView) findViewById(R.id.profile_name);
        mStatus = (TextView) findViewById(R.id.profile_status);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mEditStatus = (EditText) findViewById(R.id.edit_status);

        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);

        mUserName.setText(TJPreferences.getUserName(this));
        mStatus.setText(TJPreferences.getUserStatus(this));

        setProfileImage();

        mProfileImg.setOnClickListener(null);
    }

    private void setProfileImage() {
        String profileImgPath = TJPreferences.getProfileImgPath(this);
        File imgFile = new File(profileImgPath);
        if (imgFile.exists()) {
            Glide.with(this).load(Uri.fromFile(new File(profileImgPath))).asBitmap().into(mProfileImg);
            Glide.with(this).load(Uri.fromFile(new File(profileImgPath))).asBitmap().into(mCoverImg);
        }
    }

    private void setUpToolbar(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Profile");

        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);

        toolbar.inflateMenu(R.menu.toolbar_profile);
        if(isEditMode){
            toolbar.getMenu().findItem(R.id.action_done).setVisible(false);
            toolbar.getMenu().findItem(R.id.action_edit).setVisible(true);
        }
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_done:
                        if (HelpMe.isNetworkAvailable(ProfileActivity.this)) {
                            List<String> columns = new ArrayList<>();
                            List<String> columnNames = new ArrayList<>();
                            if (!mEditName.getText().toString().equals(TJPreferences.getUserName(ProfileActivity.this))) {
                                isNameUpdated = true;
                                columns.add(mEditName.getText().toString());
                                columnNames.add(MySQLiteHelper.CONTACT_COLUMN_PROFILE_NAME);
                            }
                            if (!mEditStatus.getText().toString().equals(TJPreferences.getUserStatus(ProfileActivity.this))) {
                                isStatusUpdated = true;
                                columns.add(mEditStatus.getText().toString());
                                columnNames.add(MySQLiteHelper.CONTACT_COLUMN_STATUS);
                            }
                            if (isProfilePicUpdated) {
                                Log.d(TAG, "profile image path " + mProfileImgPath);
                                columns.add(mProfileImgPath);
                                columnNames.add(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL);
                            }
                            if (isNameUpdated || isStatusUpdated || isProfilePicUpdated) {
                                mDialog.setMessage("Updating your profile");
                                mDialog.show();
                                isEditMode = false;
                                new UpdateProfileAsyncTask().execute();
                                String columnNamesArray[] = new String[columnNames.size()];
                                String columnValuesArray[] = new String[columnNames.size()];
                                ContactDataSource.updateContact(ProfileActivity.this, TJPreferences.getUserId(ProfileActivity.this),
                                        columns.toArray(columnValuesArray), columnNames.toArray(columnNamesArray));
                            }else {
                                finish();
                            }
                        }else{
                            Toast.makeText(ProfileActivity.this, "Network unavailable please try after some time", Toast.LENGTH_SHORT).show();
                        }

                        return true;
                    case R.id.action_edit:
                        isEditMode = true;
                        LinearLayout layout = (LinearLayout) findViewById(R.id.edit_layout);
                        layout.setVisibility(View.VISIBLE);
                        mEditName.setText(TJPreferences.getUserName(ProfileActivity.this));
                        mEditStatus.setText(TJPreferences.getUserStatus(ProfileActivity.this));
                        mProfileImg.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View arg0) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                startActivityForResult(intent, PICK_IMAGE);
                            }
                        });
                        item.setVisible(false);
                        toolbar.getMenu().findItem(R.id.action_done).setVisible(true);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && null != data) {
            mProfileImgPath = HelpMe.getRealPathFromURI(data.getData(), this);
            Log.d(TAG, "New profile Image Path" + mProfileImgPath);
            Glide.with(this).load(Uri.fromFile(new File(mProfileImgPath))).asBitmap().into(mProfileImg);
            isProfilePicUpdated = true;
        }
    }

    private class UpdateProfileAsyncTask extends AsyncTask<Map<String, String>, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(Map<String, String>... maps) {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            if (isProfilePicUpdated) {
                entityBuilder.addPart("user[profile_picture]", new FileBody(new File(mProfileImgPath)));
            }
            if (isNameUpdated) {
                entityBuilder.addTextBody("user[name]", mEditName.getText().toString());
            }
            if (isStatusUpdated) {
                entityBuilder.addTextBody("user[status]", mEditStatus.getText().toString());
            }
            Log.d(TAG, "reg id is " + TJPreferences.getGcmRegId(getBaseContext()));
            entityBuilder.addTextBody("api_key", TJPreferences.getApiKey(getBaseContext()));
            entityBuilder.addTextBody("user[red_id]", TJPreferences.getGcmRegId(getBaseContext()));

            String url = Constants.URL_UPDATE_USER_DETAILS + TJPreferences.getUserId(getBaseContext());
            Log.d(TAG, "url is " + url);

            HttpPut updateProfileRequest = new HttpPut(url);

            updateProfileRequest.setEntity(entityBuilder.build());
            HttpResponse response;

            try {
                response = new DefaultHttpClient().execute(updateProfileRequest);
                Log.d("User", "response on profile Update" + response.getStatusLine());
                return response;
            } catch (IOException e) {
                Log.d("User", "error in updating profile" + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(HttpResponse result) {
            mDialog.dismiss();
            if (result == null) {
                Glide.with(ProfileActivity.this).load(Uri.fromFile(new File(mProfileImgPath))).asBitmap().into(mProfileImg);
                Toast.makeText(ProfileActivity.this, "Unable to update Profile please try after some time", Toast.LENGTH_SHORT).show();
            }else {
                Contact contact = ContactDataSource.getContactById(ProfileActivity.this, TJPreferences.getUserId(ProfileActivity.this));
                if(isNameUpdated){
                    TJPreferences.setUserName(ProfileActivity.this, mEditName.getText().toString());
                    contact.setProfileName(mEditName.getText().toString());
                }
                if(isStatusUpdated){
                    TJPreferences.setUserStatus(ProfileActivity.this, mEditStatus.getText().toString());
                    contact.setStatus(mEditStatus.getText().toString());
                }
                if(isProfilePicUpdated){
                    TJPreferences.setProfileImgPath(ProfileActivity.this, mProfileImgPath);
                    contact.setPicLocalUrl(mProfileImgPath);
                }
                ContactDataSource.updateContact(ProfileActivity.this, contact);
                mDialog.dismiss();
                Intent intent = getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(mDialog.isShowing()){
            mDialog.dismiss();
        }
        Intent i = new Intent(this, ActivejourneyList.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}

