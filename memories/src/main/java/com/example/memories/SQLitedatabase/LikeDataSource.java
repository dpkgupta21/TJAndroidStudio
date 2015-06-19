package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Like;

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

        long likeId = db.insert(MySQLiteHelper.TABLE_LIKE, null, values);
        Log.d(TAG, "New mood Inserted with id" + likeId);

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
        return (Like)likesList.get(0);

    }

    // returns null if memory has not been liked and Like object if memory has already been liked by a particular user
    public static Like isMemoryLikedByUser(Context context, String memoryId, String userId){
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

    public static void deleteLike(Context context, Like like){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_LIKE, MySQLiteHelper.LIKE_COLUMN_ID + "=?", new String[]{like.getId()});
        db.close();
    }

    public static List<Like> getLikeIdsForMemory(Context context, String memoryId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String selectQuery = "SELECT * FROM " + MySQLiteHelper.TABLE_LIKE + " WHERE " + MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID + " = '"
                + memoryId + "'";
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
        values.put(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID, like.getMemorableId());
        values.put(MySQLiteHelper.LIKE_COLUMN_USER_ID, like.getUserId());

        db.update(MySQLiteHelper.TABLE_LIKE, values, MySQLiteHelper.LIKE_COLUMN_ID + " = '" + like.getId() + "'", null);
        db.close();

    }

    private static List<Like> getLikesFromCursor(Cursor cursor){
        List<Like> likesList = new ArrayList<>();
        Like like;
        if(cursor.moveToFirst()) {
            do {
                like = new Like();
                like.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_ID)));
                like.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_ID_ONSERVER)));
                like.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_JOURNEY_ID)));
                like.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEM_TYPE)));
                like.setMemorableId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_MEMORABLE_ID)));
                like.setUserId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.LIKE_COLUMN_USER_ID)));
                likesList.add(like);
            }while (cursor.moveToNext());
        }
        return likesList;
    }

}
