package com.example.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.memories.SQLitedatabase.MoodDataSource;
import com.example.memories.models.Mood;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;
import com.google.common.base.Joiner;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MoodUtil {
    private static final String TAG = "MOODS_UTIL";

    public static void uploadMood(final Mood mood, final Context context) {

        String uploadRequestTag = "UPLOAD_MOOD";
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("mood[user_id]", mood.getCreatedBy());
        params.put("mood[mood]", mood.getMood());
        params.put("mood[reason]", mood.getReason());
        params.put("mood[buddies]", Joiner.on(",").join(mood.getBuddyIds()));
        params.put("mood[latitude]", String.valueOf(mood.getLatitude()));
        params.put("mood[longitude]", String.valueOf(mood.getLongitude()));

        Log.d(TAG, "uploading mood with parameters " + params);

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/moods";
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "mood uploaded successfully" + response);
                        try {
                            String serverId = response.getJSONObject("mood").getString("id");
                            MoodDataSource.updateServerId(context, mood.getId(), serverId);
                        } catch (Exception ex) {
                            Log.d(TAG, "exception in parsing mood received from server" + ex);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "error in uploading mood" + error);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }
}
