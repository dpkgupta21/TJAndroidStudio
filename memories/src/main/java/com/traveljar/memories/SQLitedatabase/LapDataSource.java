package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Lap;

import java.util.ArrayList;
import java.util.List;

public class LapDataSource {

    private static final String TAG = "LapDataSource";

    public static long createLap(Lap lap, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAP_COLUMN_ID_ON_SERVER, lap.getIdOnServer());
        values.put(MySQLiteHelper.LAP_COLUMN_JOURNEY_ID, lap.getJourneyId());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_CITY, lap.getSourceCityName());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_STATE, lap.getSourceStateName());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_COUNTRY, lap.getSourceCountryName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_CITY, lap.getDestinationCityName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_STATE, lap.getDestinationStateName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_COUNTRY, lap.getDestinationCountryName());
        values.put(MySQLiteHelper.LAP_COLUMN_CONVEYANCE_MODE, lap.getConveyanceMode());
        values.put(MySQLiteHelper.LAP_COLUMN_START_DATE, lap.getStartDate());

        long lapId = db.insert(MySQLiteHelper.TABLE_LAP, null, values);
        Log.d(TAG, "New lap Inserted with id" + lapId + lap);

        db.close();

        return lapId;
    }

    public static List<Lap> getAllLaps(Context context){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_LAP;
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "length of cursor is " + cursor.getCount());
        List<Lap> lapsList = getLapsFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList;
    }

    public static Lap getLapById(String id, Context context) {
        Log.d(TAG, "fetching lap item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_LAP, null,
                MySQLiteHelper.LAP_COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Lap> lapsList = getLapsFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList.get(0);
    }

    /*get all the laps associated to a particular journey*/
    public static List<Lap> getLapFromJourney(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();

        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_LAP + " WHERE " + MySQLiteHelper.LAP_COLUMN_JOURNEY_ID + " ='" +
                journeyId + "'";
        Cursor cursor = db.rawQuery(query, null);
        List<Lap> lapsList = getLapsFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList;
    }

    /*Updates a list of laps*/
    public static void updateLapsList(List<Lap> lapsList, Context context){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        for(Lap lap : lapsList){
            Log.d(TAG, lap.getId() + lap.getJourneyId() + lap.getSourceCityName());
            values.put(MySQLiteHelper.LAP_COLUMN_ID, lap.getId());
            values.put(MySQLiteHelper.LAP_COLUMN_ID_ON_SERVER, lap.getIdOnServer());
            values.put(MySQLiteHelper.LAP_COLUMN_JOURNEY_ID, lap.getJourneyId());
            values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_CITY, lap.getSourceCityName());
            values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_STATE, lap.getSourceStateName());
            values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_COUNTRY, lap.getSourceCountryName());
            values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_CITY, lap.getDestinationCityName());
            values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_STATE, lap.getDestinationStateName());
            values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_COUNTRY, lap.getDestinationCountryName());
            values.put(MySQLiteHelper.LAP_COLUMN_CONVEYANCE_MODE, lap.getConveyanceMode());
            values.put(MySQLiteHelper.LAP_COLUMN_START_DATE, lap.getStartDate());
            db.update(MySQLiteHelper.TABLE_LAP, values, MySQLiteHelper.LAP_COLUMN_ID + " = '" + lap.getId() + "'", null);
            Log.d(TAG, "lap updated" + values + " lap id = " + lap.getId());
        }
        db.close();
    }

    /*Deletes a list of laps*/
    public static void deleteLapsList(Context context, List<Lap> lapsList){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        for(Lap lap : lapsList) {
            db.delete(MySQLiteHelper.TABLE_LAP, MySQLiteHelper.LAP_COLUMN_ID + "=?", new String[]{lap.getId()});
        }
        db.close();
    }

    /*Deletes a single lap*/
    public static void deleteLap(Context context, String lapId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LAP, MySQLiteHelper.LAP_COLUMN_ID + "=?", new String[]{lapId});
        db.close();
    }

    public static void updateLap(Lap lap, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAP_COLUMN_ID_ON_SERVER, lap.getIdOnServer());
        values.put(MySQLiteHelper.LAP_COLUMN_JOURNEY_ID, lap.getJourneyId());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_CITY, lap.getSourceCityName());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_STATE, lap.getSourceStateName());
        values.put(MySQLiteHelper.LAP_COLUMN_SOURCE_COUNTRY, lap.getSourceCountryName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_CITY, lap.getDestinationCityName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_STATE, lap.getDestinationStateName());
        values.put(MySQLiteHelper.LAP_COLUMN_DESTINATION_COUNTRY, lap.getDestinationCountryName());
        values.put(MySQLiteHelper.LAP_COLUMN_CONVEYANCE_MODE, lap.getConveyanceMode());
        values.put(MySQLiteHelper.LAP_COLUMN_START_DATE, lap.getStartDate());

        db.update(MySQLiteHelper.TABLE_LAP, values, MySQLiteHelper.LAP_COLUMN_ID + " = '" + lap.getId() + "'", null);
        db.close();

    }

    private static List<Lap> getLapsFromCursor(Cursor cursor) {
        List<Lap> lapsList = new ArrayList<>();
        Lap lap;
        if (cursor.moveToFirst()) {
            do {
                lap = new Lap();
                lap.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_ID)));
                lap.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_ID_ON_SERVER)));
                lap.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_JOURNEY_ID)));
                lap.setSourceCityName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_SOURCE_CITY)));
                lap.setSourceStateName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_SOURCE_STATE)));
                lap.setSourceCountryName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_SOURCE_COUNTRY)));
                lap.setDestinationCityName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_DESTINATION_CITY)));
                lap.setDestinationStateName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_DESTINATION_STATE)));
                lap.setDestinationCountryName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_DESTINATION_COUNTRY)));
                lap.setConveyanceMode(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_CONVEYANCE_MODE)));
                lap.setStartDate(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.LAP_COLUMN_START_DATE)));
                lapsList.add(lap);
            } while (cursor.moveToNext());
        }
        return lapsList;
    }

}
