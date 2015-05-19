package com.example.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.memories.models.Contact;
import com.example.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

public class ContactDataSource {

    private static final String TAG = "<<ContactDataSource>>";

    // ------------------------ "contacts" table methods ----------------//

    public static long createContact(Contact newContact, Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER, newContact.getIdOnServer());
        values.put(MySQLiteHelper.CONTACT_COLUMN_NAME, newContact.getName());
        values.put(MySQLiteHelper.CONTACT_COLUMN_EMAIL, newContact.getPrimaryEmail());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PHONE, newContact.getPhone_no());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_SERVER_URL, newContact.getPicServerUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL, newContact.getPicLocalUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ALL_JIDS, newContact.getAllJourneyIds());
        values.put(MySQLiteHelper.CONTACT_COLUMN_INTERESTS, newContact.getInterests());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ISONBOARD, newContact.isOnBoard() ? 1 : 0);

        // insert row
        long contact_id = db.insert(MySQLiteHelper.TABLE_CONTACT, null, values);
        db.close();

        return contact_id;
    }

    public static List<Contact> getAllContacts(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_CONTACT;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Contact> contacts = getContactsList(cursor, context);
        cursor.close();
        db.close();

        return contacts;
    }

    public static List<Contact> getContactsFromCurrentJourney(Context context) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String[] buddyIds = JourneyDataSource.getContactsFromJourney(context,
                TJPreferences.getActiveJourneyId(context));
        for (int i = 0; i < buddyIds.length; i++) {
            buddyIds[i] = buddyIds[i].trim();
        }
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN ("
                + makePlaceholders(buddyIds.length) + ")";
        Cursor cursor = db.rawQuery(query, buddyIds);
        List<Contact> contactsList = getContactsList(cursor, context);
        Log.d(TAG, "buddies from current journey are " + makePlaceholders(buddyIds.length));
        cursor.close();
        db.close();
        return contactsList;
    }

    public static Contact getContactById(Context context, String buddyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = " + buddyId;
        Cursor cursor = db.rawQuery(query, null);

        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = getContactsList(cursor, context).get(0);
        }
        cursor.close();
        db.close();
        return contact;
    }

    private static List<Contact> getContactsList(Cursor cursor, Context context) {
        List<Contact> contactsList = new ArrayList<Contact>();
        Contact contact;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact();
                contact.setIdOnServer(cursor.getString((cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER))));
                contact.setName((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_NAME))));
                contact.setPrimaryEmail((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_EMAIL))));
                contact.setStatus((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_EMAIL))));
                contact.setPicLocalUrl((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL))));
                contact.setPicServerUrl((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PIC_SERVER_URL))));
                contact.setPhone_no((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PHONE))));
                contact.setAllJourneyIds((cursor.getString(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ALL_JIDS))));
                contact.setOnBoard(cursor.getInt(cursor
                        .getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ISONBOARD)) == 1 ? true
                        : false);

                contactsList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactsList;
    }

    // Method created to use in clause.
    // http://stackoverflow.com/questions/7418849/android-sqlite-in-clause-and-placeholders
    private static String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

}