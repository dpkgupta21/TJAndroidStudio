package com.example.memories.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.memories.SQLitedatabase.ContactDataSource;
import com.example.memories.models.Contact;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by ankit on 20/5/15.
 */
public class PullBuddies {

    private static final String TAG = "PULL_BUDDIES_SERVICE";
    List<String> mBuddyIds;
    private int noRequests;

    private OnTaskFinishListener mListner;
    private Context mContext;

    public PullBuddies(Context context, List<String> buddyIds, OnTaskFinishListener listener) {
        mListner = listener;
        mBuddyIds = buddyIds;
        mContext = context;
        noRequests = buddyIds.size();
    }

    public void fetchBuddies() {
        Log.d(TAG, "fetching profiles" + mBuddyIds.toString() + ",");
        String requestUrl;
        CustomJsonRequest jsonRequest;

        for (String s : mBuddyIds) {
            Log.d(TAG, "fetching profiles for buddies ->" + s + ",");
            requestUrl = Constants.URL_USER_SHOW_DETAILS + "/" + s + "?api_key=" + TJPreferences.getApiKey(mContext);
            jsonRequest = new CustomJsonRequest(Request.Method.GET, requestUrl, null,
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

                                String picServerUrl = response.getJSONObject("user").getJSONObject("profile_picture").getJSONObject("thumb").getString("url");
                                String picLocalUrl;
                                String allJourneyIds = response.getJSONObject("user").getString("journey_ids");
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
                                                        checkPendingRequests();
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
                                    picServerUrl = null;
                                    HelpMe.createImageIfNotExist(mContext);

                                    picLocalUrl = Constants.GUMNAAM_IMAGE_URL;
                                    noRequests--;
                                    checkPendingRequests();
                                }
                                Log.d(TAG, "id = " + idOnServer + "name = " + userName + email + " " + picServerUrl);
                                Contact tempContact = new Contact(idOnServer, userName, email, status, picServerUrl, picLocalUrl,
                                        phone_no, allJourneyIds, true, interests);
                                ContactDataSource.createContact(tempContact, mContext);
                            } catch (JSONException ex) {
                                Log.d(TAG, "exception in parsing buddy received from server" + ex);
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

    private void checkPendingRequests(){
        Log.d(TAG, "no of pending requests -> " + noRequests);
        if(noRequests <= 0){
            Log.d(TAG, "all requests completed now exiting");
            mListner.onFinishTask();
        }
    }

    public interface OnTaskFinishListener{
        void onFinishTask();
    }

}