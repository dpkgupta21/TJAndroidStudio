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
        long contact_id = 0;
        try {
            contact_id = db.insertOrThrow(MySQLiteHelper.TABLE_CONTACT, null, values);
        }catch (Exception ex){
            Log.d(TAG, "contact already exists so not inserting excccceptiononon");
        }

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

    public static List<String> getNonExistingContacts(Context context, List<String> contactIds) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        List<String> existingContacts = new ArrayList<String>();
        String query = "SELECT " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " FROM " + MySQLiteHelper.TABLE_CONTACT +
                " WHERE " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN (" + makePlaceholders(contactIds.size()) + ")";
        String[] array = new String[contactIds.size()];
        Cursor cursor = db.rawQuery(query, contactIds.toArray(array));
        String contactId;
        if (cursor.moveToFirst()) {
            do {
                contactId = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER));
                existingContacts.add(contactId);
            } while (cursor.moveToNext());
        }
        List<String> nonExistingContactsList = new ArrayList<String>();
        for (String id : contactIds) {
            if (!existingContacts.contains(id)) {
                nonExistingContactsList.add(id);
            }
        }
        return nonExistingContactsList;
    }

    // This method will take a list of contact Ids and return a List of Contacts corresponding to those Ids
    public static List<Contact> getContactsListFromIds(Context context, List<String> contactIds) {
        Log.d(TAG, "fetching contacts list corresponding to contact ids list");
        String[] ids = new String[contactIds.size()];
        ids = contactIds.toArray(ids);
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN ("
                + makePlaceholders(ids.length) + ")";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(query, ids);
        List<Contact> contactsList = getContactsList(cursor, context);
        Log.d(TAG, "buddies from current journey are " + contactsList.toString());
        cursor.close();
        db.close();
        return contactsList;
    }

    public static List<Contact> getContactsFromCurrentJourney(Context context) {
        Log.d(TAG, "fetch contacts from current journey");
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        String[] buddyIds = JourneyDataSource.getBuddyIdsFromJourney(context,
                TJPreferences.getActiveJourneyId(context));
        for (int i = 0; i < buddyIds.length; i++) {
            buddyIds[i] = buddyIds[i].trim();
        }
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN ("
                + makePlaceholders(buddyIds.length) + ")";
        Cursor cursor = db.rawQuery(query, buddyIds);
        List<Contact> contactsList = getContactsList(cursor, context);
        Log.d(TAG, "buddies from current journey are " + contactsList.toString());
        cursor.close();
        db.close();
        return contactsList;
    }

    public static Contact getContactById(Context context, String buddyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = '" + buddyId + "'";
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