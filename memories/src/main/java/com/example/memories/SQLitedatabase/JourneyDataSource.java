package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Journey;
import com.example.memories.utility.Constants;
import com.google.common.base.Joiner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JourneyDataSource {

    private static final String TAG = "<<JourneyDataSource>>";

    // ------------------------ "JOURNEY" table methods ----------------//

    /*
     * Creating a journey
     */
    public static long createJourney(Journey newJourney, Context context) {

        Log.d(TAG, "value of journey is " + newJourney.toString());

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER, newJourney.getIdOnServer());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_NAME, newJourney.getName());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE, newJourney.getTagLine());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_GROUPTYPE, newJourney.getGroupType());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY, newJourney.getCreatedBy());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS, (newJourney.getBuddies() == null) ? "" : Joiner.on(",").join(newJourney.getBuddies()));
        // values.put(MySQLiteHelper.JOURNEY_COLUMN_JOURNEY_LAPS,
        // newJourney.getLaps().toString());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_STATUS, newJourney.getJourneyStatus());

        // insert row
        long journey_id = db.insert(MySQLiteHelper.TABLE_JOURNEY, null, values);
        db.close();

        Log.d(TAG, "New Journey Inserted! with journey_id = " + journey_id + newJourney.getJourneyStatus());
        return journey_id;
    }

    public static Journey getJourneyById(Context context, String journeyId) {
        Log.d(TAG, "inside getJourneyById");
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE "
                + MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = '" + journeyId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Journey> journeyList = parseJourneysAsList(context, cursor);
        cursor.close();
        db.close();
        Log.d(TAG, "journey fetched successfully");
        return journeyList.get(0);
    }

    public static List<String> getBuddyIdsFromJourney(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String dbQuery = "SELECT " + MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS + "," + MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY + " FROM "
                + MySQLiteHelper.TABLE_JOURNEY + " WHERE "
                + MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId;
        Cursor cursor = db.rawQuery(dbQuery, null);

        List<String> buddyIds = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String buddies = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
            Log.d(TAG, "Buddies ids list is = " + buddies);
            buddyIds = (buddies.isEmpty() ? null : new ArrayList<>(Arrays.asList(buddies.split(","))));
        }

        cursor.close();
        db.close();
        return buddyIds;
    }

    public static List<Journey> getAllActiveJourneys(Context context) {

        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE " + MySQLiteHelper.JOURNEY_COLUMN_STATUS + " = '" + Constants.JOURNEY_STATUS_ACTIVE + "'";
        Log.d(TAG, "fetching journeys " + selectQuery);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Journey> journeyList = parseJourneysAsList(context, cursor);
        Log.d(TAG, "total active journeys fetched are " + journeyList.size());
        cursor.close();
        db.close();
        return journeyList;
    }

    public static Cursor getAllPastJourneys(Context context) {

        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        } else {
            Log.d(TAG, "no past journeys!!!");
        }
//        c.close();
        db.close();
        return c;
    }


    private static List<Journey> parseJourneysAsList(Context context, Cursor cursor) {
        List<Journey> journeyList = new ArrayList<Journey>();
        if (cursor.moveToFirst()) {
            Journey journey;
            while (!cursor.isAfterLast()) {
                journey = new Journey();
                journey.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER)));
                journey.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_NAME)));
                journey.setTagLine(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE)));
                journey.setGroupType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_GROUPTYPE)));
                journey.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY)));
                String buddyIds = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
                journey.setBuddies(buddyIds.isEmpty() ? new ArrayList<String>() : Arrays.asList(buddyIds.split(",")));
                Log.d(TAG, "buddy ids fetched from database are " + buddyIds);
                /*String laps = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_JOURNEY_LAPS));
                journey.setLaps(Arrays.asList(laps.split(",")));*/
                journey.setJourneyStatus(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_STATUS)));
                journeyList.add(journey);
                Log.d(TAG, "everything fine upto here 5");
                cursor.moveToNext();
                Log.d(TAG, "everything fine upto here 6");
            }
        }
        return journeyList;
    }


}