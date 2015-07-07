package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Place;

import java.util.ArrayList;
import java.util.List;

public class PlaceDataSource {

    private static final String TAG = "<PlaceDataSource>";

    public static long createPlace(Place newPlace, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.PLACE_COLUMN_ID_ONSERVER, newPlace.getIdOnServer());
        values.put(MySQLiteHelper.PLACE_COLUMN_COUNTRY, newPlace.getCountry());
        values.put(MySQLiteHelper.PLACE_COLUMN_STATE, newPlace.getState());
        values.put(MySQLiteHelper.PLACE_COLUMN_CITY, newPlace.getCity());
        values.put(MySQLiteHelper.PLACE_COLUMN_CREATED_AT, newPlace.getCreatedAt());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LATITUDE, newPlace.getLatitude());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LONGITUDE, newPlace.getLongitude());


        long place_id = db.insert(MySQLiteHelper.TABLE_PLACE, null, values);
        Log.d(TAG, "New place Inserted!");

        db.close();

        return place_id;
    }

    public static Place getPlaceById(Context context, String placeId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PLACE + " WHERE "
                + MySQLiteHelper.PLACE_COLUMN_ID + " = '" + placeId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Place> placeList = parsePlacesAsList(context, cursor);
        cursor.close();
        db.close();
        Log.d(TAG, "journey fetched successfully");
        return placeList.get(0);
    }

    public static List<Place> parsePlacesAsList(Context context, Cursor cursor){
        List<Place> placeList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            Place place;
            while (!cursor.isAfterLast()) {
                place = new Place();
                place.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_ID)));
                place.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_ID_ONSERVER)));
                place.setCountry(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_COUNTRY)));
                place.setState(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_STATE)));
                place.setCity(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_CITY)));
                place.setCreatedAt(Long.parseLong(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_CREATED_AT))));
                place.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_LATITUDE)));
                place.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PLACE_COLUMN_LONGITUDE)));
                placeList.add(place);
                cursor.moveToNext();
            }
        }
        return placeList;
    }

}
