package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Lap;
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

    private static List<Lap> getLapWithPlaceFromCursor(Cursor cursor) {
        List<Lap> lapsList = new ArrayList<Lap>();
        try {
            Lap lap = null;
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
                    lap.setSourcePlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID)));
                    lap.setDestinationPlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID)));
                    lapsList.add(lap);
                } while (cursor.moveToNext());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return lapsList;
    }

    public static List<Lap> getLapsFromJourneyWithPlace(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT LP.id as id,LP.journeyId as journeyId, LP.idOnServer as idOnServer, LP.conveyanceMode as conveyanceMode, LP.startDate as startDate, " +
                " S.city as sourceCity, S.state as sourceState, S.country as sourceCountry, D.city as destinationCity," +
                " D.state as destinationState, D.country as destinationCountry,LP.sourcePlaceId as sourcePlaceId,LP.destinationPlaceId as destinationPlaceId FROM LAPS LP " +
                " left join PLACE S ON S._id = LP.sourcePlaceId " +
                " left join PLACE D ON D._id = LP.destinationPlaceId " +
                " WHERE LP.journeyId ='"+journeyId+"'";
        Cursor cursor = db.rawQuery(query, null);
        List<Lap> lapsList = getLapWithPlaceFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList;
    }

    public static Lap getLapByIdWithPlace(Context context, String id){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT LP.id as id,LP.journeyId as journeyId, LP.idOnServer as idOnServer, LP.conveyanceMode as conveyanceMode, LP.startDate as startDate, " +
                " S.city as sourceCity, S.state as sourceState, S.country as sourceCountry, D.city as destinationCity," +
                " D.state as destinationState, D.country as destinationCountry,LP.sourcePlaceId as sourcePlaceId,LP.destinationPlaceId as destinationPlaceId FROM LAPS LP " +
                " left join PLACE S ON S._id = LP.sourcePlaceId " +
                " left join PLACE D ON D._id = LP.destinationPlaceId " +
                " WHERE LP.id ='"+id+"'";
        Cursor cursor = db.rawQuery(query, null);
        List<Lap> lapsList = getLapWithPlaceFromCursor(cursor);
        return lapsList.get(0);
    }

    /*Deletes a single lap*/
    public static void deleteLap(Context context, String lapId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LAPS, MySQLiteHelper.LAP_COLUMN_ID + "=?", new String[]{lapId});
        db.close();
    }

    public static void updateLap(Lap lap, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER, lap.getIdOnServer());
        values.put(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID, lap.getJourneyId());
        values.put(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID, lap.getSourcePlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID, lap.getDestinationPlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_CONVEYANCE_MODE, lap.getConveyanceMode());
        values.put(MySQLiteHelper.LAPS_COLUMN_START_DATE, lap.getStartDate());

        db.update(MySQLiteHelper.TABLE_LAPS, values, MySQLiteHelper.LAP_COLUMN_ID + " = '" + lap.getId() + "'", null);
        db.close();

    }
}
