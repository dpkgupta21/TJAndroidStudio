package com.example.memories.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.widget.ImageView;

import com.example.memories.R;

import java.lang.ref.WeakReference;

public class LoadThumbnailFromPath {
    public static void loadBitmap(String imagePath, ImageView imageView,
                                  Context context) {
        if (cancelPotentialWork(imagePath, imageView)) {
            final LoadThumbnailsTask task = new LoadThumbnailsTask(imageView, context);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(),
                    BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.image_load_placeholder), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(imagePath);
        }
    }

    public static boolean cancelPotentialWork(String imagePath, ImageView imageView) {
        final LoadThumbnailsTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            String bitmapPath = bitmapWorkerTask.data;
            if (bitmapPath != imagePath) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was
        // cancelled
        return true;
    }

    private static LoadThumbnailsTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<LoadThumbnailsTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, LoadThumbnailsTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<LoadThumbnailsTask>(bitmapWorkerTask);
        }

        public LoadThumbnailsTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}

class LoadThumbnailsTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "BitmapWorkerTask";
    private final WeakReference<ImageView> imageViewReference;
    public String data = "";
    Context context;

    public LoadThumbnailsTask(ImageView imageView, Context context) {
        imageViewReference = new WeakReference<ImageView>(imageView);
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        Bitmap bitmap;
        bitmap = ThumbnailUtils.createVideoThumbnail(data, Thumbnails.MINI_KIND);
        Log.d(TAG, "do in background");
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
