package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Contact;
import com.traveljar.memories.utility.TJPreferences;

import java.util.ArrayList;
import java.util.List;

public class ContactDataSource {

    private static final String TAG = "<<ContactDataSource>>";

    // ------------------------ "contacts" table methods ----------------//

    public static long createContact(Contact contact, Context context) {

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER, contact.getIdOnServer());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PROFILE_NAME, contact.getProfileName());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PHONEBOOK_NAME, contact.getPhoneBookName());
        values.put(MySQLiteHelper.CONTACT_COLUMN_EMAIL, contact.getPrimaryEmail());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PHONE, contact.getPhoneNo());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_SERVER_URL, contact.getPicServerUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL, contact.getPicLocalUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ALL_JIDS, contact.getAllJourneyIds());
        values.put(MySQLiteHelper.CONTACT_COLUMN_INTERESTS, contact.getInterests());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ISONBOARD, contact.isOnBoard() ? 1 : 0);
        values.put(MySQLiteHelper.CONTACT_COLUMN_STATUS, contact.getStatus());

        long contact_id;
        //Check if the contact is already present in the database
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Contact c = ContactDataSource.getContactById(context, contact.getIdOnServer());
        if(c == null){
            //contact is not present on the database so create a new one
            Log.d(TAG, "contact is not present on the database so create a new one" + contact);
            contact_id = db.insert(MySQLiteHelper.TABLE_CONTACT, null, values);
            Log.d(TAG, "contact id after saving is " + contact_id);
        }else {
            //contact is already present on the database so updating the existing one
            Log.d(TAG, "contact is already present on the database so updating the existing one");
            db.update(MySQLiteHelper.TABLE_CONTACT, values, MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = " + contact.getIdOnServer(), null);
            contact_id = Long.parseLong(contact.getIdOnServer());
        }
        db.close();
        return contact_id;
    }

    public static void updateContact(Context context, Contact contact){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER, contact.getIdOnServer());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PROFILE_NAME, contact.getProfileName());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PHONEBOOK_NAME, contact.getPhoneBookName());
        values.put(MySQLiteHelper.CONTACT_COLUMN_EMAIL, contact.getPrimaryEmail());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PHONE, contact.getPhoneNo());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_SERVER_URL, contact.getPicServerUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL, contact.getPicLocalUrl());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ALL_JIDS, contact.getAllJourneyIds());
        values.put(MySQLiteHelper.CONTACT_COLUMN_INTERESTS, contact.getInterests());
        values.put(MySQLiteHelper.CONTACT_COLUMN_ISONBOARD, contact.isOnBoard() ? 1 : 0);
        values.put(MySQLiteHelper.CONTACT_COLUMN_STATUS, contact.getStatus());

        db.update(MySQLiteHelper.TABLE_CONTACT, values, MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = '" + contact.getIdOnServer()
                + "'", null);
        db.close();

    }

    public static List<Contact> getAllContacts(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_CONTACT;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Contact> contacts = getContactsListFromCursor(cursor, context);

        // If contacts contains own contact remove itgetAllContacts
        int i = -1;
        for (Contact contact : contacts) {
            if (contact.getIdOnServer().equals(TJPreferences.getUserId(context))) {
                i++;
                break;
            }
            i++;
        }
        if (i > -1) {
            contacts.remove(i);
        }

        cursor.close();
        db.close();

        return contacts;
    }

//    Using mapping table
    public static List<Contact> getAllContactsFromJourney(Context context, String journeyId){
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " INNER JOIN " + MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP +
                " ON " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = " + MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP +
                "." + MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID + " WHERE " + MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID +
                " = '" + journeyId + "'";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "cursor size is " + cursor.getCount());
        List<Contact> contactList = getContactsListFromCursor(cursor, context);
        cursor.close();
        db.close();
        return contactList;
    }

    //    Using mapping table
    public static List<Contact> getAllActiveContactsFromJourney(Context context, String journeyId){
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " INNER JOIN " + MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP +
                " ON " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = " + MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP +
                "." + MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID + " WHERE " + MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID +
                " = '" + journeyId + "' AND " + MySQLiteHelper.MAPPING_COLUMN_IS_USER_ACTIVE + " = 1";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d(TAG, "cursor size is " + cursor.getCount());
        List<Contact> contactList = getContactsListFromCursor(cursor, context);
        cursor.close();
        db.close();
        return contactList;
    }

    public static void updateContactJourneyStatus(Context context, String userId, String journeyId,  boolean status){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.MAPPING_COLUMN_IS_USER_ACTIVE, status ? "1" : "0");
        Log.d(TAG, "values are " + values);
        db.update(MySQLiteHelper.TABLE_CONTACT_JOURNEY_MAP, values, MySQLiteHelper.MAPPING_COLUMN_CONTACT_ID + " = '" + userId
                + "' AND " + MySQLiteHelper.MAPPING_COLUMN_JOURNEY_ID + " = '" + journeyId + "'", null);
        db.close();
    }

    public static List<String> getNonExistingContacts(Context context, List<String> contactIds) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        List<String> existingContacts = new ArrayList<>();
        List<String> nonExistingContactsList = new ArrayList<>();
        String query = "SELECT " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " FROM " + MySQLiteHelper.TABLE_CONTACT +
                " WHERE " + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN (" + makePlaceholders(contactIds.size()) + ")";

        String[] array = new String[contactIds.size()];
        Cursor cursor = db.rawQuery(query, contactIds.toArray(array));
        String contactId;
        Log.d(TAG, "contacts from journey are " + contactIds);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "cursor contact id = " + cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER)));
                    contactId = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER));
                    existingContacts.add(contactId);
                } while (cursor.moveToNext());
            }
        }

        for (String id : contactIds) {
            if (!existingContacts.contains(id)) {
                nonExistingContactsList.add(id);
            }
        }

        Log.d(TAG, "non existing contacts list" + nonExistingContactsList);
        cursor.close();
        db.close();
        return nonExistingContactsList;
    }

    public static Contact getContactById(Context context, String buddyId) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = '" + buddyId + "'";
        Cursor cursor = db.rawQuery(query, null);

        Contact contact = null;
        if (cursor.moveToFirst()) {
            contact = getContactsListFromCursor(cursor, context).get(0);
        }
        cursor.close();
        db.close();
        return contact;
    }

    public static void updateContact(Context context, String contactId, String[] columnValues, String... columns) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        int i = 0;
        for (String column : columns) {
            values.put(column, columnValues[i]);
            i++;
        }
        db.update(MySQLiteHelper.TABLE_CONTACT, values, MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " = " + contactId, null);
        db.close();
    }

    private static List<Contact> getContactsListFromCursor(Cursor cursor, Context context) {
        List<Contact> contactsList = new ArrayList<>();
        Contact contact;
        if (cursor.moveToFirst()) {
            do {
                contact = new Contact();
                contact.setIdOnServer(cursor.getString((cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER))));
                contact.setProfileName((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PROFILE_NAME))));
                contact.setPhoneBookName((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PHONEBOOK_NAME))));
                contact.setPrimaryEmail((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_EMAIL))));
                contact.setStatus((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_EMAIL))));
                contact.setPicLocalUrl((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PIC_LOCAL_URL))));
                contact.setPicServerUrl((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PIC_SERVER_URL))));
                contact.setPhoneNo((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_PHONE))));
                contact.setAllJourneyIds((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ALL_JIDS))));
                contact.setOnBoard(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_ISONBOARD)) == 1);
                contact.setStatus((cursor.getString(cursor.getColumnIndex(MySQLiteHelper.CONTACT_COLUMN_STATUS))));

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

    // This method will take a list of contact Ids and return a List of Contacts corresponding to those Ids
    public static List<Contact> getContactsListFromIds(Context context, List<String> contactIds) {
        String[] ids = new String[contactIds.size()];
        ids = contactIds.toArray(ids);
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN ("
                + makePlaceholders(ids.length) + ")";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(query, ids);
        List<Contact> contactsList = getContactsListFromCursor(cursor, context);
        cursor.close();
        db.close();
        return contactsList;
    }

    public static List<Contact> getContactsFromJourney(Context context, String jId) {
        Log.d(TAG, "in getContactsFromJourney");
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();
        List<Contact> contactsList = new ArrayList<>();

        List<String> buddyIds = JourneyDataSource.getBuddyIdsFromJourney(context, jId);
        Log.d(TAG, "sie = " + buddyIds.size() + buddyIds.toString());

        // CHeck if there are any buddies. If not just return
        if (buddyIds == null || buddyIds.size() == 0) {
            return contactsList;
        }

        String[] buddyIdsStringList = new String[buddyIds.size()];
        buddyIdsStringList = buddyIds.toArray(buddyIdsStringList);

        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_CONTACT + " WHERE "
                + MySQLiteHelper.CONTACT_COLUMN_ID_ONSERVER + " IN ("
                + makePlaceholders(buddyIdsStringList.length) + ")";
        Log.d(TAG, query + buddyIdsStringList[0]);
        Cursor cursor = db.rawQuery(query, buddyIdsStringList);

        contactsList = getContactsListFromCursor(cursor, context);
        Log.d(TAG, "buddies from current journey are " + contactsList.size() + cursor.getCount());

        cursor.close();
        db.close();
        return contactsList;
    }

}