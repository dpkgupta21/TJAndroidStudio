package com.traveljar.memories.video;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;

public class VideoGalleryGridAdapter extends BaseAdapter{
    private static final String TAG = "VideoGridAdapter";

    static Context mContext;
    private Cursor cursor;

    public VideoGalleryGridAdapter(Context context, Cursor cursor) {
        mContext = context;
        this.cursor = cursor;
    }

    static int imageWidthPixel() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 30 / displayMetrics.density) / 3;
        return width;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.media_gallery_grid_item, null);
            ViewHolder holder = new ViewHolder(rowView, mContext);
            rowView.setTag(holder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        cursor.moveToPosition(position);
        long id = cursor.getLong(0);
        final Bitmap thumb;
        thumb = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(), id,
                MediaStore.Video.Thumbnails.MICRO_KIND, null);
        holder.videoPlayImg.setVisibility(View.VISIBLE);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor. moveToPosition(position);
                long createdAt = HelpMe.getCurrentTime();
                Intent intent = new Intent(mContext, VideoPreview.class);
                intent.putExtra("VIDEO_PATH", moveFileToTravelJarDir(cursor.getString(1), createdAt));
                intent.putExtra("CREATED_AT", createdAt);
                mContext.startActivity(intent);
            }
        });
        Log.d(TAG, "thumbnail is " + position + "  " + thumb);
        holder.imgView.setImageBitmap(thumb);
        return rowView;
    }

    public static class ViewHolder {
        public ImageView imgView;
        public ImageView videoPlayImg;

        public ViewHolder(View rowView, Context context) {
            imgView = (ImageView) rowView.findViewById(R.id.gridImg);
            videoPlayImg = (ImageView) rowView.findViewById(R.id.media_type_video);

            imgView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidthPixel(),
                    imageWidthPixel()));
            //videoPlayImg.setLayoutParams(new RelativeLayout.LayoutParams(imageWidthPixel(),
//                    imageWidthPixel()));
        }
    }

    private String moveFileToTravelJarDir(String sourceFilePath, long createdAt) {
        // File originally where the video is saved
        File sourceFile = new File(sourceFilePath);

        String mVideoExtension = sourceFilePath.substring(sourceFilePath.lastIndexOf('.'));

        // directory path where we want to save our video
        File sourceDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath());
        if (!sourceDir.exists()) {
            sourceDir.mkdirs();
        }

        String mVideoPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() +
                "/vid_" + TJPreferences.getUserId(mContext) + "_" + TJPreferences.getActiveJourneyId(mContext) + "_" + createdAt + ".mp4";
        // destination file
        File destFile = new File(mVideoPath);
        // This will move the file to the traveljar video directory
        sourceFile.renameTo(destFile);
        return destFile.getAbsolutePath();
    }
}
