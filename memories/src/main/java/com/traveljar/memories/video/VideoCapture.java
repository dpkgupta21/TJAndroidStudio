package com.traveljar.memories.video;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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
        if(resultCode == RESULT_CANCELED){
            onBackPressed();
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            long createdAt = HelpMe.getCurrentTime();
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() +
                    "/vid_" + TJPreferences.getUserId(this) + "_" + TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".mp4";
            try {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();

                File root=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());

                if (!root.exists()) {
                    root.mkdirs();
                }

                File file;
                file = new File(filePath);

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

/*            Uri videoUri = data.getData();
            Log.d(TAG, "video saved at" + videoUri);
            Log.d(TAG, "Real path URI" + getRealPathFromURI(videoUri));*/

            Intent i = new Intent(this, VideoPreview.class);
            i.putExtra("VIDEO_PATH", filePath);
            i.putExtra("CREATED_AT", createdAt);
            startActivity(i);
            finish();
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