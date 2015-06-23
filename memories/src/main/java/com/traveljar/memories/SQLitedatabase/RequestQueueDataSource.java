package com.traveljar.memories.SQLitedatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.traveljar.memories.models.Request;

import java.util.ArrayList;
import java.util.List;

public class RequestQueueDataSource {
    private static final String TAG = "<<RequestDataSource>>";

    public static long createRequest(Request newRequest, Context context) {

        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.RQ_COLUMN_ID, newRequest.getId());
        values.put(MySQLiteHelper.RQ_COLUMN_LOCAL_ID, newRequest.getLocalId());
        values.put(MySQLiteHelper.RQ_COLUMN_JOURNEY_ID, newRequest.getJourneyId());
        values.put(MySQLiteHelper.RQ_COLUMN_OPERATION_TYPE, newRequest.getOperationType());
        values.put(MySQLiteHelper.RQ_COLUMN_CATEGORY_TYPE, newRequest.getCategoryType());
        values.put(MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS, newRequest.getRequestStatus());
        long RQ_id = db.insert(MySQLiteHelper.TABLE_REQUEST_QUEUE, null, values);
        Log.d(TAG, "New request Inserted!");

        db.close();
        return RQ_id;
    }

    public static List<Request> getAllRequests(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_REQUEST_QUEUE;
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        List<Request> requestList = parseRequestsFromCursor(cursor);
        cursor.close();
        db.close();
        return requestList;
    }

    private static List<Request> parseRequestsFromCursor(Cursor cursor) {
        List<Request> requestsList = new ArrayList<>();
        cursor.moveToFirst();
        Request request;
        while (!cursor.isAfterLast()) {
            request = new Request();
            request.setId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_ID)));
            request.setLocalId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_LOCAL_ID)));
            request.setJourneyId(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_JOURNEY_ID)));
            request.setOperationType(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_OPERATION_TYPE)));
            request.setCategoryType(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_CATEGORY_TYPE)));
            request.setRequestStatus(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS)));
            requestsList.add(request);
            cursor.moveToNext();
        }
        return requestsList;
    }

    public static Request getFirstNonCompletedRequest(Context context) {
        String selectQuery = "SELECT  * FROM " + MySQLiteHelper.TABLE_REQUEST_QUEUE + " WHERE " + MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS
                + " = '" + Request.REQUEST_STATUS_NOT_STARTED + "'" + " LIMIT 1 ";
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Request> requestList = parseRequestsFromCursor(cursor);
        cursor.close();
        db.close();
        return requestList.size() != 0 ? requestList.get(0) : null;
    }

    public static void updateRequestStatus(Context context, String requestId, int requestStatus) {
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS, requestStatus);
        db.update(MySQLiteHelper.TABLE_REQUEST_QUEUE, values, MySQLiteHelper.RQ_COLUMN_ID + " = " + requestId, null);
        Log.d(TAG, "request status completed successfully ");
        db.close();
    }

    //returns the number of failed requests
    public static int getFailedRequestsCount(Context context){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_REQUEST_QUEUE + " WHERE " + MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS + " ='" +
                Request.REQUEST_STATUS_FAILED + "'";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    // Makes all the failed requests as not completed
    public static void updateStatusOfAllFailedRequests(Context context){
        SQLiteDatabase db = MySQLiteHelper.getInstance(context).getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS, Request.REQUEST_STATUS_NOT_STARTED);
        db.update(MySQLiteHelper.TABLE_REQUEST_QUEUE, values, MySQLiteHelper.RQ_COLUMN_REQUEST_STATUS + " = " + Request.REQUEST_STATUS_FAILED, null);
        db.close();
    }

}
