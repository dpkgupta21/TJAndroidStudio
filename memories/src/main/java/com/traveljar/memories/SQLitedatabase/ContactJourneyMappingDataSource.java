package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;

public class ContactJourneyMappingDataSource {

    public static void addMapping(Context context, String contactId, String journeyId, boolean status){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID, journeyId);
        values.put(MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID, contactId);
        values.put(MySQLiteHelper.MAPPING_COLUMN_IS_USER_ACTIVE, status ? 1 : 0);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        try {
            db.insertOrThrow(MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP, null, values);
        }catch (SQLiteConstraintException ex){
            // Primary key constraint not followed, means entry already present, hence update the row
            db.update(MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP, values, MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID + " = '" + contactId
                    + "' AND " + MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID + " = '" + journeyId + "'", null);
        }
        db.close();
    }

}
