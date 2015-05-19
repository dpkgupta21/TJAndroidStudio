package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Journey;

public class JourneyDataSource {

    private static final String TAG = "<<JourneyDataSource>>";

    // ------------------------ "JOURNEY" table methods ----------------//

    /*
     * Creating a journey
     */
    public static long createJourney(Journey newJourney, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER, newJourney.getIdOnServer());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_NAME, newJourney.getName());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE, newJourney.getTagLine());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_GROUPTYPE, newJourney.getGroupType());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY, newJourney.getCreatedBy());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS, newJourney.getBuddies().toString());
        // values.put(MySQLiteHelper.JOURNEY_COLUMN_JOURNEY_LAPS,
        // newJourney.getLaps().toString());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_ISACTIVE, newJourney.isActive());

        // insert row
        long journey_id = db.insert(MySQLiteHelper.TABLE_JOURNEY, null, values);
        db.close();

        Log.d(TAG, "New Journey Inserted! with journey_id = " + journey_id);
        return journey_id;
    }

    public static String getCurrentJourney(Context mContext) {
        String currentJourneyId;
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE "
                + MySQLiteHelper.JOURNEY_COLUMN_ISACTIVE + " = 1";
        SQLiteDatabase db = MySQLiteHelper.getInstance(mContext).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
            currentJourneyId = c.getString(c
                    .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER));
        } else {
            Log.d(TAG, "no past journeys!!!");
            currentJourneyId = null;
        }

        c.close();
        db.close();
        return currentJourneyId;

    }

    /**
     * getting all journeys
     */
    public static Cursor getAllJourneys(Context context) {

        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        } else {
            Log.d(TAG, "no past journeys!!!");
        }
        c.close();
        db.close();
        return c;
    }

    public static String[] getContactsFromJourney(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String dbQuery = "SELECT " + MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS + " FROM "
                + MySQLiteHelper.TABLE_JOURNEY + " WHERE "
                + MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId;
        Cursor cursor = db.rawQuery(dbQuery, null);
        String[] buddyIds = null;
        if (cursor.moveToFirst()) {
            String buddies = cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
            buddies = buddies.replace("[", "");
            buddies = buddies.replace("]", "");
            Log.d(TAG,
                    "buddies List "
                            + cursor.getString(cursor
                            .getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS)));
            buddyIds = buddies.split(",");
        }
        cursor.close();
        db.close();
        return buddyIds;
    }
}