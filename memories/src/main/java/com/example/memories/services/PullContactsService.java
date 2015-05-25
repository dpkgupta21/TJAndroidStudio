package com.example.memories.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.utility.Constants;
import com.example.memories.volley.AppController;
import com.example.memories.volley.Const;

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


    private ResultReceiver mReceiver;
    private int REQUEST_CODE;
    private static final String TAG = "<PullContactsService>";
    private ArrayList<Contact> list;
    private ArrayList<String> allEmailPhoneList;


    public PullContactsService() {
        super("PullContactsService");
    }

    public PullContactsService(String name) {
        super(name);
    }

    public int onStartCommand(Intent intent, int flags, int startId){
        mReceiver = intent.getParcelableExtra("RECEIVER");
        REQUEST_CODE = intent.getIntExtra("REQUEST_CODE", 0);
        super.onStartCommand(intent, startId, startId);
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
        Integer noNameCount = 0;
        Integer nameCount = 0;
        Integer totalContacts = 0;
        allEmailPhoneList = new ArrayList<String>();

        list = new ArrayList<Contact>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String phone = null;
                String emailContact = null;
                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                String name = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                // If no name present skip that contact
                if (name != null && !name.isEmpty()) {

                    if (Integer.parseInt(cur.getString(cur
                            .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            phone = pCur.getString(pCur
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            break;
                        }
                        pCur.close();
                    }

                    Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (emailCur.moveToNext()) {
                        emailContact = emailCur.getString(emailCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    }
                    emailCur.close();

                    if (phone != null || emailContact != null) {
                        // if (emailContact != null) {
                        allEmailPhoneList.add((emailContact != null) ? emailContact : phone);
                        // allEmailPhoneList.add(emailContact);

                        /*Log.d(TAG, "-------------------------");
                        Log.d(TAG, "Id :" + nameCount);
                        Log.d(TAG, "Name : " + name);
                        Log.d(TAG, "Phone : " + phone);
                        Log.d(TAG, "Email : " + emailContact);*/
                        nameCount++;
                    } else {
                        noNameCount++;
                    }
                } else {
                    // noNameCount++;
                }
                totalContacts++;
            }
        }
        Log.d(TAG, "phone contacts scanning complete");
        Log.d(TAG, "statistics : Total contacts = " + totalContacts + "no names = " + noNameCount
                + "names count" + nameCount);

        CheckTJContacts();
        // Collections.sort(list);
        return list;
    }

    private void CheckTJContacts() {
        Log.d(TAG, "Now checking who all are on traveljar");

        Integer len = allEmailPhoneList.size();
        Map<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("api_key", "0v6xW0bp0nUjsuLdOrxd1A");
        jsonParams.put("count", len.toString());

        for (int i = 0; i < len; i++) {
            jsonParams.put("array[" + i + "]", allEmailPhoneList.get(i).toString());
        }

        // Tag used to cancel the request
        String tag_json_obj = "CheckTJContacts";

        String url = Const.URL_CHECK_TJ_CONTACTS;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, url, new JSONObject(
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

        for (int i = 0; i < len; i++) {
            JSONObject userItem = allUsers.getJSONObject(i);
            final String idOnServer = userItem.getString("id");
            String name = userItem.getString("name");
            String email = userItem.getString("email");
            String phone_no = userItem.getString("phone");
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
                    }
                });
                AppController.getInstance().addToRequestQueue(request);
            } else {

                // check whether the gumnaam image already exists
                if (!(new File(Constants.GUMNAAM_IMAGE_URL)).exists()) {
                    //check whether the dir exists
                    File dir = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
                    File file = new File(Constants.GUMNAAM_IMAGE_URL);
                    FileOutputStream outStream = null;
                    try {
                        outStream = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                        outStream.flush();
                        outStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

    @Override
    public void onDestroy(){
        Log.d(TAG, "ondestroy() method called");
        Bundle bundle = new Bundle();
        mReceiver.send(REQUEST_CODE, bundle);
        super.onDestroy();
    }

}