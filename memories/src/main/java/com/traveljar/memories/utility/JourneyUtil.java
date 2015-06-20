package com.traveljar.memories.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JourneyUtil {

    private OnAddBuddyListener mBuddyAddListener;
    private OnExitJourneyListener mExitJourneyListener;

    private static JourneyUtil mInstance;

    public static JourneyUtil getInstance(){
        mInstance = mInstance == null ? new JourneyUtil() : mInstance;
        return mInstance;
    }

    public void setAddBuddyListener(OnAddBuddyListener listener){
        mBuddyAddListener = listener;
    }

    public void setExitJourneyListener(OnExitJourneyListener listener){
        mExitJourneyListener = listener;
    }

    private static final String TAG = "JourneyUtil";


    // Removes a user from the journey
    public void exitJourney(Context context, final String userId){
        String url = Constants.URL_CREATE_JOURNEY + "/" + TJPreferences.getActiveJourneyId(context) + "/remove_buddy";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("buddy_id", userId);
        Log.d(TAG, "calling url " + url);
        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "user has successfully exited the journey");
                        mExitJourneyListener.onExitJourney(0, userId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "user unable to exit the journey" + error);
                error.printStackTrace();
                mExitJourneyListener.onExitJourney(-1, userId);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest);
    }

    public static void endJourneyOnServer(final Context context){
        String url = Constants.URL_CREATE_JOURNEY + "/" +TJPreferences.getActiveJourneyId(context);
        Map<String, String> params = new HashMap<String, String>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("journey[completed_at]", "2015-06-09T06:09:06.258Z");

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "journey with id " + TJPreferences.getActiveJourneyId(context) + "completed successfully on server" + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "journey could not be completed at server" + error);
                error.printStackTrace();
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest);
    }

    public void addUserToJourney(Context context, final String contactId){
        String url = Constants.URL_CREATE_JOURNEY + "/" + TJPreferences.getActiveJourneyId(context) + "/add_buddy";
        Map<String, String> params = new HashMap<>();
        params.put("api_key", TJPreferences.getApiKey(context));
        params.put("buddy_id", contactId);

        CustomJsonRequest uploadRequest = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "contact successfully added to the journey");
                        mBuddyAddListener.onAddBuddy(contactId, 0);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "journey could not be completed at server" + error);
                error.printStackTrace();
                mBuddyAddListener.onAddBuddy(contactId, -1);
            }
        });
        AppController.getInstance().addToRequestQueue(uploadRequest);
    }
    // result code = 0 for success, -1 for failure
    public interface OnAddBuddyListener{
        void onAddBuddy(String contactId, int resultCode);
    }

    public interface OnExitJourneyListener{
        void onExitJourney(int resultCode, String userId);
    }

}
