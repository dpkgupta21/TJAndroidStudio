package com.traveljar.memories.newjourney.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.traveljar.memories.R;

import java.util.List;

public class LocationListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "[LocationListAdapter]";
    private final Activity context;
    private List<String> names;

    public LocationListAdapter(Activity context, List<String> locationList) {
        super(context, R.layout.new_journey_location_list_item, locationList);
        Log.d(TAG, "construcor");
        this.context = context;
        this.names = locationList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.new_journey_location_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) rowView.findViewById(R.id.new_journey_location_list_name);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String n = names.get(position);
        holder.name.setText(n);
        Log.d(TAG, "5");
        return rowView;
    }

    static class ViewHolder {
        public TextView name;
    }
}
