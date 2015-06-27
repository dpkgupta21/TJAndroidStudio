package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Like;

import java.util.ArrayList;
import java.util.List;

public class LikeDataSource {

    private static final String TAG = "LikeDataSource";

    public static long createLike(Like like, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER, like.getIdOnServer());
        values.put(MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID, like.getJourneyId());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEM_TYPE, like.getMemType());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, like.getMemoryLocalId());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORY_SERVERID, like.getMemoryServerId());
        values.put(MySQLiteHelper.LIKE_COLUMN_CREATED_AT, like.getCreatedAt());
        values.put(MySQLiteHelper.LIKE_COLUMN_CREATED_AT, like.getUpdatedAt());
        values.put(MySQLiteHelper.LIKE_COLUMN_USER_ID, like.getUserId());
        values.put(MySQLiteHelper.LIKE_COLUMN_IS_VALID, (like.isValid()) ? 1 : 0);

        long likeId = db.insert(MySQLiteHelper.TABLE_LIKE, null, values);
        Log.d(TAG, "New like Inserted with id" + likeId);

        db.close();

        return likeId;
    }

    public static void deleteAllLikesFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID + "=?", new String[]{journeyId});
        db.close();
    }

    public static Like getLikeById(String id, Context context) {
        Log.d(TAG, "fetching like item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_LIKE, null,
                MySQLiteHelper.LIKE_COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Like> likesList = getLikesFromCursor(cursor);
        cursor.close();
        db.close();
        return likesList.get(0);

    }

    public static Like getLikeByIdOnServer(String id, Context context) {
        Log.d(TAG, "fetching like item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_LIKE, null,
                MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Like> likesList = getLikesFromCursor(cursor);
        cursor.close();
        db.close();
        return likesList.get(0);

    }

    public static void deleteLike(Context context, String likeId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_ID + "=?", new String[]{likeId});
        db.close();
    }

    public static void deleteLike(Context context, String memoryId, String userId, String memType) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + "=? AND " + MySQLiteHelper.LIKE_COLUMN_USER_ID +
                        "=? AND " + MySQLiteHelper.LIKE_COLUMN_MEM_TYPE + "=?",
                new String[]{memoryId, userId, memType});
        db.close();
    }

    public static void updateMemoryLocalId(String memoryServerId, String memType, String memoryLocalId, Context context){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, memoryLocalId);
        db.update(MySQLiteHelper.TABLE_LIKE, values, MySQLiteHelper.LIKE_COLUMN_MEMORY_SERVERID + "='" + memoryServerId + "' AND "
                + MySQLiteHelper.LIKE_COLUMN_MEM_TYPE + "='" + memType + "'", null);
        db.close();
    }

    public static List<Like> getLikesForMemory(Context context, String memoryId, String memType) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_LIKE + " WHERE " + MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + "='"
                + memoryId + "' AND " + MySQLiteHelper.LIKE_COLUMN_MEM_TYPE + "='" + memType + "' AND "
                + MySQLiteHelper.LIKE_COLUMN_IS_VALID + "='1'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Like> likesList = getLikesFromCursor(cursor);
        cursor.close();
        db.close();
        return likesList;
    }

    public static void updateLike(Like like, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER, like.getIdOnServer());
        values.put(MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID, like.getJourneyId());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEM_TYPE, like.getMemType());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, like.getMemoryLocalId());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORY_SERVERID, like.getMemoryServerId());
        values.put(MySQLiteHelper.LIKE_COLUMN_UPDATED_AT, like.getUpdatedAt());
        values.put(MySQLiteHelper.LIKE_COLUMN_CREATED_AT, like.getCreatedAt());
        values.put(MySQLiteHelper.LIKE_COLUMN_USER_ID, like.getUserId());
        values.put(MySQLiteHelper.LIKE_COLUMN_IS_VALID, like.isValid() ? 1 : 0);

        db.update(MySQLiteHelper.TABLE_LIKE, values, MySQLiteHelper.LIKE_COLUMN_ID + " = '" + like.getId() + "'", null);
        db.close();

    }

    private static List<Like> getLikesFromCursor(Cursor cursor) {
        List<Like> likesList = new ArrayList<>();
        Like like;
        if (cursor.moveToFirst()) {
            do {
                like = new Like();
                like.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_ID)));
                like.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER)));
                like.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID)));
                like.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEM_TYPE)));
                like.setMemoryLocalId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID)));
                like.setMemoryServerId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEMORY_SERVERID)));
                like.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_CREATED_AT)));
                like.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_UPDATED_AT)));
                like.setUserId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_USER_ID)));
                int isV = (cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_IS_VALID)));
                like.setIsValid(isV == 1);
                likesList.add(like);
            } while (cursor.moveToNext());
        }
        return likesList;
    }

}
