package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Memories;
import com.example.memories.models.Note;

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
        values.put(MySQLiteHelper.NOTES_COLUMN_LIKED_BY, newNote.getLikedBy());

        long note_id = db.insert(MySQLiteHelper.TABLE_NOTES, null, values);
        Log.d(TAG, "New note Inserted!");

        db.close();

        return note_id;
    }

    public static Note getNote(String id, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.query(MySQLiteHelper.TABLE_NOTES, null, MySQLiteHelper.NOTES_COLUMN_ID
                + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Note note = new Note();
        note.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID)));
        note.setIdOnServer(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER)));
        note.setCreatedBy(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_BY)));
        note.setCreatedAt(cursor.getLong(cursor
                .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_AT)));
        note.setUpdatedAt(cursor.getLong(cursor
                .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_UPDATED_AT)));
        note.setLikedBy(cursor.getString(cursor
                .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_LIKED_BY)));
        note.setContent(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CONTENT)));
        note.setjId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_JID)));
        cursor.close();
        db.close();
        return note;

    }

    public static void updateServerId(Context context, String noteId, String serverId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER, serverId);
        db.update(MySQLiteHelper.TABLE_NOTES, values, MySQLiteHelper.NOTES_COLUMN_ID + " = " + noteId, null);
        db.close();
    }

    public static List<Memories> getAllNotesList(Context context, String journeyId) {
        List<Memories> notesList = new ArrayList<Memories>();
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_NOTES + " WHERE "
                + MySQLiteHelper.NOTES_COLUMN_JID + " = " + journeyId;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        Log.d(TAG, "cursor length" + c.getCount() + journeyId);
        c.moveToFirst();
        Note note;
        while (!c.isAfterLast()) {
            note = new Note();

            note.setId(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID)));
            note.setIdOnServer(c.getString(c
                    .getColumnIndex(MySQLiteHelper.NOTES_COLUMN_ID_ONSERVER)));
            note.setjId(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_JID)));
            note.setMemType(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_MEM_TYPE)));
            note.setCaption(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CAPTION)));
            note.setContent(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CONTENT)));
            note.setCreatedBy(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_BY)));
            note.setCreatedAt(c.getLong(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_CREATED_AT)));
            note.setUpdatedAt(c.getLong(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_UPDATED_AT)));
            note.setLikedBy(c.getString(c.getColumnIndex(MySQLiteHelper.NOTES_COLUMN_LIKED_BY)));

            notesList.add(note);
            c.moveToNext();
        }
        c.close();
        db.close();

        return notesList;
    }

    public static void updateFavourites(Context context, String memId, String likedBy) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.NOTES_COLUMN_LIKED_BY, likedBy);
        db.update(MySQLiteHelper.TABLE_NOTES, values, MySQLiteHelper.NOTES_COLUMN_ID + " = " + memId, null);
        db.close();
    }

}
