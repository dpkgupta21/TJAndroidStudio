package com.example.memories.picture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.memories.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CapturePhotos extends Activity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "CAPTURE_PHOTOS";
    // private static final String TAG = "<CapturePhotos>";
    private String imagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capture_moments_photos);

        getActionBar().hide();
        capture();
    }

    public void retakePic(View v) {
        capture();
    }

    public void okPic(View v) {
        Intent i = new Intent(getBaseContext(), PhotoDetail.class);
        i.putExtra("imagePath", imagePath);
        startActivity(i);
        finish();
    }

    // CAMERA METHODS
    // -----------------------------------------------------------------

    private void capture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = getOutputMediaFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }


    //  Create a file Uri for saving an image or video
    // returns a new file on the image will be storeds
    private File getOutputMediaFile() throws IOException {
        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/TravelJar/Pictures");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File imageFile = null;
        imageFile = File.createTempFile("pic_" + System.currentTimeMillis(), ".jpg", storageDir);
        imagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageView img = (ImageView) findViewById(R.id.capture_photos_image_preview);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imagePath);
            } catch (Exception ex) {

            }
            bitmap = getAdjustedBitmap(this, bitmap);
            img.setImageBitmap(bitmap);
            new replacePictureTask().execute(new Object[]{new File(imagePath), bitmap});
        }
    }

    public Bitmap getAdjustedBitmap(Context context, Bitmap bitmap) {

        int rotate = 0;
        try {
            File imageFile = new File(imagePath);
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
        return bitmap;
    }

    private class replacePictureTask extends AsyncTask<Object, Integer, String> {

        @Override
        protected String doInBackground(Object... url) {
            Log.d("TAG", "inside download task do in background");
            File file = (File) url[0];
            Bitmap bitmap = (Bitmap) url[1];
            file.delete();
            //FileOutputStream fOut = null;
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                Log.d("TAG", "fine till here");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {

            }
        }
    }

}
