package com.traveljar.memories.gallery.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Note;

import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 2/7/15.
 */
public class NotesAlbumsGalleryAdapter extends BaseAdapter {

    private static final String TAG = "GalleryNoteAdapter";

    static Context mContext;
    private Map<Journey, Note> mAlbumsList;
    private List<Journey> mJourneyList;

    public NotesAlbumsGalleryAdapter(Context context, Map<Journey, Note> mAlbumsList, List<Journey> mJourneyList) {
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
            convertView = inflater.inflate(R.layout.gallery_album_grid_item, null);
        }
        ImageView img = (ImageView)convertView.findViewById(R.id.album_img);
        TextView journeyName = (TextView)convertView.findViewById(R.id.album_name);
        Bitmap bitmap;
        Note note = mAlbumsList.get(mJourneyList.get(position));

        img.setLayoutParams(new RelativeLayout.LayoutParams(getImageWidth(), getImageWidth()));
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(note != null){
            img.setImageResource(R.drawable.ic_play);
        }else {
            img.setImageResource(R.drawable.gumnaam_profile_image);
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
