package com.traveljar.memories.currentjourney.adapters;

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

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.LapDataSource;
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.newjourney.AddLap;
import com.traveljar.memories.utility.HelpMe;

import java.util.List;

public class LapsAdapter extends ArrayAdapter<Laps> {
    private static final String TAG = "[LapsListAdapter]";
    private final Activity context;
    private List<Laps> lapsList;

    public LapsAdapter(Activity context, List<Laps> lapsList) {
        super(context, R.layout.new_journey_laps_list, lapsList);
        Log.d(TAG, "constructor");
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
            viewHolder.conveyance = (TextView) rowView.findViewById(R.id.new_journey_location_lap_conveyance);
            viewHolder.editBtn = (ImageButton) rowView.findViewById(R.id.edit_journey_lap);
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
                                    i.putExtra("isJourneyCreated", true);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    i.putExtra("EDIT_LAP_ID", lapsList.get(position).getId());
                                    i.putExtra("isJourneyCreated", true);
                                    context.startActivity(i);
                                    break;
                                case R.id.remove_lap:
                                    //LapDataSource.deleteLap(context, lapsList.get(position).getId());
                                    LapsDataSource.deleteLaps(context, lapsList.get(position).getId());
                                    lapsList.remove(position);
                                    LapsAdapter.this.notifyDataSetChanged();
                                    (context).invalidateOptionsMenu();
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
        String from = lapsList.get(position).getSourceCityName();
        String to = lapsList.get(position).getDestinationCityName();
        long date = lapsList.get(position).getStartDate();

        String c = HelpMe.getConveyanceMode(lapsList.get(position).getConveyanceMode());
        holder.from.setText(from);
        holder.to.setText(to);
        holder.date.setText(HelpMe.getDate(date, 1));
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
