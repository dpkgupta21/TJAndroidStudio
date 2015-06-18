package com.example.memories.newjourney.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
            final ViewHolder viewHolder = new ViewHolder();
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
                    final PopupMenu popup = new PopupMenu(context, viewHolder.editBtn);
                    popup.getMenuInflater().inflate(R.menu.lap_card_dropdown_items, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.edit_lap:
                                    Intent i = new Intent(context, AddLap.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putExtra("EDIT_JOURNEY_POSITION", position);
                                    context.startActivity(i);
                                    break;
                                case R.id.remove_lap:
                                    lapsList.remove(position);
                                    LapsListAdapter.this.notifyDataSetChanged();
                                    break;
                            }
                            return false;
                        }
                    });
                }
            });
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        String f = lapsList.get(position).get("fromCity");
        String t = lapsList.get(position).get("toCity");
        String d = lapsList.get(position).get("date");
        String c = HelpMe.getConveyanceMode(Integer.parseInt(lapsList.get(position).get("conveyance")));
        holder.from.setText(f);
        holder.to.setText(t);
        holder.date.setText(HelpMe.getDate(Long.parseLong(d), 1));
        Log.d(TAG, d + "===" + HelpMe.getDate(Long.parseLong(d), 1));
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
