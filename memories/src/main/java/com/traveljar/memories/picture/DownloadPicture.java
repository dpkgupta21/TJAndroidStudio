package com.traveljar.memories.picture;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.volley.AppController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadPicture {

    private static final String TAG = "DownloadPicture";
    private Picture picture;
    private OnPictureDownloadListener mListener;

    public DownloadPicture(Picture picture, OnPictureDownloadListener listener) {
        this.picture = picture;
        mListener = listener;
    }

    public void startDownloadingPic() {
        Log.d(TAG, "download pic called");
        String picServerUrl = picture.getDataServerURL();
        final String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                "/pic_" + picture.getCreatedBy() + "_" + picture.getjId() + "_" + picture.getCreatedAt() + ".jpg";
        File file = new File(imagePath);
        if(!file.exists()) {
            if (picServerUrl != null) {
                ImageRequest request = new ImageRequest(picServerUrl, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        FileOutputStream out = null;
                        try {
                            out = new FileOutputStream(imagePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            picture.setDataLocalURL(imagePath);
                            mListener.onDownloadPicture(picture, true);
                            Log.d(TAG, "picture downloaded successfully");
                        } catch (Exception e) {
                            e.printStackTrace();
                            mListener.onDownloadPicture(picture, false);
                        } finally {
                            try {
                                if (out != null) {
                                    out.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 0, 0, null, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            } else {
                Log.d(TAG, "pic server url is null");
            }
        }else {
            picture.setDataLocalURL(imagePath);
            mListener.onDownloadPicture(picture, true);
        }
    }

    public interface OnPictureDownloadListener {
        void onDownloadPicture(Picture picture, boolean result);
    }
}
