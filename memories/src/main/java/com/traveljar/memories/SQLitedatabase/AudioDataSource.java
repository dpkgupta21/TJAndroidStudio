package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Audio;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class AudioDataSource {

    private static final String TAG = "<<AudioDataSource>>";

    public static long createAudio(Audio newVoice, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER, newVoice.getIdOnServer());
        values.put(MySQLiteHelper.VOICE_COLUMN_JID, newVoice.getjId());
        values.put(MySQLiteHelper.VOICE_COLUMN_EXT, newVoice.getExtension());
        values.put(MySQLiteHelper.VOICE_COLUMN_MEM_TYPE, newVoice.getMemType());
        values.put(MySQLiteHelper.VOICE_COLUMN_SIZE, newVoice.getSize());
        values.put(MySQLiteHelper.VOICE_COLUMN_DATASERVERURL, newVoice.getDataServerURL());
        values.put(MySQLiteHelper.VOICE_COLUMN_DATALOCALURL, newVoice.getDataLocalURL());
        values.put(MySQLiteHelper.VOICE_COLUMN_CREATEDBY, newVoice.getCreatedBy());
        values.put(MySQLiteHelper.VOICE_COLUMN_CREATEDAT, newVoice.getCreatedAt());
        values.put(MySQLiteHelper.VOICE_COLUMN_UPDATEDAT, newVoice.getUpdatedAt());
        values.put(MySQLiteHelper.VOICE_COLUMN_LATITUDE, newVoice.getLatitude());
        values.put(MySQLiteHelper.VOICE_COLUMN_LONGITUDE, newVoice.getLongitude());
        values.put(MySQLiteHelper.VOICE_COLUMN_DURATION, newVoice.getAudioDuration());
        long voice_id = db.insert(MySQLiteHelper.TABLE_AUDIO, null, values);
        Log.d(TAG, "New audio Inserted!");

        db.close();
        return voice_id;
    }

    // To get total number of pictures of a journey
    public static int getAudioCountOfJourney(Context context, String jId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.VOICE_COLUMN_JID + " = '"
                + jId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public static List<Audio> getAllAudios(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Audio> audioList = getAudiosList(context, cursor);
        cursor.close();
        db.close();
        return audioList;
    }

    public static Audio getAudioById(Context context, String audioId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.VOICE_COLUMN_ID + " = " + audioId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Audio audio = null;
        if (cursor.moveToFirst()) {
            audio = getAudiosList(context, cursor).get(0);
        }
        cursor.close();
        db.close();
        return audio;
    }

    public static Audio getAudioByServerId(Context context, String audioId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER
                + " = " + audioId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Audio audio = null;
        if (cursor.moveToFirst()) {
            audio = getAudiosList(context, cursor).get(0);
        }
        cursor.close();
        db.close();
        return audio;
    }

    public static List<Memories> getAudioMemoriesForJourney(Context context, String journeyId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.VOICE_COLUMN_JID + " = '"
                + journeyId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Memories> audioList = getAudioMemoriesList(context, cursor);
        cursor.close();
        db.close();
        Log.d(TAG, "audios fetched successfully " + audioList.size());
        return audioList;
    }

    public static void updateServerIdAndUrl(Context context, String audioId, String serverId, String serverUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER, serverId);
        values.put(MySQLiteHelper.VOICE_COLUMN_DATASERVERURL, serverUrl);
        db.update(MySQLiteHelper.TABLE_AUDIO, values, MySQLiteHelper.VOICE_COLUMN_ID + " = " + audioId, null);
        db.close();
    }

    public static void updateDataLocalUrl(Context context, String audioId, String localUrl) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VOICE_COLUMN_DATALOCALURL, localUrl);
        db.update(MySQLiteHelper.TABLE_AUDIO, values, MySQLiteHelper.VOICE_COLUMN_ID + " = " + audioId, null);
        db.close();
    }

    public static void deleteAudioByServerId(Context context, String idOnServer){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_AUDIO, MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER + "=?", new String[]{idOnServer});
        db.close();
    }

    public static void deleteAllAudioFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_AUDIO, MySQLiteHelper.VOICE_COLUMN_JID + "=?", new String[]{journeyId});
        db.close();
    }

    public static void deleteAllAudioFromJourneyByUser(Context context, String journeyId, String userId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_AUDIO, MySQLiteHelper.VOICE_COLUMN_JID + "=? AND " + MySQLiteHelper.VOICE_COLUMN_CREATEDBY
                        + "=?", new String[]{journeyId, userId});
        db.close();
    }

    public static void updateDeleteStatus(Context context, String memLocalId, boolean isDeleted){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.VOICE_COLUMN_IS_DELETED, isDeleted ? 1 : 0);
        db.update(MySQLiteHelper.TABLE_AUDIO, values, MySQLiteHelper.VOICE_COLUMN_ID + " = " + memLocalId, null);
        db.close();
    }

    public static Audio getRandomAudioFromJourney(String jId, Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_AUDIO + " WHERE " + MySQLiteHelper.VOICE_COLUMN_JID +
                " = '" + jId + "' AND " + MySQLiteHelper.VOICE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Log.e(TAG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        int randomNum = 0;
        Audio randomAudio = null;
        int count = c.getCount();

        if (count > 0) {
            Random r = new Random();
            randomNum = r.nextInt(count);
        }

        if (c.moveToFirst() && c.moveToPosition(randomNum)) {
            randomAudio = getAudiosList(context, c).get(0);
        }
        c.close();
        db.close();

        return randomAudio;
    }

    private static List<Memories> getAudioMemoriesList(Context context, Cursor cursor) {
        List<Memories> audioList = new ArrayList<Memories>();
        cursor.moveToFirst();
        Audio audio;
        while (!cursor.isAfterLast()) {
            audio = new Audio();
            audio.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_ID)));
            audio.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER)));
            audio.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_JID)));
            audio.setExtension(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_EXT)));
            audio.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_MEM_TYPE)));
            audio.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_SIZE)));
            audio.setDataServerURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DATASERVERURL)));
            audio.setDataLocalURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DATALOCALURL)));
            audio.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_CREATEDBY)));
            audio.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_CREATEDAT)));
            audio.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_UPDATEDAT)));
            audio.setLikes(LikeDataSource.getLikesForMemory(context, audio.getId(), HelpMe.AUDIO_TYPE));
            audio.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LATITUDE)));
            audio.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LONGITUDE)));
            audio.setAudioDuration(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DURATION)));
            audioList.add(audio);
            cursor.moveToNext();
        }
        return audioList;
    }

    private static List<Audio> getAudiosList(Context context, Cursor cursor) {
        List<Audio> audioList = new ArrayList<Audio>();
        cursor.moveToFirst();
        Audio audio;
        while (!cursor.isAfterLast()) {
            audio = new Audio();
            audio.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_ID)));
            audio.setIdOnServer(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_ID_ONSERVER)));
            audio.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_JID)));
            audio.setExtension(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_EXT)));
            audio.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_MEM_TYPE)));
            audio.setSize(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_SIZE)));
            audio.setDataServerURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DATASERVERURL)));
            audio.setDataLocalURL(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DATALOCALURL)));
            audio.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_CREATEDBY)));
            audio.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_CREATEDAT)));
            audio.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_UPDATEDAT)));
            audio.setLikes(LikeDataSource.getLikesForMemory(context, audio.getId(), HelpMe.AUDIO_TYPE));
            audio.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LATITUDE)));
            audio.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_LONGITUDE)));
            audio.setAudioDuration(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.VOICE_COLUMN_DURATION)));
            audioList.add(audio);
            cursor.moveToNext();
        }

        return audioList;
    }

    // Getting single contact
    public Audio getAudioById(String id, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_AUDIO, null, MySQLiteHelper.VOICE_COLUMN_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Audio audio = getAudiosList(context, cursor).get(0);
        cursor.close();
        db.close();
        return audio;
    }

}
