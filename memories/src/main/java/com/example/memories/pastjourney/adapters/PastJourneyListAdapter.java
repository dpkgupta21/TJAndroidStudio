package com.example.memories.pastjourney.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.MySQLiteHelper;

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

        Log.d(TAG, "1");
        TextView journeyTitle = (TextView) view.findViewById(R.id.past_journey_title);
        Log.d(TAG, "2" + cursor.getColumnIndex("name"));
        journeyTitle.setText(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_NAME)));

        Log.d(TAG, "3");
        TextView journeyPlace = (TextView) view.findViewById(R.id.past_journey_place);
        journeyPlace.setText(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE)));

        Log.d(TAG, "3.1");
        TextView journeyDate = (TextView) view.findViewById(R.id.past_journey_date);
        journeyDate.setText(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY)));
        Log.d(TAG, "3.2");

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.d(TAG, "4");
        final View view = mInflater.inflate(R.layout.past_journey_list_item, parent, false);
        return view;
    }

}
