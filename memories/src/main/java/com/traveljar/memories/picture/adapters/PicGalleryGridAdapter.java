package com.traveljar.memories.picture.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.picture.PicturePreview;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PicGalleryGridAdapter extends BaseAdapter{
    private static final String TAG = "MediaGridAdapter";

    static Context mContext;
    private Cursor cursor;

    public PicGalleryGridAdapter(Context context, Cursor cursor) {
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

//        thumb = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        Glide.with(mContext).load(Uri.fromFile(new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))))).into(holder.imgView);

        holder.videoPlayImg.setVisibility(View.GONE);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                long createdAt = HelpMe.getCurrentTime();
                Intent intent = new Intent(mContext, PicturePreview.class);
                intent.putExtra("imagePath", cursor.getString(1));
                intent.putExtra("IS_PIC_FROM_GALLERY", true);
                intent.putExtra("CREATED_AT", createdAt);
                mContext.startActivity(intent);
            }
        });
        Log.d(TAG, "inside Pic gallery grid adapter ");
/*        Log.d(TAG, "thumbnail is " + position + "  " + thumb);
        holder.imgView.setImageBitmap(thumb);*/
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

    private String replaceImg(Bitmap bitmap, long createdAt){
        Log.d(TAG, "bitmap is " + bitmap);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        String fileName = "/pic_" + TJPreferences.getUserId(mContext) + "_" + TJPreferences.getActiveJourneyId(mContext) + "_" + createdAt + ".jpg";
        File videoFile = new File(storageDir, fileName);
        try {
            videoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(videoFile);
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
        return videoFile.getAbsolutePath();
    }
}
