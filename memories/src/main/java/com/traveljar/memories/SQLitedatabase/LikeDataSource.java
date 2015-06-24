package com.traveljar.memories.SQLitedatabase;

/**
 * Created by abhi on 19/06/15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Like;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ankit on 19/6/15.
 */
public class LikeDataSource {

    private static final String TAG = "LikeDataSource";

    public static long createLike(Like like, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER, like.getIdOnServer());
        values.put(MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID, like.getJourneyId());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEM_TYPE, like.getMemType());
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, like.getMemorableId());
        values.put(MySQLiteHelper.LIKE_COLUMN_USER_ID, like.getUserId());
        values.put(MySQLiteHelper.LIKE_COLUMN_IS_VALID, (like.isValid()) ? 1 : 0);

        long likeId = db.insert(MySQLiteHelper.TABLE_LIKE, null, values);
        Log.d(TAG, "New like Inserted with id" + likeId);

        db.close();

        return likeId;
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
        return (Like) likesList.get(0);

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

    // returns null if memory has not been liked and Like object if memory has already been liked by a particular user
    public static Like isMemoryLikedByUser(Context context, String memoryId, String userId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_LIKE + " WHERE " + MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + " = '"
                + memoryId + "' AND " + MySQLiteHelper.LIKE_COLUMN_USER_ID + " = '" + userId + "'";
        Log.d(TAG, "query is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Like> likes = getLikesFromCursor(cursor);
        Log.d(TAG, "is memory liked by user" + !(cursor.getCount() == 0) + cursor.getCount());
        cursor.close();
        db.close();
        return likes.size() == 0 ? null : likes.get(0);
    }

    public static void deleteLike(Context context, String likeId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_ID + "=?", new String[]{likeId});
        db.close();
    }

    public static void deleteLikeWithMemIdAndUser(Context context, String memoryId, String userId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + "=? AND " + MySQLiteHelper.LIKE_COLUMN_USER_ID + "=?",
                new String[]{memoryId, userId});
        db.close();
    }

    public static List<Like> getLikesForMemory(Context context, String memoryId, String memType) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_LIKE + " WHERE " + MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + "='"
                + memoryId + "' AND " + MySQLiteHelper.LIKE_COLUMN_MEM_TYPE + "='" + memType + "' AND "
                + MySQLiteHelper.LIKE_COLUMN_IS_VALID + "='1'" ;
        Log.d(TAG, "query is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Like> likesList = getLikesFromCursor(cursor);
        Log.d(TAG, "likes from memory are " + likesList);
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
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, like.getMemorableId());
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
                like.setMemorableId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID)));
                like.setUserId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_USER_ID)));
                int isV = (cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_IS_VALID)));
                like.setIsValid((isV == 1) ? true : false);
                likesList.add(like);
            } while (cursor.moveToNext());
        }
        return likesList;
    }

}
//+ MySQLiteHelper.LIKE_COLUMN_MEM_TYPE + " = '" + memType + "' AND "