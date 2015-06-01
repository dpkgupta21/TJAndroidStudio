package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Memories;
import com.example.memories.models.Mood;
import com.google.common.base.Joiner;

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
        values.put(MySQLiteHelper.MOOD_COLUMN_LIKED_BY, newMood.getLikedBy() == null ? null : Joiner.on(",").join(newMood.getLikedBy()));

        long mood_id = db.insert(MySQLiteHelper.TABLE_MOOD, null, values);
        Log.d(TAG, "New mood Inserted!");

        db.close();

        return mood_id;
    }

    public static List<Memories> getMoodsFromJourney(Context context, String journeyId) {
        List<Memories> moodsList = new ArrayList<Memories>();
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_MOOD + " WHERE "
                + MySQLiteHelper.MOOD_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length" + c.getCount() + journeyId);
        c.moveToFirst();
        Mood mood;
        while (!c.isAfterLast()) {
            mood = new Mood();

            mood.setId(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_ID)));
            mood.setIdOnServer(c.getString(c
                    .getColumnIndex(MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER)));
            mood.setjId(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_JID)));
            mood.setMemType(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_MEM_TYPE)));
            mood.setCreatedBy(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_CREATED_BY)));
            mood.setCreatedAt(c.getLong(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_CREATED_AT)));
            mood.setUpdatedAt(c.getLong(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_UPDATED_AT)));
            String liked = c.getString(c.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LIKEDBY));
            mood.setLikedBy(liked == null ? null : new ArrayList<String>(Arrays.asList(liked)));
            mood.setBuddyIds(Arrays.asList((c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_FRIENDS_ID))).split(",")));
            mood.setMood(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_MOOD)));
            mood.setReason(c.getString(c.getColumnIndex(MySQLiteHelper.MOOD_COLUMN_REASON)));
            moodsList.add(mood);
            c.moveToNext();
        }
        c.close();
        db.close();

        return moodsList;
    }

    public static void updateServerId(Context context, String moodId, String serverId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MOOD_COLUMN_ID_ONSERVER, serverId);
        db.update(MySQLiteHelper.TABLE_MOOD, values, MySQLiteHelper.MOOD_COLUMN_ID + " = " + moodId, null);
        db.close();
    }

    public static void updateFavourites(Context context, String memId, List<String> likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MOOD_COLUMN_LIKED_BY, likedBy == null ? null : Joiner.on(",").join(likedBy));
        db.update(MySQLiteHelper.TABLE_MOOD, values, MySQLiteHelper.MOOD_COLUMN_ID + " = " + memId, null);
        db.close();
    }

}
