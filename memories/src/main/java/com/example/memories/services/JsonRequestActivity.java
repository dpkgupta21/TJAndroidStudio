package com.example.memories.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.memories.volley.AppController;
import com.example.memories.volley.Const;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonRequestActivity {

    private static String TAG = "JsonRequestActivity";
    private static ProgressDialog pDialog;

    // These tags will be used to cancel the requests
    private static String tag_json_obj = "jobj_req";
    private static JSONObject res;
    private String tag_json_arry = "jarray_req";

    public static JSONObject getCheckInPlaces(Context c) {

        makeJsonObjReq();
        return res;
    }

    private static void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private static void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    /**
     * Making json object request
     */
    private static void makeJsonObjReq() {
        // showProgressDialog();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET,
                Const.URL_FS_VENUE_EXPLORE, (String)null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "success!!!");
                // hideProgressDialog();
                res = response;
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hideProgressDialog();
                res = null;
            }
        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

        // Cancelling request
        // ApplicationController.getInstance().getRequestQueue().cancelAll(tag_json_obj);

    }

}
