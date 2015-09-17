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

    public static List<Laps> getLapsFromJourney(Context context, String journeyId) {
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

    private static List<Laps> getLapsDetailWithPlaceFromCursor(Cursor cursor) {
        List<Laps> lapsList = new ArrayList<Laps>();
        try {
            Laps laps = null;
            if (cursor.moveToFirst()) {
                do {
                    laps = new Laps();
                    laps.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_ID)));
                    laps.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER)));
                    laps.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID)));
                    laps.setSourceCityName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_CITY)));
                    laps.setSourceStateName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_STATE)));
                    laps.setSourceCountryName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_COUNTRY)));
                    laps.setDestinationCityName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_CITY)));
                    laps.setDestinationStateName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_STATE)));
                    laps.setDestinationCountryName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_COUNTRY)));
                    laps.setConveyanceMode(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_CONVEYANCE_MODE)));
                    laps.setStartDate(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_START_DATE)));
                    laps.setSourcePlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID)));
                    laps.setDestinationPlaceId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID)));
                    lapsList.add(laps);
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lapsList;
    }

    public static List<Laps> getLapsFromJourneyWithPlace(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT LP.id as id,LP.journeyId as journeyId," +
                " LP.idOnServer as idOnServer," +
                " LP.conveyanceMode as conveyanceMode," +
                " LP.startDate as startDate, " +
                " S.city as sourceCity," +
                " S.state as sourceState," +
                " S.country as sourceCountry," +
                " D.city as destinationCity," +
                " D.state as destinationState," +
                " D.country as destinationCountry," +
                " LP.sourcePlaceId as  sourcePlaceId," +
                " LP.destinationPlaceId as destinationPlaceId" +
                " FROM LAPS LP " +
                " left join PLACE S ON S._id = LP.sourcePlaceId " +
                " left join PLACE D ON D._id = LP.destinationPlaceId " +
                " WHERE LP.journeyId ='" + journeyId + "'";

//        String query = "SELECT LP.id as id,LP.journeyId as journeyId, LP.idOnServer as idOnServer, " +
//                " LP.conveyanceMode as conveyanceMode, LP.startDate as startDate,  S.city as sourceCity, " +
//                "S.state as sourceState, S.country as sourceCountry, " +
//                "D.city as destinationCity, D.state as destinationState, D.country as destinationCountry, " +
//                "LP.sourcePlaceId as sourcePlaceId,LP.destinationPlaceId as destinationPlaceId FROM LAPS LP " +
//                "left join PLACE S ON S._id = LP.sourcePlaceId  left join PLACE D ON D._id = LP.destinationPlaceId " +
//                "Where  LP.sourcePlaceId <>'' AND LP.destinationPlaceId <>'' AND LP.journeyId ='" + journeyId + "' " +
//                "UNION " +
//                "SELECT LP.id as id,LP.journeyId as journeyId, LP.idOnServer as idOnServer, " +
//                "LP.conveyanceMode as conveyanceMode, LP.startDate as startDate, " +
//                "LP.sourceCity as sourceCity, LP.sourceState as sourceState, LP.sourceCountry as sourceCountry, " +
//                "LP.destinationCity as destinationCity, LP.destinationState as destinationState, " +
//                "LP.destinationCountry as destinationCountry, " +
//                "LP.sourcePlaceId as sourcePlaceId, LP.destinationPlaceId as destinationPlaceId " +
//                "FROM LAPS  LP Where  LP.sourcePlaceId ='' AND LP.destinationPlaceId ='' AND " +
//                "LP.journeyId ='" + journeyId + "' ";

        Cursor cursor = db.rawQuery(query, null);
        List<Laps> lapsList = getLapsDetailWithPlaceFromCursor(cursor);
        cursor.close();
        db.close();
        return lapsList;
    }

    public static Laps getLapsByIdWithPlace(Context context, String id) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT LP.id as id," +
                " LP.journeyId as journeyId," +
                " LP.idOnServer as idOnServer," +
                " LP.conveyanceMode as conveyanceMode," +
                " LP.startDate as startDate, " +
                " S.city as sourceCity," +
                " S.state as sourceState," +
                " S.country as sourceCountry," +
                " D.city as destinationCity," +
                " D.state as destinationState," +
                " D.country as destinationCountry," +
                " LP.sourcePlaceId as sourcePlaceId," +
                " LP.destinationPlaceId as destinationPlaceId " +
                " FROM LAPS LP " +
                " left join PLACE S ON S._id = LP.sourcePlaceId " +
                " left join PLACE D ON D._id = LP.destinationPlaceId " +
                " WHERE LP.id ='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        List<Laps> lapsList = getLapsDetailWithPlaceFromCursor(cursor);
        return lapsList.get(0);
    }


//    public static Lap getLapByIdWithPlace(Context context, String id) {
//        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
//        String query = "SELECT LP.id as id,LP.journeyId as journeyId, LP.idOnServer as idOnServer, LP.conveyanceMode as conveyanceMode, LP.startDate as startDate, " +
//                " S.city as sourceCity, S.state as sourceState, S.country as sourceCountry, D.city as destinationCity," +
//                " D.state as destinationState, D.country as destinationCountry,LP.sourcePlaceId as sourcePlaceId,LP.destinationPlaceId as destinationPlaceId FROM LAPS LP " +
//                " left join PLACE S ON S._id = LP.sourcePlaceId " +
//                " left join PLACE D ON D._id = LP.destinationPlaceId " +
//                " WHERE LP.id ='" + id + "'";
//        Cursor cursor = db.rawQuery(query, null);
//        List<Lap> lapsList = getLapWithPlaceFromCursor(cursor);
//        return lapsList.get(0);
//    }

    /*Deletes a single lap*/
    public static void deleteLaps(Context context, String lapsId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LAPS, MySQLiteHelper.LAPS_COLUMN_ID + "=?", new String[]{lapsId});
        db.close();
    }

    public static void updateLaps(Laps laps, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER, laps.getIdOnServer());
        values.put(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID, laps.getJourneyId());
        values.put(MySQLiteHelper.LAPS_COLUMN_SOURCE_PLACE_ID, laps.getSourcePlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_DESTINATION_PLACE_ID, laps.getDestinationPlaceId());
        values.put(MySQLiteHelper.LAPS_COLUMN_CONVEYANCE_MODE, laps.getConveyanceMode());
        values.put(MySQLiteHelper.LAPS_COLUMN_START_DATE, laps.getStartDate());

        db.update(MySQLiteHelper.TABLE_LAPS, values, MySQLiteHelper.LAPS_COLUMN_ID + " = '" + laps.getId() + "'", null);
        db.close();

    }

    /*
     *  Here we are updating Laps server id and Journey Id
     */
    public static void updateLapsServerId(Laps laps, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LAPS_COLUMN_ID_ON_SERVER, laps.getIdOnServer());
        values.put(MySQLiteHelper.LAPS_COLUMN_JOURNEY_ID, laps.getJourneyId());
        int flag = db.update(MySQLiteHelper.TABLE_LAPS, values, MySQLiteHelper.LAPS_COLUMN_ID + " = '" + laps.getId() + "'", null);

        db.close();

    }
}
