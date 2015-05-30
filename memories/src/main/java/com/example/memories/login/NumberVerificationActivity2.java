package com.example.memories.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.memories.R;
import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.utility.Constants;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NumberVerificationActivity2 extends Activity {

    private static final String TAG = "<NumberVer2>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification_2);

        // Retrieve phone number saved in TJPreferences by SMSReceiver
        // IF not null update the same on the server
        String phoneNumber = TJPreferences.getPhone(this);
        if (phoneNumber != "" && phoneNumber != null) {
            updateUserPhoneNumber();
        }

    }

    public void goToActiveJourneys(View v) {
        Intent i = new Intent(getApplicationContext(), ActivejourneyList.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void updateUserPhoneNumber() {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.URL_UPDATE_USER_DETAILS + TJPreferences.getUserId(getBaseContext());

        Map params = new HashMap<>();
        params.put("user[phone]", TJPreferences.getPhone(getBaseContext()));
        params.put("api_key", TJPreferences.getApiKey(getBaseContext()));

        // Request a string response from the provided URL.
        CustomJsonRequest signUpReg = new CustomJsonRequest(Request.Method.PUT, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "=====" + response.toString());
                        Log.d(TAG, "phone number updated = " + TJPreferences.getPhone(getBaseContext()));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Unable to update phone number with volley error -> " + error);
            }
        });

        // Add the request to the RequestQueue.
        //AppController.getInstance().getRequestQueue().add(signUpReg);
        queue.add(signUpReg);
    }

}