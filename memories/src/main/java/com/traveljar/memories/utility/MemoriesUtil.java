package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MemoriesUtil {

    private static MemoriesUtil instance;
    private OnMemoryDeleteListener mDeleteListener;

    public static MemoriesUtil getInstance(){
        instance = instance == null ? new MemoriesUtil() : instance;
        return instance;
    }
    public void setMemoryDeleteListener(OnMemoryDeleteListener mDeleteListener){
        this.mDeleteListener = mDeleteListener;
    }

    private static final String TAG = "MemoriesUtil";

    public static void likeMemory(final Context context, final Like like){
        String url = Constants.URL_MEMORY_UPDATE + like.getJourneyId() + "/memories/" + like.getMemorableId() + "/like";
        String requestTag = "LIKE_MEMORY";

        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d(TAG, "memory liked successfully on server with response " + response);
                            like.setIdOnServer(response.getJSONArray("likes").getJSONObject(0).getString("id"));
                            LikeDataSource.updateLike(like, context);
                        } catch (Exception ex) {
                            Log.d(TAG, "exception in parsing mood received from server" + ex);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "unable to like memory" + error);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest, requestTag);
    }

    public static void unlikeMemory(final Context context, final Like like){
        String url = Constants.URL_MEMORY_UPDATE + like.getJourneyId() + "/memories/" + like.getMemorableId() + "/unlike";
        String requestTag = "LIKE_MEMORY";

        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "memory successfully unliked on server " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "unable to unlike memory" + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("api_key", TJPreferences.getApiKey(context));
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(uploadRequest, requestTag);
    }

    public void deleteMemory(final Context context, final Like like){
        String url = Constants.URL_MEMORY_UPDATE + like.getJourneyId() + "/memories/" + like.getMemorableId() + "/unlike";
        String requestTag = "LIKE_MEMORY";

        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "memory successfully unliked on server " + response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "unable to unlike memory" + error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("api_key", TJPreferences.getApiKey(context));
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(uploadRequest, requestTag);
    }

    public interface OnMemoryDeleteListener{
        void onDeleteMemory(int resultCode);
    }

}
