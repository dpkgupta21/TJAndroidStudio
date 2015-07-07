package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Laps;

import java.util.ArrayList;
import java.util.List;

public class LapsDataSource {
    private static final String TAG = "LapsDataSource";

    public static long createLap(Laps laps, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER, laps.getIdOnServer());
        values.put(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID, laps.getJourneyId());
        values.put(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID, laps.getSourcePlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID, laps.getDestinationPlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_CONVEYANCE_MODE, laps.getConveyanceMode());
        values.put(MySQLiteHelper.LAPS_COLUMN_START_DATE, laps.getStartDate());

        long lapId = db.insert(MySQLiteHelper.TABLE_LAPS, null, values);
        Log.d(TAG, "New lap Inserted with id" + lapId);

        db.close();

        return lapId;
    }

    public static List<Laps> getLapsFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_LAPS + " WHERE " + MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID +
                " ='" + journeyId + "'";
        Cursor cursor = db.rawQuery(query, null);
        List<Laps> lapsList = getLapsFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList;
    }

    private static List<Laps> getLapsFromCursor(Cursor cursor) {
        List<Laps> lapsList = new ArrayList<>();
        Laps lap;
        if (cursor.moveToFirst()) {
            do {
                lap = new Laps();
                lap.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_ID)));
                lap.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER)));
                lap.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID)));
                lap.setSourcePlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID)));
                lap.setDestinationPlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID)));
                lap.setConveyanceMode(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_CONVEYANCE_MODE)));
                lap.setStartDate(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_START_DATE)));
                lapsList.add(lap);
            } while (cursor.moveToNext());
        }
        return lapsList;
    }

}
