package com.example.memories.picture;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.memories.R;

/**
 * Created by ankit on 2/6/15.
 */
public class DisplayPicture extends Activity {

    private static final String TAG = "display_picture";
    private String mPictureLocalPath;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_display);
        
        Log.d(TAG, "display picture with path " + mPictureLocalPath);

        mPictureLocalPath = getIntent().getExtras().getString("PICTURE_PATH");
        mImageView = (ImageView)findViewById(R.id.picture_image_view);

        mImageView.setImageBitmap(BitmapFactory.decodeFile(mPictureLocalPath));

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

}
