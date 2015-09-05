package com.traveljar.memories.picture;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class UploadPictureFromCamera extends Fragment{
    private static final String TAG = "uploadPicFromCam";
    private View rootView;
    private Camera mCamera;
    private CameraPreview mPreview;
    private FrameLayout preview;
    private Button captureButton;
    private String imagePath;
    private long createdAt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.capture_image, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDeviceSupportCamera(getActivity())) {
            // initialize camera and display it
            capture();
        } else {
            Toast.makeText(getActivity(), "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
        }
    }

    public void capture() {
        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        // Add a listener to the Capture button
        captureButton = (Button) rootView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                mCamera.takePicture(null, null, mPicture);
            }
        });
    }

    private boolean isDeviceSupportCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
        }
        return c;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = null;
            try {
                pictureFile = getOutputMediaFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pictureFile == null) {
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                int rotation = getImageRotationInDegrees();
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                Log.d(TAG, "rotation is " + rotation);
                if (rotation != 0) {
                    bitmap = getAdjustedBitmap(bitmap, rotation);
                    Log.d(TAG, "calling replace image");
                    replaceImg(bitmap);
                    Log.d("TAG", "bitmap compressed successfully");
                }
                Intent i = new Intent(getActivity(), PicturePreview.class);
                i.putExtra("imagePath", imagePath);
                startActivity(i);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private File getOutputMediaFile() throws IOException {
        createdAt = System.currentTimeMillis();
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String fileName = "/pic_" + TJPreferences.getUserId(getActivity()) + "_" + TJPreferences.getActiveJourneyId(getActivity()) + "_" + createdAt + ".jpg";
        File file = new File(storageDir, fileName);
        file.createNewFile();
        imagePath = file.getAbsolutePath();
        return file;
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
