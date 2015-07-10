package com.traveljar.memories.pastjourney.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.SQLitedatabase.MySQLiteHelper;
import com.traveljar.memories.SQLitedatabase.PlaceDataSource;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.models.Place;

import java.util.Arrays;
import java.util.List;

public class PastJourneyListAdapter extends CursorAdapter {
    private static final String TAG = "<PastJourneyListAdapt>";
    private final LayoutInflater mInflater;
    private Cursor cursor;

    @SuppressWarnings("deprecation")
    public PastJourneyListAdapter(Context context, Cursor c) {
        super(context, c);
        cursor = c;
        mInflater = LayoutInflater.from(context);
        Log.d(TAG, "6" + c.getColumnCount() + c.getCount());
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView journeyTitle = (TextView) view.findViewById(R.id.past_journey_title);
        TextView journeyPlace = (TextView) view.findViewById(R.id.past_journey_place);
        TextView journeyTagline = (TextView) view.findViewById(R.id.past_journey_tagline);
        TextView buddyCount = (TextView) view.findViewById(R.id.past_journey_buddy_count);

        journeyTitle.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_NAME)));
        journeyTagline.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE)));

        String buddyIds = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
        int size = buddyIds.isEmpty() ? 0 : Arrays.asList(buddyIds.split(",")).size();
        buddyCount.setText(String.valueOf(size + 1) + " people");

        String place = "";
        List<Laps> lapsList = LapsDataSource.getLapsFromJourney(context, cursor.getString(cursor.getColumnIndex
                (MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER)));
        if(lapsList.size() > 0){
            Place destinationCity = PlaceDataSource.getPlaceById(context, lapsList.get(0).getDestinationPlaceId());
            place = destinationCity.getCity();
        }
        journeyPlace.setText(place);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "4");
        final View view = mInflater.inflate(R.layout.past_journey_list_item, parent, false);
        return view;
    }

}
