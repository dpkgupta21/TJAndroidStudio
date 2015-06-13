package com.example.memories.newjourney.adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.newjourney.AddLap;
import com.example.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LapsListAdapter extends ArrayAdapter<Map<String, String>> {
    private static final String TAG = "[LapsListAdapter]";
    private final Activity context;
    private List<Map<String, String>> lapsList;

    public LapsListAdapter(Activity context, ArrayList<Map<String, String>> lapsList) {
        super(context, R.layout.new_journey_laps_list, lapsList);
        Log.d(TAG, "construcor");
        this.context = context;
        this.lapsList = lapsList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.new_journey_laps_list_item, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.from = (TextView) rowView.findViewById(R.id.new_journey_location_lap_from);
            viewHolder.to = (TextView) rowView.findViewById(R.id.new_journey_location_lap_to);
            viewHolder.date = (TextView) rowView.findViewById(R.id.new_journey_location_lap_date);
            viewHolder.conveyance = (TextView) rowView
                    .findViewById(R.id.new_journey_location_lap_conveyance);
            viewHolder.editBtn = (ImageButton) rowView
                    .findViewById(R.id.edit_journey_lap);
            viewHolder.editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AddLap.class);
                    i.putExtra("EDIT_JOURNEY_POSITION", position);
                    context.startActivity(i);
                }
            });
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String f = lapsList.get(position).get("from");
        String t = lapsList.get(position).get("to");
        String d = lapsList.get(position).get("date");
        String c = HelpMe.getConveyanceMode(Integer.parseInt(lapsList.get(position).get("conveyance")));
        holder.from.setText(f);
        holder.to.setText(t);
        holder.date.setText(d);
        holder.conveyance.setText(c);
        return rowView;
    }

    static class ViewHolder {
        public TextView from;
        public TextView to;
        public TextView date;
        public TextView conveyance;
        public ImageButton editBtn;
    }
}
