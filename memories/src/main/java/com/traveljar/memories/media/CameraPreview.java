package com.traveljar.memories.media;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * A basic Camera preview class
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public static final String LOG_TAG = "CameraPreview";
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    // Constructor that obtains context and camera
    @SuppressWarnings("deprecation")
    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        Log.d(LOG_TAG, "mCamera :: " + mCamera);
        this.mSurfaceHolder = this.getHolder();
        this.mSurfaceHolder.addCallback(this);
        this.mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        Log.d(LOG_TAG, "mSurfaceHolder :: " + mSurfaceHolder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera == null)
                mCamera = Camera.open();
            mCamera.setPreviewDisplay(surfaceHolder);
            Log.d(LOG_TAG, "surfaceCreated (setPreview) :: " + mCamera);
            mCamera.startPreview();
            Log.d(LOG_TAG, "surfaceCreated (startPreview) :: " + mCamera);
        } catch (Exception e) {
            // left blank for now
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }catch (Exception e){
            mCamera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        Log.d(LOG_TAG, "on surface destroyed");
        try {
            if (mCamera == null)
                mCamera = Camera.open();
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
                parameters.setRotation(90);
            }else {
                // This is an undocumented although widely known feature
                parameters.set("orientation", "landscape");
                // For Android 2.2 and above
                mCamera.setDisplayOrientation(0);
                // Uncomment for Android 2.0 and above
                parameters.setRotation(0);
            }
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // intentionally left blank for a test
        }
    }
}