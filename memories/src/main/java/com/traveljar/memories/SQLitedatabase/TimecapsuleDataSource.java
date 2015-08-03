package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Timecapsule;

import java.util.ArrayList;
import java.util.List;

public class TimecapsuleDataSource {

    private static final String TAG = "<TimecapsuleDataSource>";

    public static long createTimecapsule(Timecapsule newTimecapsule, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_ID_ONSERVER, newTimecapsule.getIdOnServer());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_JID, newTimecapsule.getjId());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_CAPTION, newTimecapsule.getCaption());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_EXT, newTimecapsule.getExtension());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_SIZE, newTimecapsule.getSize());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_VIDEOSERVERURL, newTimecapsule.getVideoServerURL());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_VIDEOLOCALURL, newTimecapsule.getVideoLocalURL());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_CREATED_BY, newTimecapsule.getCreatedBy());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_CREATED_AT, newTimecapsule.getCreatedAt());
        values.put(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT, newTimecapsule.getUpdatedAt());
        values.put(MySQLiteHelper.TIMECAPSULE_COLUMN_LOCALTHUMBNAILPATH, newTimecapsule.getLocalThumbPath());

        long timecapsule_id = db.insert(MySQLiteHelper.TABLE_TIMECAPSULE, null, values);
        Log.d(TAG, "New timecapsule Inserted!");

        db.close();

        return timecapsule_id;
    }

    // Getting single contact
    public static Timecapsule getTimecapsuleById(String id, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_TIMECAPSULE, null, MySQLiteHelper.TIMECAPSULE_COLUMN_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Timecapsule timecapsule = getTimecapsulesList(cursor, context).get(0);
        cursor.close();
        db.close();
        return timecapsule;

    }

    public static Timecapsule getTimecapsuleByIdOnServer(String id, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_TIMECAPSULE, null, MySQLiteHelper.TIMECAPSULE_COLUMN_ID_ONSERVER
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Timecapsule timecapsule = getTimecapsulesList(cursor, context).get(0);
        cursor.close();
        db.close();
        return timecapsule;

    }

    public static List<Timecapsule> getAllTimecapsule(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_TIMECAPSULE + " WHERE "
                + MySQLiteHelper.TIMECAPSULE_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length = " + c.getCount() + " with j_id = " + journeyId);
        List<Timecapsule> timecapsuleList = getTimecapsulesList(c, context);
        c.close();
        db.close();
        return timecapsuleList;
    }

    private static List<Timecapsule> getTimecapsulesList(Cursor cursor, Context context) {
        List<Timecapsule> timecapsulesList = new ArrayList<>();
        cursor.moveToFirst();
        Timecapsule timecapsule;
        while (!cursor.isAfterLast()) {
            timecapsule = new Timecapsule();

            timecapsule.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_ID)));
            timecapsule.setIdOnServer(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_ID_ONSERVER)));
            timecapsule.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_JID)));
            timecapsule.setCaption(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_CAPTION)));
            timecapsule.setExtension(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_EXT)));
            timecapsule.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_SIZE)));
            timecapsule.setVideoServerURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_VIDEOSERVERURL)));
            timecapsule.setVideoLocalURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_VIDEOLOCALURL)));
            timecapsule.setCreatedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_CREATED_BY)));
            timecapsule.setCreatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_CREATED_AT)));
            timecapsule.setUpdatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_UPDATED_AT)));
            timecapsule.setLocalThumbPath(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.TIMECAPSULE_COLUMN_LOCALTHUMBNAILPATH)));
            timecapsulesList.add(timecapsule);
            cursor.moveToNext();
        }
        return timecapsulesList;
    }


}
