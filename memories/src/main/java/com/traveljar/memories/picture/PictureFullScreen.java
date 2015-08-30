package com.traveljar.memories.picture;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by ankit on 2/6/15.
 */
public class PictureFullScreen extends Activity {

    private static final String TAG = "display_picture";
    private String mPictureLocalPath;
    private ImageView mImageView;
    private PhotoViewAttacher mAttacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_display);

        Log.d(TAG, "display picture with path " + mPictureLocalPath);

        mPictureLocalPath = getIntent().getExtras().getString("PICTURE_PATH");
        mImageView = (ImageView) findViewById(R.id.picture_image_view);

        Glide.with(this).load(mPictureLocalPath).into(mImageView);

        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
        mAttacher = new PhotoViewAttacher(mImageView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
