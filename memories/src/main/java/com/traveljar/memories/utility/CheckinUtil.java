package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.traveljar.memories.SQLitedatabase.CheckinDataSource;
import com.traveljar.memories.models.CheckIn;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CheckinUtil {
    private static final String TAG = "CHECKIN_UTIL";

    public static void uploadCheckin(final CheckIn checkin, final Context context) {

        String uploadRequestTag = "UPLOAD_CHECKIN";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("checkin[user_id]", checkin.getCreatedBy());
        params.put("checkin[place_name]", checkin.getCheckInPlaceName());
        params.put("checkin[latitude]", String.valueOf(checkin.getLatitude()));
        params.put("checkin[longitude]", String.valueOf(checkin.getLongitude()));
        params.put("checkin[buddies]", checkin.getCheckInWith() == null ? null : checkin.getCheckInWith().toString());
        params.put("checkin[note]", checkin.getCaption());
        params.put("checkin[created_at]", String.valueOf(checkin.getCreatedAt()));
        params.put("checkin[updated_at]", String.valueOf(checkin.getUpdatedAt()));
        Log.d(TAG, "uploading checkin with parameters " + params);

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/checkins";
        Log.d(TAG, "uploading checkin on url " +  url);

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "checkin uploaded successfully" + response);
                        try {
                            String serverId = response.getJSONObject("checkin").getString("id");
                            CheckinDataSource.updateServerId(context.getApplicationContext(), checkin.getId(), serverId);
                        } catch (Exception ex) {
                            Log.d(TAG, "exception in parsing checkin received from server" + ex);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in uploading checkin");
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }

    public static boolean uploadCheckInOnServer(Context context, CheckIn checkIn){
        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/checkins";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("checkin[user_id]", checkIn.getCreatedBy());
        params.put("checkin[place_name]", checkIn.getCheckInPlaceName());
        params.put("checkin[latitude]", String.valueOf(checkIn.getLatitude()));
        params.put("checkin[longitude]", String.valueOf(checkIn.getLongitude()));
        params.put("checkin[buddies]", checkIn.getCheckInWith() == null ? null : checkIn.getCheckInWith().toString());
        params.put("checkin[note]", checkIn.getCaption());
        params.put("checkin[created_at]", String.valueOf(checkIn.getCreatedAt()));
        params.put("checkin[updated_at]", String.valueOf(checkIn.getUpdatedAt()));

        Log.d(TAG, "uploading checkIn with parameters " + params);
        Log.d(TAG, "uploading checkIn with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            Log.d(TAG, "checkIn uploaded with response " + response);
            String serverId = response.getJSONObject("checkin").getString("id");
            CheckinDataSource.updateServerId(context.getApplicationContext(), checkIn.getId(), serverId);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "checkIn couldnot be uploaded InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "checkIn couldnot be uploaded ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "checkIn couldnot be uploaded TimeoutException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "checkIn could not be parsed although uploaded successfully");
            e.printStackTrace();
        }
        return false;
    }

}
