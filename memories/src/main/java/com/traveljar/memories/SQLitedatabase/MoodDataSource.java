package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Joiner;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoodDataSource {

    private static final String TAG = "MOOD_DATA_SOURCE";

    public static long createMood(Mood newMood, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER, newMood.getIdOnServer());
        values.put(MySQLiteHelper.MOOD_COLUMN_JID, newMood.getjId());
        values.put(MySQLiteHelper.MOOD_COLUMN_MEM_TYPE, newMood.getMemType());
        values.put(MySQLiteHelper.MOOD_COLUMN_FRIENDS_ID, Joiner.on(",").join(newMood.getBuddyIds()));
        values.put(MySQLiteHelper.MOOD_COLUMN_MOOD, newMood.getMood());
        values.put(MySQLiteHelper.MOOD_COLUMN_REASON, newMood.getReason());
        values.put(MySQLiteHelper.MOOD_COLUMN_CREATED_BY, newMood.getCreatedBy());
        values.put(MySQLiteHelper.MOOD_COLUMN_CREATED_AT, newMood.getCreatedAt());
        values.put(MySQLiteHelper.MOOD_COLUMN_UPDATED_AT, newMood.getUpdatedAt());
        values.put(MySQLiteHelper.MOOD_COLUMN_LATITUDE, newMood.getLatitude());
        values.put(MySQLiteHelper.MOOD_COLUMN_LONGITUDE, newMood.getLongitude());

        long mood_id = db.insert(MySQLiteHelper.TABLE_MOOD, null, values);
        Log.d(TAG, "New mood Inserted!");

        db.close();

        return mood_id;
    }

    // To get total number of moods of a journey
    public static int getMoodCountOfJourney(Context context, String jId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_MOOD + " WHERE " + MySQLiteHelper.MOOD_COLUMN_JID + " = '"
                + jId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public static Mood getMoodById(String id, Context context) {
        Log.d(TAG, "fetching one mood item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_MOOD, null,
                MySQLiteHelper.MOOD_COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Memories> moodsList = getMoodsFromCursor(cursor, context);
        cursor.close();
        db.close();
        return (Mood)moodsList.get(0);

    }

    public static Mood getMoodByIdOnServer(String id, Context context) {
        Log.d(TAG, "fetching one mood item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_MOOD, null,
                MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Memories> moodsList = getMoodsFromCursor(cursor, context);
        cursor.close();
        db.close();
        return (Mood)moodsList.get(0);

    }

    private static List<Memories> getMoodsFromCursor(Cursor cursor, Context context){
        List<Memories> moodsList = new ArrayList<>();
        Mood mood;
        if(cursor.moveToFirst()) {
            do {
                mood = new Mood();

                mood.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_ID)));
                mood.setIdOnServer(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER)));
                mood.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_JID)));
                mood.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_MEM_TYPE)));
                mood.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_CREATED_BY)));
                mood.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_CREATED_AT)));
                mood.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_UPDATED_AT)));
                mood.setLikes(LikeDataSource.getLikesForMemory(context, mood.getId(), HelpMe.MOOD_TYPE));
                mood.setBuddyIds(Arrays.asList((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_FRIENDS_ID))).split(",")));
                mood.setMood(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_MOOD)));
                mood.setReason(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_REASON)));
                mood.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_LATITUDE)));
                mood.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_LONGITUDE)));
                moodsList.add(mood);
            }while (cursor.moveToNext());
        }
        return moodsList;
    }

    public static List<Memories> getMoodsFromJourney(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_MOOD + " WHERE " + MySQLiteHelper.MOOD_COLUMN_JID + " = '"
                + journeyId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        List<Memories> memoriesList = getMoodsFromCursor(c, context);
        c.close();
        db.close();
        return memoriesList;
    }

    public static void updateServerId(Context context, String moodId, String serverId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER, serverId);
        db.update(MySQLiteHelper.TABLE_MOOD, values, MySQLiteHelper.MOOD_COLUMN_ID + " = " + moodId, null);
        db.close();
    }

    public static void deleteMoodByServerId(Context context, String idOnServer){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_MOOD, MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER + "=?", new String[]{idOnServer});
        db.close();
    }

    public static void updateDeleteStatus(Context context, String memLocalId, boolean isDeleted){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MOOD_COLUMN_IS_DELETED, isDeleted ? 1 : 0);
        db.update(MySQLiteHelper.TABLE_MOOD, values, MySQLiteHelper.MOOD_COLUMN_ID + " = " + memLocalId, null);
        db.close();
    }

}
