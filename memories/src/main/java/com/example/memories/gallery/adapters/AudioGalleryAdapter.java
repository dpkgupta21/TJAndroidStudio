package com.example.memories.gallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.memories.R;
import com.example.memories.models.Audio;

import java.util.List;

public class AudioGalleryAdapter extends BaseAdapter {

    private Context mContext;
    private List<Audio> mAudioList;

    public AudioGalleryAdapter(Context context, List<Audio> audioList) {
        mContext = context;
        mAudioList = audioList;
    }

    @Override
    public int getCount() {
        return mAudioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAudioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_audio_list_item, null);
        }
        return convertView;
    }
}
