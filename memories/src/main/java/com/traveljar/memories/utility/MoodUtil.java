package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.google.common.base.Joiner;
import com.traveljar.memories.SQLitedatabase.MoodDataSource;
import com.traveljar.memories.models.Mood;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MoodUtil {
    private static final String TAG = "MOODS_UTIL";

    public static boolean uploadMoodOnServer(Context context, Mood mood){
        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/moods";
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("mood[user_id]", mood.getCreatedBy());
        params.put("mood[mood]", mood.getMood());
        params.put("mood[reason]", mood.getReason());
        params.put("mood[buddies]", Joiner.on(",").join(mood.getBuddyIds()));
        params.put("mood[latitude]", String.valueOf(mood.getLatitude()));
        params.put("mood[longitude]", String.valueOf(mood.getLongitude()));
        params.put("mood[created_at]", String.valueOf(mood.getCreatedAt()));
        params.put("mood[updated_at]", String.valueOf(mood.getUpdatedAt()));

        Log.d(TAG, "uploading mood with parameters " + params);
        Log.d(TAG, "uploading mood with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            Log.d(TAG, "mood uploaded with response " + response);
            String serverId = response.getJSONObject("mood").getString("id");
            MoodDataSource.updateServerId(context.getApplicationContext(), mood.getId(), serverId);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "mood couldnot be uploaded InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "mood couldnot be uploaded ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "mood couldnot be uploaded TimeoutException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "mood could not be parsed although uploaded successfully");
            e.printStackTrace();
        }
        return false;
    }

}
