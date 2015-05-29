package com.example.memories.video;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class VideoCapture extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final String TAG = "<CaptureVideo>";
    private String mVideoPath;
    private String mVideoExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakeVideoIntent();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle("Capture Audio");
//        setSupportActionBar(toolbar);
    }

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            Log.d(TAG, "video saved at" + videoUri);
            Log.d(TAG, "Real path URI" + getRealPathFromURI(videoUri));
//            moveFileToTravelJarDir(getRealPathFromURI(videoUri));
//			saveAndUploadVideo();

            Intent i = new Intent(this, VideoDetail.class);
            i.putExtra("VIDEO_PATH", getRealPathFromURI(videoUri));
            startActivity(i);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /*private void moveFileToTravelJarDir(String sourceFilePath) {
        // File originally where the video is saved
        File sourceFile = new File(sourceFilePath);

        mVideoExtension = sourceFilePath.substring(sourceFilePath.lastIndexOf('.'));

        // directory path where we want to save our video
        mVideoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/TravelJar/Videos";
        File sourceDir = new File(mVideoPath);
        if (!sourceDir.exists()) {
            sourceDir.mkdirs();
        }
        mVideoPath += "/video_" + System.currentTimeMillis() + mVideoExtension;

        // destination file
        File destFile = new File(mVideoPath);
        // This will move the file to the traveljar video directory
        sourceFile.renameTo(destFile);

    }*/
}