package com.traveljar.memories.gallery.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.AudioDataSource;
import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.Journey;

import java.util.List;
import java.util.Map;

public class AudioAlbumsGalleryAdapter extends BaseAdapter {

    private static final String TAG = "GalleryAudioAdapter";

    static Context mContext;
    private Map<Journey, Audio> mAlbumsList;
    private List<Journey> mJourneyList;

    public AudioAlbumsGalleryAdapter(Context context, Map<Journey, Audio> mAlbumsList, List<Journey> mJourneyList) {
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
            convertView = inflater.inflate(R.layout.gallery_album_audio_grid_item, null);
        }
        ImageView img = (ImageView)convertView.findViewById(R.id.album_img);
        TextView journeyName = (TextView)convertView.findViewById(R.id.album_name);
        TextView noItems = (TextView)convertView.findViewById(R.id.no_items);
        RelativeLayout relLay = (RelativeLayout) convertView.findViewById(R.id.gallery_photos_rel_layout);
        Audio audio = mAlbumsList.get(mJourneyList.get(position));

        AbsListView.LayoutParams params = new AbsListView.LayoutParams(getImageWidth(), getImageWidth());
        relLay.setLayoutParams(params);
/*        relLay.getLayoutParams().width = getImageWidth();
        relLay.getLayoutParams().height = getImageWidth();
        relLay.requestLayout();*/

        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if(audio != null){
            noItems.setText(String.valueOf(AudioDataSource.getAudioCountOfJourney(mContext, audio.getjId())));
        }else {
            noItems.setText("0");
        }
        img.setImageResource(R.drawable.gallery_audio);
        journeyName.setText(mJourneyList.get(position).getName());

        return convertView;
    }

    private int getImageWidth() {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 15 / displayMetrics.density) / 2;
        return width;
    }

}
