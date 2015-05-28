package com.example.memories.services;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.utility.Constants;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ankit on 20/5/15.
 */
public class PullBuddiesService extends IntentService {

    private static final String TAG = "PULL_BUDDIES_SERVICE";
    List<String> buddyIds;
    private ResultReceiver mReceiver;
    private int REQUEST_CODE;

    public PullBuddiesService() {
        super("pull buddies service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        fetchBuddies();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mReceiver = intent.getParcelableExtra("RECEIVER");
        REQUEST_CODE = intent.getIntExtra("REQUEST_CODE", 0);
        buddyIds = intent.getStringArrayListExtra("BUDDY_IDS");
        return START_STICKY;
    }

    public void fetchBuddies() {
        String requestUrl;
        CustomJsonRequest jsonRequest;

        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(this));
        for (String s : buddyIds) {
            requestUrl = "https://www.traveljar.in/api/v1/users/" + s;
            jsonRequest = new CustomJsonRequest(Request.Method.POST, requestUrl, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "buddy fetched successfully" + response);
                            try {
                                final String idOnServer = response.getJSONObject("user").getString("id");
                                String userName = response.getJSONObject("user").getString("name");
                                String email = response.getJSONObject("user").getString("email");
                                String status = response.getJSONObject("user").getString("status");
                                String interests = response.getJSONObject("user").getString("interests");
                                String phone_no = response.getJSONObject("user").getString("phone");
                                String picServerUrl = response.getJSONObject("profile_picture").getJSONObject("thumb")
                                        .getString("url");
                                String picLocalUrl;
                                String allJourneyIds = response.getString("journey_ids");
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
                                Log.d(TAG, "id = " + idOnServer + "name = " + userName + email + " " + picServerUrl);
                                Contact tempContact = new Contact(idOnServer, userName, email, status, picServerUrl, picLocalUrl,
                                        phone_no, allJourneyIds, true, interests);
                                ContactDataSource.createContact(tempContact, PullBuddiesService.this);
                            } catch (Exception ex) {
                                Log.d(TAG, "exception in parsing note received from server" + ex);
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "error in uploading note" + error);
                }
            });
            AppController.getInstance().addToRequestQueue(jsonRequest);
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "ondestroy() method called");
        Bundle bundle = new Bundle();
        mReceiver.send(REQUEST_CODE, bundle);
        super.onDestroy();
    }

}
