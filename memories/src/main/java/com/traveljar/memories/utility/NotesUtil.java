package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.RequestFuture;
import com.traveljar.memories.SQLitedatabase.NoteDataSource;
import com.traveljar.memories.models.Note;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NotesUtil {

    private static final String TAG = "NOTES_UTIL";

    // For a synchronous request
    public static boolean uploadNoteOnServer(Context context, Note note){

        String url = Constants.URL_MEMORY_UPLOAD + TJPreferences.getActiveJourneyId(context) + "/notes";
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("note[user_id]", note.getCreatedBy());
        params.put("note[note]", note.getContent());
        params.put("note[latitude]", String.valueOf(note.getLatitude()));
        params.put("note[longitude]", String.valueOf(note.getLongitude()));
        params.put("note[created_at]", String.valueOf(note.getCreatedAt()));
        params.put("note[updated_at]", String.valueOf(note.getUpdatedAt()));

        Log.d(TAG, "uploading note with parameters " + params);
        Log.d(TAG, "uploading note with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(Request.Method.POST, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(60, TimeUnit.SECONDS);
            Log.d(TAG, "note uploaded with response " + response);
            String serverId = response.getJSONObject("note").getString("id");
            NoteDataSource.updateServerId(context, note.getId(), serverId);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "note couldnot be uploaded InterruptedException");
            e.printStackTrace();
            e.getCause();
        } catch (ExecutionException e) {
            Log.d(TAG, "note couldnot be uploaded ExecutionException");
            e.printStackTrace();
            e.getCause();
            return true;
        } catch (TimeoutException e) {
            Log.d(TAG, "note couldnot be uploaded TimeoutException");
            e.printStackTrace();
            e.getCause();
        }catch (CancellationException e){
            Log.d(TAG, "note couldnot be uploaded CancellationException");
            e.printStackTrace();
            e.getCause();
        } catch (JSONException e) {
            Log.d(TAG, "note couldnot be parsed although uploaded successfully");
            e.printStackTrace();
            e.getCause();
        }
        return false;
    }

}
