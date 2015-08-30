package com.traveljar.memories.gallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.PictureDataSource;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Picture;

import java.io.File;
import java.util.List;
import java.util.Map;

public class PictureAlbumsGalleryAdapter extends BaseAdapter{
    private static final String TAG = "GalleryImageAdapter";

    static Context mContext;
    private Map<Journey, Picture> mAlbumsList;
    private List<Journey> mJourneyList;

    public PictureAlbumsGalleryAdapter(Context context, Map<Journey, Picture> mAlbumsList, List<Journey> mJourneyList) {
        mContext = context;
        this.mAlbumsList = mAlbumsList;
        this.mJourneyList = mJourneyList;
    }

    @Override
    public int getCount() {
        return mAlbumsList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlbumsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_album_grid_item, parent, false);
        }
        ImageView img = (ImageView)convertView.findViewById(R.id.album_img);
        RelativeLayout relLay = (RelativeLayout) convertView.findViewById(R.id.gallery_photos_rel_layout);
        TextView journeyName = (TextView)convertView.findViewById(R.id.album_name);
        TextView noItems = (TextView)convertView.findViewById(R.id.no_items);

        Bitmap bitmap;
        Picture picture = mAlbumsList.get(mJourneyList.get(position));

        relLay.getLayoutParams().width = getImageWidth();
        relLay.getLayoutParams().height = getImageWidth();
        relLay.requestLayout();
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(picture != null){
            Glide.with(mContext).load(Uri.fromFile(new File(picture.getPicThumbnailPath()))).asBitmap().into(img);
            noItems.setText(String.valueOf(PictureDataSource.getPicCountOfJourney(mContext, picture.getjId())));
        }else {
            img.setImageResource(R.drawable.gumnaam_profile_image);
            noItems.setText("0");
        }
        journeyName.setText(mJourneyList.get(position).getName());

        return convertView;
    }

    private int getImageWidth() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 15 / displayMetrics.density) / 2;
        return width;
    }

}
