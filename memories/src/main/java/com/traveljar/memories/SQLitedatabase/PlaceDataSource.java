package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Place;

/**
 * Created by abhi on 05/06/15.
 */
public class PlaceDataSource {

    private static final String TAG = "<PlaceDataSource>";

    public static long createPlace(Place newPlace, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.PLACE_COLUMN_ID_ONSERVER, newPlace.getIdOnServer());
        values.put(MySQLiteHelper.PLACE_COLUMN_ID_ON_GOOGLE, newPlace.getIdOnGoogle());
        values.put(MySQLiteHelper.PLACE_COLUMN_COUNTRY, newPlace.getCountry());
        values.put(MySQLiteHelper.PLACE_COLUMN_STATE, newPlace.getState());
        values.put(MySQLiteHelper.PLACE_COLUMN_CITY, newPlace.getCity());
        values.put(MySQLiteHelper.PLACE_COLUMN_CREATED_BY, newPlace.getCreatedBy());
        values.put(MySQLiteHelper.PLACE_COLUMN_CREATED_AT, newPlace.getCreatedAt());


        long place_id = db.insert(MySQLiteHelper.TABLE_PLACE, null, values);
        Log.d(TAG, "New place Inserted!");

        db.close();

        return place_id;
    }
}
