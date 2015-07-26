package com.traveljar.memories.currentjourney.adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.traveljar.memories.R;

import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 26/7/15.
 */
public class StatisticsAdapter extends BaseAdapter{


    private List<Map<String, String>> statistics;
    private Context context;

    public StatisticsAdapter(Context context,List<Map<String, String>> statistics){
        this.statistics = statistics;
        this.context = context;
    }

    @Override
    public int getCount() {
        return statistics.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.statictics_item, parent, false);
        }
        convertView.findViewById(R.id.statistic_item_layout).setLayoutParams(new
                AbsListView.LayoutParams(imageWidthPixel(), imageWidthPixel()));
        /*convertView.setLayoutParams(new LinearLayout.LayoutParams(imageWidthPixel(),
                imageWidthPixel()));*/
        ImageView icon = (ImageView)convertView.findViewById(R.id.stat_icon);
        TextView statName = (TextView)convertView.findViewById(R.id.stat_name);
        TextView statCount = (TextView)convertView.findViewById(R.id.stat_count);
        icon.setImageResource(Integer.parseInt(statistics.get(position).get("resource_id")));;
        statName.setText(statistics.get(position).get("title"));
        statCount.setText(statistics.get(position).get("count"));
        return convertView;
    }

    private int imageWidthPixel() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = (int) (displayMetrics.widthPixels - 34 / displayMetrics.density) / 2;
        return width;
    }
}
