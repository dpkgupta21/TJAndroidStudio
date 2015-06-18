package com.example.memories.gallery.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.memories.R;
import com.example.memories.models.Picture;
import com.example.memories.utility.LoadScaledBitmapFromPath;

import java.util.List;

public class ImageGalleryAdapter extends BaseAdapter {
    private static final String TAG = "GalleryImageAdapter";

    static Context mContext;
    private List<Picture> mImgList;

    public ImageGalleryAdapter(Context context, List<Picture> imgList) {
        mContext = context;
        mImgList = imgList;
    }

    static int imageWidthPixel() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 30 / displayMetrics.density) / 3;
        return width;
    }

    @Override
    public int getCount() {
        return mImgList.size();
    }

    @Override
    public Object getItem(int position) {
        return mImgList.get(position);
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
        ViewHolder holder = (ViewHolder) rowView.getTag();
        if (!mImgList.get(position).isChecked()) {
            holder.overlayImgView.setVisibility(View.GONE);
        } else {
            holder.overlayImgView.setVisibility(View.VISIBLE);
        }

        LoadScaledBitmapFromPath.loadBitmap(mImgList.get(position).getPicThumbnailPath(), holder.imgView, 150, 150, mContext);

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
