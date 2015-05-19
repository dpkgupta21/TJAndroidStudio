package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Memories;
import com.example.memories.models.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoDataSource {

    private static final String TAG = "<VideoDataSource>";

    public static long createVideo(Video newVideo, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER, newVideo.getIdOnServer());
        values.put(MySQLiteHelper.VIDEO_COLUMN_JID, newVideo.getjId());
        values.put(MySQLiteHelper.VIDEO_COLUMN_MEM_TYPE, newVideo.getMemType());
        values.put(MySQLiteHelper.VIDEO_COLUMN_CAPTION, newVideo.getCaption());
        values.put(MySQLiteHelper.VIDEO_COLUMN_EXT, newVideo.getExtension());
        values.put(MySQLiteHelper.VIDEO_COLUMN_SIZE, newVideo.getSize());
        values.put(MySQLiteHelper.VIDEO_COLUMN_DATASERVERURL, newVideo.getDataServerURL());
        values.put(MySQLiteHelper.VIDEO_COLUMN_DATALOCALURL, newVideo.getDataLocalURL());
        values.put(MySQLiteHelper.VIDEO_COLUMN_CREATED_BY, newVideo.getCreatedBy());
        values.put(MySQLiteHelper.VIDEO_COLUMN_CREATED_AT, newVideo.getCreatedAt());
        values.put(MySQLiteHelper.VIDEO_COLUMN_LIKED_BY, newVideo.getLikedBy());

        long video_id = db.insert(MySQLiteHelper.TABLE_VIDEO, null, values);
        Log.d(TAG, "New video Inserted!");

        db.close();

        return video_id;
    }

    /**
     * getting all videos
     */

    // Getting single contact
    public static Video getVideoById(String id, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_VIDEO, null, MySQLiteHelper.VIDEO_COLUMN_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Video video = getVideosList(cursor, context).get(0);
        cursor.close();
        db.close();
        return video;

    }

    public static List<Memories> getAllVideoMemories(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE "
                + MySQLiteHelper.VIDEO_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length = " + c.getCount() + " with j_id = " + journeyId);
        List<Memories> memoriesList = getVideosMemoryList(c, context);
        c.close();
        db.close();
        return memoriesList;
    }

    public static List<Video> getAllVideosFromJourney(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE "
                + MySQLiteHelper.VIDEO_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length = " + c.getCount() + " with j_id = " + journeyId);
        List<Video> videosList = getVideosList(c, context);
        c.close();
        db.close();
        return videosList;
    }

    public static List<Video> getAllVideos(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        List<Video> videosList = getVideosList(c, context);
        c.close();
        db.close();
        return videosList;
    }

    public static void updateServerIdAndUrl(Context context, String videoId, String serverId, String serverUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER, serverId);
        values.put(MySQLiteHelper.VIDEO_COLUMN_DATASERVERURL, serverUrl);
        db.update(MySQLiteHelper.TABLE_VIDEO, values, MySQLiteHelper.VIDEO_COLUMN_ID + " = " + videoId, null);
        db.close();
    }

    public static void updateFavourites(Context context, String memId, String likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_LIKED_BY, likedBy);
        db.update(MySQLiteHelper.TABLE_VIDEO, values, MySQLiteHelper.VIDEO_COLUMN_ID + " = "
                + memId, null);

        db.close();
    }

    private static List<Memories> getVideosMemoryList(Cursor cursor, Context context) {
        List<Memories> videosList = new ArrayList<Memories>();
        cursor.moveToFirst();
        Video video;
        while (!cursor.isAfterLast()) {
            video = new Video();

            video.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_ID)));
            video.setIdOnServer(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER)));
            video.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_JID)));
            video.setMemType(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_MEM_TYPE)));
            video.setCaption(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CAPTION)));
            video.setExtension(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_EXT)));
            video.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_SIZE)));
            video.setDataServerURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_DATASERVERURL)));
            video.setDataLocalURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_DATALOCALURL)));
            video.setCreatedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CREATED_BY)));
            video.setCreatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CREATED_AT)));
            video.setUpdatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT)));
            video.setLikedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LIKED_BY)));
            videosList.add(video);
            cursor.moveToNext();
        }
        return videosList;
    }

    private static List<Video> getVideosList(Cursor cursor, Context context) {
        List<Video> videosList = new ArrayList<Video>();
        Video video;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            video = new Video();

            video.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_ID)));
            video.setIdOnServer(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER)));
            video.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_JID)));
            video.setMemType(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_MEM_TYPE)));
            video.setCaption(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CAPTION)));
            video.setExtension(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_EXT)));
            video.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_SIZE)));
            video.setDataServerURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_DATASERVERURL)));
            video.setDataLocalURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_DATALOCALURL)));
            video.setCreatedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CREATED_BY)));
            video.setCreatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_CREATED_AT)));
            video.setUpdatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT)));
            video.setLikedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LIKED_BY)));
            videosList.add(video);
            cursor.moveToNext();
        }
        return videosList;
    }

}
