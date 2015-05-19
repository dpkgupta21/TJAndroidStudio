package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Memories;
import com.example.memories.models.Picture;

import java.util.ArrayList;
import java.util.List;

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
        values.put(MySQLiteHelper.PICTURE_COLUMN_LIKEDBY, newPic.getLikedBy());

        // insert row
        Long picture_id = db.insert(MySQLiteHelper.TABLE_PICTURE, null, values);
        Log.d(TAG, "New Picture Inserted! with ");

        db.close();

        return picture_id;
    }

    // To get list of all pictures as Picture list
    public static List<Picture> getAllPictures(Context context) {
        List<Picture> memoriesList;
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        memoriesList = getPictures(cursor);
        db.close();
        cursor.close();
        return memoriesList;
    }

    // To get list of all pictures as Memories list
    public static List<Memories> getAllPictureMemories(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Memories> memoriesList = getPictureMemories(cursor);
        cursor.close();
        db.close();
        return memoriesList;
    }

    public static Picture getPictureById(Context context, String picId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_ID + " = " + picId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Picture pic = null;
        if (cursor.moveToFirst()) {
            pic = getPictures(cursor).get(0);
        }
        cursor.close();
        db.close();
        return pic;
    }

    public static List<Memories> getPictureMemoriesFromJourney(Context context, String journeyId) {
        List<Memories> memoriesList;
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_PICTURE + " WHERE "
                + MySQLiteHelper.PICTURE_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        memoriesList = getPictureMemories(cursor);

        cursor.close();
        db.close();
        return memoriesList;
    }

    public static void updateServerIdAndUrl(Context context, String picId, String serverId, String serverUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER, serverId);
        values.put(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL, serverUrl);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " = " + picId, null);
        db.close();
    }

    public static void updateFavourites(Context context, String memId, String likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.PICTURE_COLUMN_LIKEDBY, likedBy);
        db.update(MySQLiteHelper.TABLE_PICTURE, values, MySQLiteHelper.PICTURE_COLUMN_ID + " = "
                + memId, null);
        db.close();
    }

    // This method returns list of picture objects from the cursor
    private static List<Picture> getPictures(Cursor cursor) {
        List<Picture> picturesList = new ArrayList<Picture>();
        cursor.moveToFirst();
        Picture picture;
        while (!cursor.isAfterLast()) {
            picture = new Picture();
            picture.setCaption(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CAPTION)));
            picture.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID)));
            picture.setIdOnServer(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER)));
            picture.setMemType(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_MEM_TYPE)));
            picture.setExtension(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_EXT)));
            picture.setSize(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_SIZE)));
            picture.setDataServerURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL)));
            picture.setDataLocalURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL)));
            picture.setCreatedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDBY)));
            picture.setCreatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDAT)));
            picture.setUpdatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_UPDATEDAT)));
            picture.setLikedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LIKEDBY)));
            picture.setjId(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_JID)));
            picturesList.add(picture);
            cursor.moveToNext();
        }
        return picturesList;
    }

    // This method creates a list of picture memories objects from the cursor
    private static List<Memories> getPictureMemories(Cursor cursor) {
        List<Memories> picturesList = new ArrayList<Memories>();
        cursor.moveToFirst();
        Picture picture;
        while (!cursor.isAfterLast()) {
            picture = new Picture();
            picture.setCaption(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CAPTION)));
            picture.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID)));
            picture.setIdOnServer(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_ID_ONSERVER)));
            picture.setMemType(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_MEM_TYPE)));
            picture.setExtension(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_EXT)));
            picture.setSize(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_SIZE)));
            picture.setDataServerURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATASERVERURL)));
            picture.setDataLocalURL(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_DATALOCALURL)));
            picture.setCreatedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDBY)));
            picture.setCreatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_CREATEDAT)));
            picture.setUpdatedAt(cursor.getLong(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_UPDATEDAT)));
            picture.setLikedBy(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_LIKEDBY)));
            picture.setjId(cursor.getString(cursor
                    .getColumnIndex(MySQLiteHelper.PICTURE_COLUMN_JID)));
            picturesList.add(picture);
            cursor.moveToNext();
        }
        return picturesList;
    }

}
