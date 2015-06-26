package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.common.base.Joiner;
import com.traveljar.memories.models.Memories;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.utility.HelpMe;

import java.util.ArrayList;
import java.util.List;

public class NoteDataSource {

    private static final String TAG = "<NoteDataSource>";

    public static long createNote(Note newNote, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER, newNote.getIdOnServer());
        values.put(MySQLiteHelper.NOTES_COLUMN_JID, newNote.getjId());
        values.put(MySQLiteHelper.NOTES_COLUMN_MEM_TYPE, newNote.getMemType());
        values.put(MySQLiteHelper.NOTES_COLUMN_CAPTION, newNote.getCaption());
        values.put(MySQLiteHelper.NOTES_COLUMN_CONTENT, newNote.getContent());
        values.put(MySQLiteHelper.NOTES_COLUMN_CREATED_BY, newNote.getCreatedBy());
        values.put(MySQLiteHelper.NOTES_COLUMN_CREATED_AT, newNote.getCreatedAt());
        values.put(MySQLiteHelper.NOTES_COLUMN_UPDATED_AT, newNote.getUpdatedAt());
        values.put(MySQLiteHelper.NOTES_COLUMN_LATITUDE, newNote.getLatitude());
        values.put(MySQLiteHelper.NOTES_COLUMN_LONGITUDE, newNote.getLongitude());

        long note_id = db.insert(MySQLiteHelper.TABLE_NOTES, null, values);
        Log.d(TAG, "New note Inserted! with id " + note_id);

        db.close();

        return note_id;
    }

    // To get total number of notes of a journey
    public static int getNoteCountOfJourney(Context context, String jId) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_NOTES + " WHERE " + MySQLiteHelper.NOTES_COLUMN_JID +
                " = '" + jId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        Log.d(TAG, selectQuery);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public static Note getNoteByServerId(String id, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_NOTES, null, MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Log.d(TAG, "get note  with server id = " + id);
        Note note = (Note)parseNotesFromCursor(context, cursor).get(0);
        cursor.close();
        db.close();
        return note;
    }

    public static Note getNoteById(String id, Context context) {
        Log.d(TAG, "1.1 " + "note local id = " + id);
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_NOTES, null, MySQLiteHelper.NOTES_COLUMN_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        Note note = (Note)parseNotesFromCursor(context, cursor).get(0);
        cursor.close();
        db.close();
        return note;
    }

    public static void updateServerId(Context context, String noteId, String serverId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER, serverId);
        db.update(MySQLiteHelper.TABLE_NOTES, values, MySQLiteHelper.NOTES_COLUMN_ID + " = " + noteId, null);
        Log.d(TAG, "note updated for note id on server = " + serverId + "local id = " + noteId);

        db.close();
    }

    public static void deleteNoteOnServer(Context context, String idOnServer){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_NOTES, MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER + "=?", new String[]{idOnServer});
        db.close();
    }

    public static List<Memories> getAllNotesList(Context context, String journeyId) {

        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_NOTES + " WHERE " + MySQLiteHelper.NOTES_COLUMN_JID + " = '"
                + journeyId + "' AND " + MySQLiteHelper.PICTURE_COLUMN_IS_DELETED + " ='0'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Memories> notesList = parseNotesFromCursor(context, cursor);
        cursor.close();
        db.close();

        return notesList;
    }

    public static void updateFavourites(Context context, String memId, List<String> likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_LIKED_BY, likedBy == null ? null : Joiner.on(",").join(likedBy));
        db.update(MySQLiteHelper.TABLE_NOTES, values, MySQLiteHelper.NOTES_COLUMN_ID + " = " + memId, null);
        db.close();
    }

    public static void updateDeleteStatus(Context context, String memLocalId, boolean isDeleted){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_IS_DELETED, isDeleted ? 1 : 0);
        db.update(MySQLiteHelper.TABLE_NOTES, values, MySQLiteHelper.NOTES_COLUMN_ID + " = " + memLocalId, null);
        db.close();
    }

    public static void deleteAllNotesFromJourney(Context context, String journeyId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_NOTES, MySQLiteHelper.NOTES_COLUMN_JID + "=?", new String[]{journeyId});
        db.close();
    }

    public static void deleteAllNoteFromJourneyByUser(Context context, String journeyId, String userId){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        db.delete(MySQLiteHelper.TABLE_NOTES, MySQLiteHelper.NOTES_COLUMN_JID + "=? AND " + MySQLiteHelper.NOTES_COLUMN_CREATED_BY
                + "=?", new String[]{journeyId, userId});
        db.close();
    }

    public static List<Memories> parseNotesFromCursor(Context context, Cursor cursor){
        Note note;
        List<Memories> notesList = new ArrayList<Memories>();
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                note = new Note();
                note.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID)));
                note.setIdOnServer(cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER)));
                note.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_JID)));
                note.setMemType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_MEM_TYPE)));
                note.setCaption(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CAPTION)));
                note.setContent(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CONTENT)));
                note.setCreatedBy(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_BY)));
                note.setCreatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_AT)));
                note.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_UPDATED_AT)));
                note.setLikes(LikeDataSource.getLikesForMemory(context, note.getId(), HelpMe.NOTE_TYPE));

                note.setLatitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_LATITUDE)));
                note.setLongitude(cursor.getDouble(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_LONGITUDE)));
                notesList.add(note);
                cursor.moveToNext();
            }
        }
        return notesList;
    }

}
