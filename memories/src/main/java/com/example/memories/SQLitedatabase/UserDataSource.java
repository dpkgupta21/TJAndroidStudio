package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.User;

public class UserDataSource {

    private static final String TAG = "<UserDataSource>";
    // Database fields

    public static long createUser(User newUser, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context)
                .getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_USER_NAME, newUser.getName());
        values.put(MySQLiteHelper.COLUMN_USER_EMAIL, newUser.getPrimaryEmail());
        values.put(MySQLiteHelper.COLUMN_USER_PSWRD, newUser.getPswrd());
        values.put(MySQLiteHelper.COLUMN_USER_JOINED_ON, newUser.getJoinedOn());

        long userId = db.insert(MySQLiteHelper.TABLE_USER, null, values);

        Log.d(TAG, "New User Inserted!");
        db.close();

        return userId;
    }

}
