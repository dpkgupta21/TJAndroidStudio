package com.example.memories.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.utility.Constants;
import com.example.memories.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PullContactsService extends IntentService {

    private static final String TAG = "<PullContactsService>";
    private static int noRequests = -1;
    private ArrayList<Contact> list;
    private ArrayList<String> allPhoneList;
    private ArrayList<String> allEmailList;
    private ResultReceiver mReceiver;

    public PullContactsService() {
        super("PullContactsService");
    }

    public PullContactsService(String name) {
        super(name);
    }

    public static boolean isServiceFinished() {
        Log.d(TAG, "no of requests = " + noRequests);
        return noRequests == 0;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, startId, startId);
        if (intent.hasExtra("RECEIVER")) {
            mReceiver = intent.getParcelableExtra("RECEIVER");
        }
        Log.d(TAG, "on start command");
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        Log.d(TAG, "on Handle Intent");
        getPhoneContactsList();
    }

    private List<Contact> getPhoneContactsList() {
        Log.d(TAG, "started scanning phone contacts");
        Integer nameCount = 0;
        Integer emailCount = 0;
        Integer totalContacts = 0;
        allPhoneList = new ArrayList<>();
        allEmailList = new ArrayList<>();

        list = new ArrayList<Contact>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Log.d(TAG, "total count of the contacts cursor is " + cur.getCount());

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                //String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // If no name present skip that contact
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {
                        //phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //allPhoneList.add(phone);
                        allPhoneList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        nameCount++;
                        break;
                    }
                    pCur.close();
                }

                Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id}, null);

                while (emailCur.moveToNext()) {
//                        emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
//                        allEmailList.add(emailContact);
                    allEmailList.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    emailCount++;
                }
                emailCur.close();

                // if (phone != null || emailContact != null) {

//                    Log.d(TAG, "-------------------------");
//                    Log.d(TAG, "Id :" + totalContacts);

                totalContacts++;
            }
        }

        Log.d(TAG, "phone contacts scanning complete" + " : names count = " + nameCount + " : email count = " + emailCount);

        CheckTJContacts();
        // Collections.sort(list);
        return list;
    }

    private void CheckTJContacts() {
        Log.d(TAG, "Now checking who all are on traveljar");

        Integer phoneLen = allPhoneList.size();
        Integer emailLen = allEmailList.size();
        Map<String, String> jsonParams = new HashMap<String, String>();

        jsonParams.put("phone_count", phoneLen.toString());
        jsonParams.put("email_count", emailLen.toString());

        for (int i = 0; i < phoneLen; i++) {
            jsonParams.put("phone_array[" + i + "]", allPhoneList.get(i).toString());
        }

        for (int i = 0; i < emailLen; i++) {
            jsonParams.put("email_array[" + i + "]", allEmailList.get(i).toString());
        }

        // Tag used to cancel the request
        String tag_json_obj = "CheckTJContacts";

        String url = Constants.URL_CHECK_TJ_CONTACTS;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(
                jsonParams), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());
                try {
                    insertContactsInDB(response);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("User-agent", "My useragent");
                return headers;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void insertContactsInDB(JSONObject response) throws JSONException {
        JSONArray allUsers = response.getJSONArray("users");
        int len = allUsers.length();
        Contact tempContact;
        Log.d(TAG, "Inserting into local DB total users = " + len);
        noRequests = len;

        for (int i = 0; i < len; i++) {
            JSONObject userItem = allUsers.getJSONObject(i);
            final String idOnServer = userItem.getString("id");
            String name = userItem.getString("name");
            String email = userItem.getString("email");
            String phone_no = (userItem.getString("phone") == "null") ? null : userItem.getString("phone");
            String status = userItem.getString("status");
            String picServerUrl = userItem.getJSONObject("profile_picture").getJSONObject("thumb")
                    .getString("url");
            String picLocalUrl;
            String allJourneyIds = userItem.getString("journey_ids");
            String interests = userItem.getString("interests");
            if (picServerUrl != "null") {
                picLocalUrl = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES + idOnServer + ".jpeg";
                ImageRequest request = new ImageRequest(picServerUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                FileOutputStream out = null;
                                try {
                                    File tjDir = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
                                    if (!tjDir.exists()) {
                                        tjDir.mkdirs();
                                    }
                                    String fileName = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES
                                            + idOnServer + ".jpeg";
                                    out = new FileOutputStream(fileName);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    noRequests--;
                                    onFinish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (out != null) {
                                            out.close();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, 0, 0, null, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        noRequests--;
                        onFinish();
                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            } else {

                // check whether the gumnaam image already exists
                noRequests--;
                onFinish();
                picServerUrl = null;
                picLocalUrl = Constants.GUMNAAM_IMAGE_URL;
            }
            Log.d(TAG, "id = " + idOnServer + "name = " + name + email + " " + picServerUrl);
            tempContact = new Contact(idOnServer, name, email, status, picServerUrl, picLocalUrl,
                    phone_no, allJourneyIds, true, interests);
            ContactDataSource.createContact(tempContact, this);
            list.add(tempContact);
        }
    }

    public void onFinish() {
        if (noRequests == 0 && mReceiver != null) {
            Bundle bundle = new Bundle();
            mReceiver.send(0, bundle);
        }
    }

    /*    private static final String TAG = "Pull_contacts_service";
    public PullContactsService() {
        super("name");
    }
    public PullContactsService(String name) {
        super(name);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int count = cur.getCount();
        Log.d(TAG, "total contacts present in the database are " + count);
        Cursor cursor;
        for(int i = 0; i < count; i += 200){
            cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if(cursor.moveToPosition(i)){
                Log.d(TAG, "cursor position is" + cursor.getPosition());
                UploadAsyncTask task = new UploadAsyncTask(this, cursor);
                task.execute();
            }
        }
    }
    private class UploadAsyncTask extends AsyncTask<String, Void, JSONObject> {
        Context context;
        Cursor cursor;
        private ArrayList<String> allPhoneList;
        private ArrayList<String> allEmailList;
        public UploadAsyncTask(Context context, Cursor cursor) {
            this.context = context;
            this.cursor = cursor;
        }
        @Override
        protected JSONObject doInBackground(String... maps) {
            int cursorPos = cursor.getPosition();
            Log.d(TAG, "fetching contacts from cursor position"+cursorPos);
            allPhoneList = new ArrayList<>();
            allEmailList = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            int i = 0;
                do {
                    String id = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                    //Log.d(TAG, "id " + id + " cursor position -> " + cursorPos);
                    //String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    // If no name present skip that contact
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            //phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //allPhoneList.add(phone);
                            allPhoneList.add(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                            break;
                        }
                        pCur.close();
                    }
                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext()) {
                        allEmailList.add(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                    }
                    emailCur.close();
                    i++;
                    Log.d(TAG, "i -> " + i + " " + cursorPos);
                }while (cursor.moveToNext() && i <= 200);
                Log.d(TAG, "successfully fetched contacts for cursor starting from" + cursorPos);
            return null;
        }
        @Override
        protected void onPostExecute(JSONObject object) {
        }
    }*/


}