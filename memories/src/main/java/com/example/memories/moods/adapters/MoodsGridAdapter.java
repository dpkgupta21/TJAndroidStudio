package com.example.memories.moods.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memories.R;

import java.util.List;
import java.util.Map;

public class MoodsGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<Map<String, String>> emoticonIds;

    public MoodsGridAdapter(Context mContext, List<Map<String, String>> emoticonIds) {
        super();
        this.mContext = mContext;
        this.emoticonIds = emoticonIds;
    }

    @Override
    public int getCount() {
        return emoticonIds.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mood_grid_item, null);
            ImageView emoticon = (ImageView) convertView.findViewById(R.id.emoticon);
            TextView emoticonTitle = (TextView) convertView.findViewById(R.id.emoticonTitle);

            emoticon.setImageResource(Integer.parseInt(emoticonIds.get(position).get("id")));
            emoticonTitle.setText(emoticonIds.get(position).get("name"));
        }
        return convertView;
    }


}
