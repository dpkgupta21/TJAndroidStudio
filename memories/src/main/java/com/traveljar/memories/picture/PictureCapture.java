package com.traveljar.memories.picture;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.traveljar.memories.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PictureCapture extends AppCompatActivity {

    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final String TAG = "CAPTURE_PHOTOS";
    // private static final String TAG = "<CapturePhotos>";
    private String imagePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);

        capture();
    }

    public void retakePic(View v) {
        capture();
    }

    public void okPic(View v) {
        Intent i = new Intent(getBaseContext(), PicturePreview.class);
        i.putExtra("imagePath", imagePath);
        startActivity(i);
        Log.d(TAG, "ok pic");
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
        Log.d(TAG, "on activity result " + resultCode + RESULT_OK);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            ImageView img = (ImageView) findViewById(R.id.capture_photos_image_preview);
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(imagePath);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            int rotation = getImageRotationInDegrees();
            if(rotation != 0){
                bitmap = getAdjustedBitmap(bitmap, rotation);
                Log.d(TAG, "calling replace image");
                replaceImg(bitmap);
                Log.d("TAG", "bitmap compressed successfully");
            }
            img.setImageBitmap(bitmap);
//            new replacePictureTask().execute(new Object[]{new File(imagePath), bitmap});
        }
    }

    private int getImageRotationInDegrees(){
        try {
            ExifInterface exif = new ExifInterface(imagePath);
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
        File file = new File(imagePath);
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
