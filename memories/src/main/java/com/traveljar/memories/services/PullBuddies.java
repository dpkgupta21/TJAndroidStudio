package com.traveljar.memories.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.customevents.ContactsFetchEvent;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.greenrobot.event.EventBus;

public class PullBuddies {

    private static final String TAG = "PULL_BUDDIES_SERVICE";
    List<String> mBuddyIds;
    private int noRequests;

    private Context mContext;

    //code of activity which is calling for the event
    private final int EVENT_LISTENER_CODE;

    public PullBuddies(Context context, List<String> buddyIds, int listenerCode) {
        mBuddyIds = buddyIds;
        mContext = context;
        noRequests = buddyIds.size();
        EVENT_LISTENER_CODE = listenerCode;
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
                                String phoneBookName = HelpMe.getContactNameFromNumber(mContext, phone_no);

                                String picServerUrl = response.getJSONObject("user").getJSONObject("profile_picture").getJSONObject("thumb").getString("url");
                                String picLocalUrl;
                                String allJourneyIds = response.getJSONObject("user").getString("journey_ids");
                                if (!picServerUrl.equals("null")) {
                                    picLocalUrl = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES + idOnServer + ".jpeg";
                                    ImageRequest request = new ImageRequest(picServerUrl,
                                            new Response.Listener<Bitmap>() {
                                                @Override
                                                public void onResponse(Bitmap bitmap) {
                                                    FileOutputStream out = null;
                                                    try {
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
                                            // In case of server error send back the event with success false
                                            EventBus.getDefault().post(new ContactsFetchEvent("Contacts Fetched Successfully", EVENT_LISTENER_CODE, true));
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
                                Contact tempContact = new Contact(idOnServer, userName, phoneBookName, email, status, picServerUrl, picLocalUrl,
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

    private void checkPendingRequests() {
        Log.d(TAG, "no of pending requests -> " + noRequests);
        if (noRequests <= 0) {
            Log.d(TAG, "all requests completed now exiting");
            EventBus.getDefault().post(new ContactsFetchEvent("Contacts Fetched Successfully", EVENT_LISTENER_CODE, true));
        }
    }
}