package com.example.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.memories.SQLitedatabase.NoteDataSource;
import com.example.memories.models.CheckIn;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CheckinUtil {
    private static final String TAG = "CHECKIN_UTIL";

    public static void uploadCheckin(final CheckIn checkin, final Context context) {

        String uploadRequestTag = "UPLOAD_CHECKIN";
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("checkin[user_id]", checkin.getCreatedBy());
        params.put("checkin[place_name]", checkin.getCheckInPlaceName());
        params.put("checkin[latitude]", (Double) checkin.getLatitude() == null ? null : ((Double) checkin.getLatitude()).toString());
        params.put("checkin[longitude]", (Double) checkin.getLongitude() == null ? null : ((Double) checkin.getLongitude()).toString());
        params.put("checkin[buddies]", checkin.getCheckInWith() == null ? null : checkin.getCheckInWith().toString());
        Log.d(TAG, "uploading checkin with parameters " + params);

        String url = Constants.TRAVELJAR_API_BASE_URL + "/journeys/"
                + TJPreferences.getActiveJourneyId(context) + "/checkins";
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "checkin uploaded successfully" + response);
                        try {
                            String serverId = response.getJSONObject("checkin").getString("id");
                            NoteDataSource.updateServerId(context.getApplicationContext(), checkin.getId(), serverId);
                        } catch (Exception ex) {
                            Log.d(TAG, "exception in parsing checkin received from server" + ex);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in uploading checkin" + error);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }
}
