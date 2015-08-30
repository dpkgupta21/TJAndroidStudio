package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Joiner;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.utility.Constants;

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

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER, newJourney.getIdOnServer());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_NAME, newJourney.getName());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE, newJourney.getTagLine());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_GROUPTYPE, newJourney.getGroupType());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY, newJourney.getCreatedBy());
        String buddyIds = newJourney.getBuddies() == null ? "" : Joiner.on(",").join(newJourney.getBuddies());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS, (buddyIds));
        values.put(MySQLiteHelper.JOURNEY_COLUMN_STATUS, newJourney.getJourneyStatus());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_CREATED_AT, newJourney.getCreatedAt());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_UPDATED_AT, newJourney.getUpdatedAt());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_COMPELTED_AT, newJourney.getCompletedAt());
        values.put(MySQLiteHelper.JOURNEY_COLUMN_IS_USER_ACTIVE, newJourney.isUserActive() ? 1 : 0);

        // insert row
        long journey_id = db.insert(MySQLiteHelper.TABLE_JOURNEY, null, values);

        // Insert journey-contact mapping in the mapping table
        if(newJourney.getBuddies() != null) {
            for (String contact : newJourney.getBuddies()) {
                values = new ContentValues();
                values.put(MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID, newJourney.getIdOnServer());
                values.put(MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID, contact);
                values.put(MySQLiteHelper.MAPPING_COLUMN_IS_USER_ACTIVE, newJourney.isUserActive() ? 1 : 0);
                Log.d(TAG, "adding mapping" + values);
                db.insert(MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP, null, values);
            }
        }
        db.close();

        Log.d(TAG, "New Journey Inserted! with idOnServer = " + newJourney.getIdOnServer() + newJourney.getJourneyStatus());
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
        return journeyList.get(0);
    }

    public static List<String> getBuddyIdsFromJourney(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String dbQuery = "SELECT " + MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS + " FROM "
                + MySQLiteHelper.TABLE_JOURNEY + " WHERE "
                + MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId;
        Cursor cursor = db.rawQuery(dbQuery, null);

        List<String> buddyIds = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String buddies = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
            if (!buddies.isEmpty()) {
                buddyIds = Arrays.asList(buddies.split(","));
            }
        }

        cursor.close();
        db.close();
        return buddyIds;
    }

    public static List<Journey> getAllActiveJourneys(Context context) {

        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE " + MySQLiteHelper.JOURNEY_COLUMN_STATUS + " = '" + Constants.JOURNEY_STATUS_ACTIVE + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Journey> journeyList = parseJourneysAsList(context, cursor);
        cursor.close();
        db.close();
        return journeyList;
    }

    public static int getActiveJourneysCount(Context context){
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE " + MySQLiteHelper.JOURNEY_COLUMN_STATUS + " = '" + Constants.JOURNEY_STATUS_ACTIVE + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public static Cursor getAllPastJourneys(Context context) {

        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE " + MySQLiteHelper.JOURNEY_COLUMN_STATUS + " = '" + Constants.JOURNEY_STATUS_FINISHED + "'";
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

    public static int getPastJourneysCount(Context context){
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_JOURNEY + " WHERE " + MySQLiteHelper.JOURNEY_COLUMN_STATUS + " = '" + Constants.JOURNEY_STATUS_FINISHED + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public static void updateJourneyStatus(Context context, String journeyId, String journeyStatus) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_STATUS, journeyStatus);
        db.update(MySQLiteHelper.TABLE_JOURNEY, values, MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId, null);
        db.close();
    }

    public static void updateUserActiveStatus(Context context, String journeyId, boolean isActive) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_IS_USER_ACTIVE, isActive ? 1 : 0);
        db.update(MySQLiteHelper.TABLE_JOURNEY, values, MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId, null);
        db.close();
    }

    public static void updateUpdatedAtTime(Context context, long updatedAt, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_UPDATED_AT, updatedAt);
        db.update(MySQLiteHelper.TABLE_JOURNEY, values, MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId, null);
        db.close();
    }

    public static void deleteJourney(Context context, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_JOURNEY, MySQLiteHelper.JOURNEY_COLUMN_ID + "=?", new String[]{journeyId});
        db.close();
    }

    public static void addContactToJourney(Context context, String contactId, String journeyId) {
        List<String> buddyIds = new ArrayList<>(JourneyDataSource.getJourneyById(context, journeyId).getBuddies());
        buddyIds.add(contactId);

        //Update this buddyIds on the database
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS, Joiner.on(",").join(buddyIds));
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        db.update(MySQLiteHelper.TABLE_JOURNEY, values, MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = '" + journeyId + "'", null);
        db.close();
    }

    public static void removeContactFromJourney(Context context, String contactId, String journeyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Journey journey = getJourneyById(context, journeyId);
        journey.getBuddies().remove(contactId);

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS, Joiner.on(",").join(journey.getBuddies()));
        db.update(MySQLiteHelper.TABLE_JOURNEY, values, MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER + " = " + journeyId, null);
        db.close();
    }

    public static void removeAllMemoriesFromJourney(Context context, String journeyId){
        AudioDataSource.deleteAllAudioFromJourney(context, journeyId);
        CheckinDataSource.deleteAllCheckInsFromJourney(context, journeyId);
        MoodDataSource.deleteAllMoodsFromJourney(context, journeyId);
        NoteDataSource.deleteAllNotesFromJourney(context, journeyId);
        PictureDataSource.deleteAllPicturesFromJourney(context, journeyId);
        VideoDataSource.deleteAllVideosFromJourney(context, journeyId);
    }

    public static void removeAllMemoriesByUserFromJourney(Context context, String journeyId, String contactId){
        AudioDataSource.deleteAllAudioFromJourneyByUser(context, journeyId, contactId);
        CheckinDataSource.deleteAllCheckInsFromJourneyByUser(context, journeyId, contactId);
        MoodDataSource.deleteAllMoodsFromJourneyByUser(context, journeyId, contactId);
        NoteDataSource.deleteAllNoteFromJourneyByUser(context, journeyId, contactId);
        PictureDataSource.deleteAllPicturesFromJourneyByUser(context, journeyId, contactId);
        VideoDataSource.deleteAllVideosFromJourneyByUser(context, journeyId, contactId);
    }

    private static List<Journey> parseJourneysAsList(Context context, Cursor cursor) {
        List<Journey> journeyList = new ArrayList<Journey>();
        if (cursor.moveToFirst()) {
            Journey journey;
            while (!cursor.isAfterLast()) {
                journey = new Journey();
                journey.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_ID)));
                journey.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_ID_ONSERVER)));
                journey.setName(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_NAME)));
                journey.setTagLine(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_TAGLINE)));
                journey.setGroupType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_GROUPTYPE)));
                journey.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_CREATEDBY)));
                String buddyIds = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_BUDDY_IDS));
                journey.setBuddies(buddyIds.isEmpty() ? new ArrayList<String>() : new ArrayList<>(Arrays.asList(buddyIds.split(","))));
                journey.setJourneyStatus(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_STATUS)));
                journey.setCreatedAt(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_CREATED_AT)));
                journey.setUpdatedAt(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_UPDATED_AT)));
                journey.setCompletedAt(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_COMPELTED_AT)));
                journey.setLapsList(LapDataSource.getLapFromJourney(context, journey.getIdOnServer()));
                journey.setIsUserActive(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.JOURNEY_COLUMN_IS_USER_ACTIVE)) == 1);
                Log.d(TAG, LapDataSource.getLapFromJourney(context, journey.getIdOnServer()) + "!");
                journeyList.add(journey);
                cursor.moveToNext();
            }
        }
        return journeyList;
    }


}