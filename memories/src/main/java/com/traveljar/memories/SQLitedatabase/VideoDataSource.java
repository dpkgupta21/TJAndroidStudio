package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Video;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
        values.put(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT, newVideo.getUpdatedAt());
/*        values.put(MySQLiteHelper.VIDEO_COLUMN_LIKED_BY, newVideo.getLikedBy() == null ? null : Joiner.on(",").join(newVideo.getLikedBy()));*/
        values.put(MySQLiteHelper.VIDEO_COLUMN_LOCALTHUMBNAILPATH, newVideo.getLocalThumbPath());
        values.put(MySQLiteHelper.VIDEO_COLUMN_LATITUDE, newVideo.getLatitude());
        values.put(MySQLiteHelper.VIDEO_COLUMN_LONGITUDE, newVideo.getLongitude());
        long video_id = db.insert(MySQLiteHelper.TABLE_VIDEO, null, values);
        Log.d(TAG, "New video Inserted!");

        db.close();

        return video_id;
    }

    // To get total number of videos of a journey
    public static int getVideoCountOfJourney(Context context, String jId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE " + MySQLiteHelper.VIDEO_COLUMN_JID + " = '" + jId
                + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

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

    public static Video getVideoByIdOnServer(String id, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_VIDEO, null, MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Video video = getVideosList(cursor, context).get(0);
        cursor.close();
        db.close();
        return video;

    }

    public static List<Memories> getAllVideoMemories(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE " + MySQLiteHelper.VIDEO_COLUMN_JID + " = '"
                + journeyId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length = " + c.getCount() + " with j_id = " + journeyId);
        List<Memories> memoriesList = getVideosMemoryList(c, context);
        c.close();
        db.close();
        return memoriesList;
    }

    public static List<Memories> getAllVideos(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED
                + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        List<Memories> videosList = getVideosMemoryList(c, context);
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

    public static void updateDeleteStatus(Context context, String memLocalId, boolean isDeleted){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_IS_DELETED, isDeleted ? 1 : 0);
        values.put(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT, HelpMe.getCurrentTime());
        db.update(MySQLiteHelper.TABLE_VIDEO, values, MySQLiteHelper.VIDEO_COLUMN_ID + " = " + memLocalId, null);
        db.close();
    }

    public static void updateVideoLocalUrl(Context context, String memId, String localUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_DATALOCALURL, localUrl);
        db.update(MySQLiteHelper.TABLE_VIDEO, values, MySQLiteHelper.VIDEO_COLUMN_ID + " = "
                + memId, null);

        db.close();
    }

    public static void updateCaption(Context context, String caption, String videoId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VIDEO_COLUMN_CAPTION, caption);
        values.put(MySQLiteHelper.VIDEO_COLUMN_UPDATED_AT, HelpMe.getCurrentTime());
        db.update(MySQLiteHelper.TABLE_VIDEO, values, MySQLiteHelper.VIDEO_COLUMN_ID + " = " + videoId, null);
        db.close();
    }

    public static void deleteVideoByIdOnServer(Context context, String idOnServer){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_VIDEO, MySQLiteHelper.VIDEO_COLUMN_ID_ONSERVER + "=?", new String[]{idOnServer});
        db.close();
    }

    public static void deleteAllVideosFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_VIDEO, MySQLiteHelper.VIDEO_COLUMN_JID + "=?", new String[]{journeyId});
        db.close();
    }

    public static void deleteAllVideosFromJourneyByUser(Context context, String journeyId, String userId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_VIDEO, MySQLiteHelper.VIDEO_COLUMN_JID + "=? AND " + MySQLiteHelper.VIDEO_COLUMN_CREATED_BY
                + "=?", new String[]{journeyId, userId});
        db.close();
    }

    public static Video getRandomVideoOfJourney(String jId, Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_VIDEO + " WHERE " + MySQLiteHelper.VIDEO_COLUMN_JID +
                " = '" + jId + "' AND " + MySQLiteHelper.VIDEO_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        int randomNum = 0;
        Video randomVideo = null;
        int count = c.getCount();

        if (count > 0) {
            Random r = new Random();
            randomNum = r.nextInt(count);
        }

        if (c.moveToFirst() && c.moveToPosition(randomNum)) {
            randomVideo = getVideosList(c, context).get(0);
        }
        c.close();
        db.close();

        return randomVideo;
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
            video.setLatitude(cursor.getDouble(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LATITUDE)));
            video.setLongitude(cursor.getDouble(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LONGITUDE)));
/*            String liked = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LIKEDBY));
            video.setLikedBy(liked == null ? null : new ArrayList<String>(Arrays.asList(liked)));*/
            video.setLikes(LikeDataSource.getLikesForMemory(context, video.getId(), HelpMe.VIDEO_TYPE));
            video.setLocalThumbPath(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LOCALTHUMBNAILPATH)));
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
            video.setLatitude(cursor.getDouble(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LATITUDE)));
            video.setLongitude(cursor.getDouble(cursor
                    .getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LONGITUDE)));
/*            String liked = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LIKEDBY));
            video.setLikedBy(liked == null ? null : new ArrayList<String>(Arrays.asList(liked)));*/
            video.setLikes(LikeDataSource.getLikesForMemory(context, video.getId(), HelpMe.VIDEO_TYPE));
            video.setLocalThumbPath(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VIDEO_COLUMN_LOCALTHUMBNAILPATH)));
            videosList.add(video);
            cursor.moveToNext();
        }
        return videosList;
    }

}
