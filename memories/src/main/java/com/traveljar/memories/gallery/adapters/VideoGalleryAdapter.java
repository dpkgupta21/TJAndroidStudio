package com.traveljar.memories.gallery.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Video;

import java.io.File;
import java.util.List;

public class VideoGalleryAdapter extends BaseAdapter {
    private static final String TAG = "GalleryImageAdapter";

    static Context mContext;
    private List<Memories> mVideoList;

    public VideoGalleryAdapter(Context context, List<Memories> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    static int imageWidthPixel() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 30 / displayMetrics.density) / 3;
        return width;
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.gallery_gridview_item, null);
            ViewHolder holder = new ViewHolder(rowView, mContext);
            rowView.setTag(holder);
        }
        Video video = (Video) mVideoList.get(position);
        ViewHolder holder = (ViewHolder) rowView.getTag();
        if (!video.isChecked()) {
            holder.overlayImgView.setVisibility(View.GONE);
        } else {
            holder.overlayImgView.setVisibility(View.VISIBLE);
        }

        Glide.with(mContext).load(Uri.fromFile(new File(video.getLocalThumbPath()))).asBitmap().into(holder.imgView);
        holder.overlayImgView.setImageResource(R.drawable.img_selected);
        return rowView;
    }

    public static class ViewHolder {
        public ImageView imgView;
        public ImageView overlayImgView;

        public ViewHolder(View rowView, Context context) {
            imgView = (ImageView) rowView.findViewById(R.id.gridImg);
            overlayImgView = (ImageView) rowView.findViewById(R.id.overlayImg);

            imgView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidthPixel(),
                    imageWidthPixel()));
            overlayImgView.setLayoutParams(new RelativeLayout.LayoutParams(imageWidthPixel(),
                    imageWidthPixel()));
        }
    }

}
