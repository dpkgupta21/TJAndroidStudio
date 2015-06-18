package com.example.memories.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.example.memories.R;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

/**
 * Created by ankit on 18/6/15.
 */
public class LoadScaledBitmapFromPath {

    public static void loadBitmap(String imagePath, ImageView imageView, int height, int width,
                                  Context context) {
        if (cancelPotentialWork(imagePath, imageView)) {
            final LoadBitmapTask task = new LoadBitmapTask(imageView, context, width, height);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(),
                    BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.image_load_placeholder), task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(imagePath);
        }
    }

    public static boolean cancelPotentialWork(String imagePath, ImageView imageView) {
        final LoadBitmapTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

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

    private static LoadBitmapTask getBitmapWorkerTask(ImageView imageView) {
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
        private final WeakReference<LoadBitmapTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, LoadBitmapTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        public LoadBitmapTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
}

class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    public String data = "";
    Context context;
    int width;
    int height;

    public LoadBitmapTask(ImageView imageView, Context context, int width, int height) {
        imageViewReference = new WeakReference<>(imageView);
        this.context = context;
        this.width = width;
        this.height = height;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        data = params[0];
        Bitmap bitmap = null;

        try {
            bitmap = HelpMe.decodeSampledBitmapFromPath(context, data, width, height);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

