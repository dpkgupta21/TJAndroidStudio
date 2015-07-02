package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Picture;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PictureDataSource {

    private static final String TAG = "<<PictureDataSource>>";

    // ------------------------ "PICTURE" table methods ----------------//

    /*
     * Creating a picture
     */
    public static long createPicture(Picture newPic, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER, newPic.getIdOnServer());
        values.put(MySQLiteHelper.PICTURE_COLUMN_JID, newPic.getjId());
        values.put(MySQLiteHelper.PICTURE_COLUMN_MEM_TYPE, newPic.getMemType());
        values.put(MySQLiteHelper.PICTURE_COLUMN_CAPTION, newPic.getCaption());
        values.put(MySQLiteHelper.PICTURE_COLUMN_EXT, newPic.getExtension());
        values.put(MySQLiteHelper.PICTURE_COLUMN_SIZE, newPic.getSize());
        values.put(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL, newPic.getDataServerURL());
        values.put(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL, newPic.getDataLocalURL());
        values.put(MySQLiteHelper.PICTURE_COLUMN_CREATEDBY, newPic.getCreatedBy());
        values.put(MySQLiteHelper.PICTURE_COLUMN_CREATEDAT, newPic.getCreatedAt());
        values.put(MySQLiteHelper.PICTURE_COLUMN_UPDATEDAT, newPic.getCreatedAt());
        values.put(MySQLiteHelper.PICTURE_COLUMN_LOCALTHUMBNAILPATH, newPic.getPicThumbnailPath());
        values.put(MySQLiteHelper.PICTURE_COLUMN_LATITUDE, newPic.getLatitude());
        values.put(MySQLiteHelper.PICTURE_COLUMN_LONGITUDE, newPic.getLongitude());

        // insert row
        Long picture_id = db.insert(MySQLiteHelper.TABLE_PICTURE, null, values);
        Log.d(TAG, "New Picture Inserted! with id" + picture_id);

        db.close();

        return picture_id;
    }

    // To get total number of pictures of a journey
    public static int getPicCountOfJourney(Context context, String jId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_JID + " = '" +
                jId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // To get list of all pictures as Picture list
    public static List<Picture> getAllPictures(Context context) {
        List<Picture> memoriesList;
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + "='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        memoriesList = getPictures(cursor, context);
        cursor.close();
        db.close();
        return memoriesList;
    }

    public static Picture getPictureById(Context context, String picId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_ID + " = '" + picId + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "get picture by id" + cursor.getCount() + " " + selectQuery);
        Picture pic = getPictures(cursor, context).get(0);
        cursor.close();
        db.close();
        return pic;
    }

    public static Picture getPictureByIdOnServer(Context context, String picId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER
                + " = '" + picId + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d(TAG, "get picture by id" + cursor.getCount() + " " + selectQuery);
        Picture pic = getPictures(cursor, context).get(0);
        cursor.close();
        db.close();
        return pic;
    }

    public static List<Memories> getPictureMemoriesFromJourney(Context context, String journeyId) {
        List<Memories> memoriesList;
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_JID
                + " = '" + journeyId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        memoriesList = getPictureMemories(cursor, context);

        cursor.close();
        db.close();
        Log.d(TAG, "pictures fetched successfully " + memoriesList.size());
        return memoriesList;
    }

    public static void updateServerIdAndUrl(Context context, String picId, String serverId, String serverUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER, serverId);
        values.put(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL, serverUrl);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " = '" + picId + "'", null);
        db.close();
        Log.d(TAG, "server id and server url saved succesfully");
    }

    public static void updatePicLocalPath(Context context, String newPath, String picId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL, newPath);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " ='" + picId + "'", null);
        db.close();
        Log.d(TAG, "updating " + values + " id = " + picId);
    }

    public static void updateDeleteStatus(Context context, String memLocalId, boolean isDeleted){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_IS_DELETED, isDeleted ? 1 : 0);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " = " + memLocalId, null);
        db.close();
    }

    public static void updateCaption(Context context, String caption, String picId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_CAPTION, caption);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " = " + picId, null);
        db.close();
    }

    public static void deletePictureByIdOnServer(Context context, String idOnServer) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_PICTURE, MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER + "=?", new String[]{idOnServer});
        db.close();
    }

    public static void deleteAllPicturesFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_PICTURE, MySQLiteHelper.PICTURE_COLUMN_JID + "=?", new String[]{journeyId});
        db.close();
    }

    public static void deleteAllPicturesFromJourneyByUser(Context context, String journeyId, String userId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_PICTURE, MySQLiteHelper.PICTURE_COLUMN_JID + "=? AND " + MySQLiteHelper.PICTURE_COLUMN_CREATEDBY
                + "=?", new String[]{journeyId, userId});
        db.close();
    }

    // This method returns list of picture objects from the cursor
    private static List<Picture> getPictures(Cursor cursor, Context context) {
        List<Picture> picturesList = new ArrayList<>();
        cursor.moveToFirst();
        Picture picture;
        while (!cursor.isAfterLast()) {
            picture = new Picture();
            picture.setCaption(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CAPTION)));
            picture.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID)));
            picture.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER)));
            picture.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_MEM_TYPE)));
            picture.setExtension(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_EXT)));
            picture.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_SIZE)));
            picture.setDataServerURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL)));
            picture.setDataLocalURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL)));
            picture.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDBY)));
            picture.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDAT)));
            picture.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_UPDATEDAT)));
            picture.setLikes(LikeDataSource.getLikesForMemory(context, picture.getId(), HelpMe.PICTURE_TYPE));
            picture.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_JID)));
            picture.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LATITUDE)));
            picture.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LONGITUDE)));
            picture.setPicThumbnailPath(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LOCALTHUMBNAILPATH)));
            picturesList.add(picture);
            cursor.moveToNext();
        }
        return picturesList;
    }

    // This method creates a list of picture memories objects from the cursor
    private static List<Memories> getPictureMemories(Cursor cursor, Context context) {
        List<Memories> picturesList = new ArrayList<>();
        cursor.moveToFirst();
        Picture picture;
        while (!cursor.isAfterLast()) {
            picture = new Picture();
            picture.setCaption(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CAPTION)));
            picture.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID)));
            picture.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER)));
            picture.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_MEM_TYPE)));
            picture.setExtension(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_EXT)));
            picture.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_SIZE)));
            picture.setDataServerURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL)));
            picture.setDataLocalURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL)));
            picture.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDBY)));
            picture.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDAT)));
            picture.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_UPDATEDAT)));
            picture.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LATITUDE)));
            picture.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LONGITUDE)));
            picture.setLikes(LikeDataSource.getLikesForMemory(context, picture.getId(), HelpMe.PICTURE_TYPE));
            picture.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_JID)));
            picture.setPicThumbnailPath(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LOCALTHUMBNAILPATH)));
            picturesList.add(picture);
            cursor.moveToNext();
        }
        return picturesList;
    }

    // Fetch a random picture of a journey
    // Used for Active/Past journeys thumbnails
    public static Picture getRandomPicOfJourney(String jId, Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_JID +
                " = '" + jId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        int randomNum = 0;
        Picture randomPic = null;
        int count = c.getCount();

        if (count > 0) {
            Random r = new Random();
            randomNum = r.nextInt(count);
        }

        if (c.moveToFirst() && c.moveToPosition(randomNum)) {
            randomPic = getPictures(c, context).get(0);
        }
        c.close();
        db.close();

        return randomPic;
    }

}
