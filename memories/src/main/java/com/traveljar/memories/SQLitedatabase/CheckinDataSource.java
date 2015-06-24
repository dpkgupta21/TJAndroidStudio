package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Joiner;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CheckinDataSource {

    private static final String TAG = "<<CheckinDataSource>>";

    // Database fields
    // ------------------------ "CHECKIN" table methods ----------------//

    /*
     * Creating a checkin
     */
    public static long createCheckIn(CheckIn newCheckIn, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CHECKIN_COLUMN_ID_ONSERVER, newCheckIn.getIdOnServer());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_JID, newCheckIn.getjId());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_MEM_TYPE, newCheckIn.getMemType());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_CAPTION, newCheckIn.getCaption());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LATITUDE, newCheckIn.getLatitude());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LONGITUDE, newCheckIn.getLongitude());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_PIC_URL, newCheckIn.getCheckInPicURL());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_PLACE_NAME, newCheckIn.getCheckInPlaceName());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_WITH, newCheckIn.getCheckInWith() == null ? null : Joiner.on(",").join(newCheckIn.getCheckInWith()));
        values.put(MySQLiteHelper.CHECKIN_COLUMN_CREATED_BY, newCheckIn.getCreatedBy());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_CREATED_AT, newCheckIn.getCreatedAt());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_UPDATED_AT, newCheckIn.getUpdatedAt());
       /* values.put(MySQLiteHelper.CHECKIN_COLUMN_LIKED_BY, newCheckIn.getLikedBy() == null ? null : Joiner.on(",").join(newCheckIn.getLikedBy()));*/
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LATITUDE, newCheckIn.getLatitude());
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LONGITUDE, newCheckIn.getLongitude());

        // insert row
        Long checkin_id = db.insert(MySQLiteHelper.TABLE_CHECKIN, null, values);
        Log.d(TAG, "New Checkin Inserted!");

        db.close();
        return checkin_id;
    }

    public static Cursor getCheckInsOfCurrentJourney(String j_id, Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_CHECKIN + " WHERE "
                + MySQLiteHelper.CHECKIN_COLUMN_JID + " = " + j_id;

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();
        } else {
            Log.d(TAG, "no past checkins!!!");
        }

        c.close();
        db.close();
        return c;
    }

    public static CheckIn getCheckInById(String id, Context context) {
        Log.d(TAG, "fetching one checkin item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_CHECKIN, null,
                MySQLiteHelper.CHECKIN_COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Memories> checkInsList = getCheckInsFromCursor(cursor, context);
        cursor.close();
        db.close();
        return (CheckIn)checkInsList.get(0);

    }

    public static CheckIn getCheckInByIdOnServer(String id, Context context) {
        Log.d(TAG, "fetching one checkin item from DB with id =" + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_CHECKIN, null,
                MySQLiteHelper.CHECKIN_COLUMN_ID_ONSERVER + "=?", new String[]{String.valueOf(id)}, null,
                null, null, null);

        List<Memories> checkInsList = getCheckInsFromCursor(cursor, context);
        cursor.close();
        db.close();
        return (CheckIn)checkInsList.get(0);

    }

/*    public static void updateFavourites(Context context, String memId, List<String> likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CHECKIN_COLUMN_LIKED_BY, likedBy == null ? null : Joiner.on(",").join(likedBy));
        db.update(MySQLiteHelper.TABLE_CHECKIN, values, MySQLiteHelper.CHECKIN_COLUMN_ID + " = "
                + memId, null);
        db.close();
    }*/

    public static void updateServerId(Context context, String checkinId, String serverId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CHECKIN_COLUMN_ID_ONSERVER, serverId);
        db.update(MySQLiteHelper.TABLE_CHECKIN, values, MySQLiteHelper.CHECKIN_COLUMN_ID + " = " + checkinId, null);
        db.close();
    }

    public static List<Memories> getAllCheckinsList(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_CHECKIN + " WHERE "
                + MySQLiteHelper.CHECKIN_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length" + c.getCount() + journeyId);
        List<Memories> checkInsList = getCheckInsFromCursor(c, context);
        c.close();
        db.close();
        return checkInsList;
    }

    public static void deleteCheckIn(Context context, String checkInId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_CHECKIN, MySQLiteHelper.CHECKIN_COLUMN_ID + "=?", new String[]{checkInId});
        db.close();
    }

    private static List<Memories> getCheckInsFromCursor(Cursor cursor, Context context){
        List<Memories> checkInsList = new ArrayList<Memories>();
        CheckIn checkin;
        if(cursor.moveToFirst()){
            do{
                checkin = new CheckIn();

                checkin.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_ID)));
                checkin.setIdOnServer(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_ID_ONSERVER)));
                checkin.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_JID)));
                checkin.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_MEM_TYPE)));
                checkin.setCaption(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_CAPTION)));

                checkin.setCheckInPlaceName(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_PLACE_NAME)));
                checkin.setCheckInPicURL(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_PIC_URL)));
                String list = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_WITH));
                if (list != null) {
                    checkin.setCheckInWith(Arrays.asList(list.split(",")));
                }
                checkin.setCreatedBy(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_CREATED_BY)));
                checkin.setCreatedAt(cursor.getLong(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_CREATED_AT)));
                checkin.setUpdatedAt(cursor.getLong(cursor
                        .getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_UPDATED_AT)));
                /*String liked = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LIKEDBY));
                checkin.setLikedBy(liked == null ? null : new ArrayList<String>(Arrays.asList(liked)));*/
                checkin.setLikes(LikeDataSource.getLikesForMemory(context, checkin.getId(), HelpMe.CHECKIN_TYPE));

                checkin.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_LATITUDE)));
                checkin.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.CHECKIN_COLUMN_LONGITUDE)));
                checkInsList.add(checkin);
            }while(cursor.moveToNext());
        }
        return checkInsList;
    }

}
