package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Joiner;
import com.traveljar.memories.models.Memories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemoriesDataSource {

    private static final String TAG = "<MemoriesDS>";

    public static List<Memories> getAllMemoriesList(Context context, String journeyId) {
        List<Memories> memoriesList = new ArrayList<>();
        memoriesList.addAll(PictureDataSource.getPictureMemoriesFromJourney(context, journeyId));
        memoriesList.addAll(AudioDataSource.getAudioMemoriesForJourney(context, journeyId));
        memoriesList.addAll(CheckinDataSource.getAllCheckinsList(context, journeyId));
        memoriesList.addAll(NoteDataSource.getAllNotesList(context, journeyId));
        memoriesList.addAll(VideoDataSource.getAllVideoMemories(context, journeyId));
        memoriesList.addAll(MoodDataSource.getMoodsFromJourney(context, journeyId));
        Collections.sort(memoriesList);

        Log.d(TAG, "total memories =" + memoriesList.size());
        return memoriesList;
    }


    public static void deleteAllMemoriesCreatedByUser(Context context, String userId, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_AUDIO, MySQLiteHelper.VIDEO_COLUMN_CREATED_BY + "=? AND " + MySQLiteHelper.VOICE_COLUMN_JID
                + " = '" + journeyId + "'", new String[]{userId});
        db.delete(MySQLiteHelper.TABLE_CHECKIN, MySQLiteHelper.CHECKIN_COLUMN_CREATED_BY + "=?", new String[]{userId});
        db.delete(MySQLiteHelper.TABLE_MOOD, MySQLiteHelper.MOOD_COLUMN_LONGITUDE + "=?", new String[]{userId});
        db.delete(MySQLiteHelper.TABLE_NOTES, MySQLiteHelper.NOTES_COLUMN_CREATED_BY + "=?", new String[]{userId});
        db.delete(MySQLiteHelper.TABLE_PICTURE, MySQLiteHelper.PICTURE_COLUMN_CREATEDBY + "=?", new String[]{userId});
        db.delete(MySQLiteHelper.TABLE_VIDEO, MySQLiteHelper.VIDEO_COLUMN_CREATED_BY + "=?", new String[]{userId});
        db.close();
    }

    public static void removeUserFromMemories(Context context, String userId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String [] tableNames = new String[]{MySQLiteHelper.TABLE_CHECKIN, MySQLiteHelper.TABLE_MOOD};
        String [] buddyColumnNames = new String[]{MySQLiteHelper.CHECKIN_COLUMN_WITH, MySQLiteHelper.MOOD_COLUMN_FRIENDS_ID};
        String [] columnIdNames = new String[]{MySQLiteHelper.CHECKIN_COLUMN_ID, MySQLiteHelper.MOOD_COLUMN_ID};
        int i = 0;
        Cursor cursor;
        for(String tableName : tableNames) {
            String selectQuery = "SELECT * " + " FROM " + tableName +
                    " WHERE " + buddyColumnNames[i] + " LIKE " + "'%" + userId + "%'";
            cursor = db.rawQuery(selectQuery, null);
            String buddyIds = "";
            String id;
            if (cursor.moveToFirst()) {
                do{
                    id = cursor.getString(cursor.getColumnIndex(columnIdNames[i]));
                    buddyIds = cursor.getString(cursor.getColumnIndex(buddyColumnNames[i]));
                    List<String> buddiesList = new ArrayList<>(Arrays.asList(buddyIds.split(",")));
                    buddiesList.remove(userId);
                    buddyIds = Joiner.on(",").join(buddiesList);

                    Log.d(TAG, "contact after removing contact is " + buddyIds);
                    cursor.close();
                    //Update this buddyIds on the database
                    ContentValues values = new ContentValues();
                    values.put(buddyColumnNames[i], buddyIds);
                    db.update(tableName, values, columnIdNames[i] + " = " + id, null);
                }while (cursor.moveToNext());
                cursor.close();

            }
            Log.d(TAG, "existing contact for journey " + buddyIds);
            i++;
        }
        db.close();
        Log.d(TAG, "user successfully added to the current journey");
    }

    public static Memories getMemoryFromTypeAndId(Context context, String idOnServer, String memType){
        switch (Integer.parseInt(memType)){
            case 1:
                return AudioDataSource.getAudioByServerId(context, idOnServer);
            case 2:
                return CheckinDataSource.getCheckInByIdOnServer(idOnServer, context);
            case 3:
                return MoodDataSource.getMoodByIdOnServer(idOnServer, context);
            case 4:
                return NoteDataSource.getNoteByServerId(idOnServer, context);
            case 5:
                return PictureDataSource.getPictureByIdOnServer(context, idOnServer);
            case 6:
                return VideoDataSource.getVideoByIdOnServer(idOnServer, context);
        }
        return null;
    }

    public static void deleteMemoryWithServerId(Context context, String memType, String idOnServer){
        switch (Integer.parseInt(memType)){
            case 1:
                AudioDataSource.deleteAudioByServerId(context, idOnServer);
            case 2:
                CheckinDataSource.deleteCheckInByServerId(context, idOnServer);
            case 3:
                MoodDataSource.deleteMoodByServerId(context, idOnServer);
            case 4:
                NoteDataSource.deleteNoteOnServer(context, idOnServer);
            case 5:
                PictureDataSource.deletePictureByIdOnServer(context, idOnServer);
            case 6:
                VideoDataSource.deleteVideoByIdOnServer(context, idOnServer);
        }
    }

}
