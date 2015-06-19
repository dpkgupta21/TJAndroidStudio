package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotesUtil {

    private static final String TAG = "NOTES_UTIL";

    public static void uploadNotes(final Note note, final Context context) {

        String uploadRequestTag = "UPLOAD_NOTE";
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("note[user_id]", note.getCreatedBy());
        params.put("note[note]", note.getContent());
        params.put("note[latitude]", String.valueOf(note.getLatitude()));
        params.put("note[longitude]", String.valueOf(note.getLongitude()));
        Log.d(TAG, "uploading note with parameters " + params);

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/notes";
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "note uploaded successfully" + response);
                        try {
                            String serverId = response.getJSONObject("note").getString("id");
                            NoteDataSource.updateServerId(context, note.getId(), serverId);
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
        AppController.getInstance().addToRequestQueue(uploadRequest, uploadRequestTag);
    }
}
