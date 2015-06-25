package com.traveljar.memories.utility;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.toolbox.RequestFuture;
import com.traveljar.memories.SQLitedatabase.LikeDataSource;
import com.traveljar.memories.SQLitedatabase.RequestQueueDataSource;
import com.traveljar.memories.models.Like;
import com.traveljar.memories.services.MakeServerRequestsService;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;
import com.traveljar.memories.models.Request;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class MemoriesUtil {

    private static MemoriesUtil instance;

    public static MemoriesUtil getInstance() {
        instance = instance == null ? new MemoriesUtil() : instance;
        return instance;
    }

    private static final String TAG = "MemoriesUtil";

    public void deleteMemory(Context context, String memoryIdOnServer) {
        new DeleteMemoryAsyncTask(context).execute(memoryIdOnServer);
    }

    public static boolean likeMemoryOnServer(Context context, Like like) {
        String url = Constants.URL_MEMORY_UPDATE + like.getJourneyId() + "/memories/" + like.getMemoryLocalId() + "/like";

        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));

        Log.d(TAG, "uploading like with parameters " + params);
        Log.d(TAG, "uploading like with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(com.android.volley.Request.Method.POST, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            Log.d(TAG, "memory liked successfully on server with response " + response);
            like.setIdOnServer(response.getJSONArray("likes").getJSONObject(0).getString("id"));
            /*LikeDataSource.updateLike(like, context);*/
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "like couldnot be uploaded InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "like couldnot be uploaded ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "like couldnot be uploaded TimeoutException");
            e.printStackTrace();
        } catch (JSONException e) {
            Log.d(TAG, "like couldnot be parsed although uploaded successfully");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean unlikeMemoryOnServer(Context context, Like like) {
        String url = Constants.URL_MEMORY_UPDATE + like.getJourneyId() + "/memories/" + like.getMemoryLocalId() + "/unlike";

        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));

        Log.d(TAG, "unliking memory with parameters " + params);
        Log.d(TAG, "unliking memory with url " + url);

        final RequestFuture<JSONObject> futureRequest = RequestFuture.newFuture();
        CustomJsonRequest jsonRequest = new CustomJsonRequest(com.android.volley.Request.Method.PUT, url, params, futureRequest, futureRequest);

        AppController.getInstance().getRequestQueue().add(jsonRequest);
        try {
            JSONObject response = futureRequest.get(30, TimeUnit.SECONDS);
            LikeDataSource.deleteLike(context, like.getId());
            Log.d(TAG, "memory unliked successfully on server with response " + response);
            return true;
        } catch (InterruptedException e) {
            Log.d(TAG, "couldnot unlike InterruptedException");
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.d(TAG, "couldnot unlike ExecutionException");
            e.printStackTrace();
        } catch (TimeoutException e) {
            Log.d(TAG, "couldnot unlike TimeoutException");
            e.printStackTrace();
        }
        return false;
    }

    public static Like createLikeRequest(String memoryId, int categoryType, Context context, String memoryType) {
        Like like = new Like(null, null, TJPreferences.getActiveJourneyId(context), memoryId, TJPreferences.getUserId(context), memoryType, true, null, HelpMe.getCurrentTime(), HelpMe.getCurrentTime());
        like.setId(String.valueOf(LikeDataSource.createLike(like, context)));
        Log.d(TAG, "like created in database = " + like + memoryType);
        Request request = new Request(null, like.getId(), TJPreferences.getActiveJourneyId(context),
                Request.OPERATION_TYPE_LIKE, categoryType, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, context);
        if (HelpMe.isNetworkAvailable(context)) {
            Intent intent = new Intent(context, MakeServerRequestsService.class);
            context.startService(intent);
        } else {
            Log.d(TAG, "since no network not starting service RQ");
        }

        return like;
    }

    public static void createUnlikeRequest(Like like, int categoryType, Context context) {
        Log.d(TAG, "unliking a memory with id = " + like.getId() + like.getMemType());
        //LikeDataSource.deleteLike(context, like);
        like.setIsValid(false);
        LikeDataSource.updateLike(like, context);

        Request request = new Request(null, like.getId(), TJPreferences.getActiveJourneyId(context),
                Request.OPERATION_TYPE_UNLIKE, categoryType, Request.REQUEST_STATUS_NOT_STARTED, 0);
        RequestQueueDataSource.createRequest(request, context);
        if (HelpMe.isNetworkAvailable(context)) {
            Intent intent = new Intent(context, MakeServerRequestsService.class);
            context.startService(intent);
        } else {
            Log.d(TAG, "since no network not starting service RQ");
        }
    }

    private class DeleteMemoryAsyncTask extends AsyncTask<String, Void, String> {
        Context context;
        public DeleteMemoryAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... maps) {
            String url = Constants.URL_MEMORY_UPDATE + TJPreferences.getActiveJourneyId(context) + "/memories/" + maps[0]
                    + "?api_key=" + TJPreferences.getApiKey(context);
            Log.d(TAG, "url is " + url + " api key " + TJPreferences.getApiKey(context));
            HttpDelete deleteRequest = new HttpDelete(url);
            HttpResponse response;
            try {
                response = new DefaultHttpClient().execute(deleteRequest);
                JSONObject object = new JSONObject(EntityUtils.toString(response.getEntity()));
                Log.d(TAG, "response on deleting memory" + object);
            } catch (Exception e) {
                Log.d(TAG, "error in deleting memory" + e.getMessage());
            }
            return null;
        }
    }

}
