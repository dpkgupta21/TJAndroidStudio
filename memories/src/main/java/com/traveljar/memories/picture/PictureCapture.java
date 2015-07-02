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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PictureCapture extends AppCompatActivity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int PICK_GALLERY_IMAGE_REQUEST_CODE = 2;
    private static final String TAG = "CAPTURE_PHOTOS";
    private String imagePath;
    private ImageView mImageView;
    private LinearLayout mPageFooter;
    private LinearLayout mCaptureOptions;
    private Button mSelectGalleryImage;
    private Button mClickNewPicture;
    private long createdAt;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_capture);

        mImageView = (ImageView) findViewById(R.id.capture_photos_image_preview);
        mPageFooter = (LinearLayout)findViewById(R.id.capture_photos_preview_footer);
        mCaptureOptions = (LinearLayout)findViewById(R.id.capture_picture_options);
        mSelectGalleryImage = (Button) findViewById(R.id.choose_gallery_picture);
        mClickNewPicture = (Button) findViewById(R.id.click_new_picture);

        mClickNewPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture();
            }
        });

        mSelectGalleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureFromGallery();
            }
        });
    }

    private void takePictureFromGallery(){
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_GALLERY_IMAGE_REQUEST_CODE);
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
        createdAt = System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String fileName = "/pic_" + TJPreferences.getUserId(this) + "_" + TJPreferences.getActiveJourneyId(this) + "_" + createdAt + ".jpg";
        File file = new File(storageDir, fileName);
        file.createNewFile();
        imagePath = file.getAbsolutePath();
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "on activity result " + resultCode + RESULT_OK);
        if(resultCode == RESULT_OK) {
            //Make the preview view visible and hide both the buttons
            mCaptureOptions.setVisibility(View.GONE);
            mPageFooter.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            Bitmap bitmap = null;

            // If new image is clicked
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                try {
                    bitmap = BitmapFactory.decodeFile(imagePath);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                int rotation = getImageRotationInDegrees();
                if (rotation != 0) {
                    bitmap = getAdjustedBitmap(bitmap, rotation);
                    Log.d(TAG, "calling replace image");
                    replaceImg(bitmap);
                    Log.d("TAG", "bitmap compressed successfully");
                }
                mImageView.setImageBitmap(bitmap);
            }

            // If image is picked from gallery
            if(requestCode == PICK_GALLERY_IMAGE_REQUEST_CODE){
                Uri selectedImageUri = data.getData();
                imagePath = copyPictureToTJDir(HelpMe.getRealPathFromURI(selectedImageUri, this));
                Intent i = new Intent(getBaseContext(), PicturePreview.class);
                i.putExtra("imagePath", imagePath);
                i.putExtra("CREATED_AT", createdAt);
                startActivity(i);
                Log.d(TAG, "ok pic");
                finish();
            }

        }
        if(resultCode == RESULT_CANCELED){
            finish();
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

    private String copyPictureToTJDir(String sourceFilePath) {
        File destinationFile = null;
        try {
            destinationFile = getOutputMediaFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream in = null;
        try {
            in = new FileInputStream(sourceFilePath);
            OutputStream out = new FileOutputStream(destinationFile.getAbsolutePath());
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return destinationFile.getAbsolutePath();
    }

}
