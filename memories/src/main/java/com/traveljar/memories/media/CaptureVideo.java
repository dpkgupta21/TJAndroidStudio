package com.traveljar.memories.media;

import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.video.VideoPreview;

import java.io.IOException;

public class CaptureVideo extends Fragment{
    private Camera myCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private View rootView;
    private Button captureButton;
    private SurfaceHolder surfaceHolder;
    boolean recording;
    long createdAt;
    private String filePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.capture_image, container, false);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recording = false;

        //Get Camera for preview
        myCamera = getCameraInstance();
        if(myCamera == null){
            Toast.makeText(getActivity(), "Fail to get Camera", Toast.LENGTH_LONG).show();
        }

        mPreview = new CameraPreview(getActivity(), myCamera);
        FrameLayout myCameraPreview = (FrameLayout)rootView.findViewById(R.id.camera_preview);
        myCameraPreview.addView(mPreview);

        captureButton = (Button)rootView.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(captureButtonClickListener);
    }

    Button.OnClickListener captureButtonClickListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if(recording){
                mediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                Intent i = new Intent(getActivity(), VideoPreview.class);
                i.putExtra("VIDEO_PATH", filePath);
                i.putExtra("CREATED_AT", createdAt);
                startActivity(i);
                getActivity().finish();
            }else{
                createdAt = HelpMe.getCurrentTime();
                //Release Camera before MediaRecorder start
                releaseCamera();

                if(!prepareMediaRecorder()){
                    Toast.makeText(getActivity(), "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                }

                mediaRecorder.start();
                recording = true;
                captureButton.setText("STOP");
            }
        }};

    private Camera getCameraInstance(){
// TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private boolean prepareMediaRecorder(){
        myCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();

        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        mediaRecorder.setOutputFile(getOutputMediaFilePath());
        mediaRecorder.setMaxDuration(60000); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M

        mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    @Override
    public void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }

    private String getOutputMediaFilePath(){
        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() +
                "/vid_" + TJPreferences.getUserId(getActivity()) + "_" + TJPreferences.getActiveJourneyId(getActivity()) + "_"
                + createdAt + ".mp4";
        return filePath;
    }

}
