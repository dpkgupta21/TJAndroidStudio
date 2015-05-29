package com.example.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.currentjourney.CurrentJourneyBaseActivity;
import com.example.memories.models.Journey;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.CustomJsonRequest;
import com.google.common.base.Joiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewJourneyDetail extends AppCompatActivity {

    protected static final String TAG = "<NewJourneyDetail>";

    private String jGroupType;
    private String jBuddyList;
    private String jName;
    private EditText mJourneyName;
    private EditText mJourneyTagLine;
    private Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Details");
        setSupportActionBar(toolbar);

        mJourneyName = (EditText) findViewById(R.id.new_journey_detail_name);
        mJourneyTagLine = (EditText) findViewById(R.id.new_journey_detail_tagline);
    }

    private void createParams() {

        jGroupType = "Friends";
        jBuddyList = Joiner.on(",").join(AppController.buddyList);
        Log.d(TAG, "buddy list = " + jBuddyList);

        // create params to be sent in create new journey api
        params = new HashMap<>();
        try {
            params.put("journey[name]", mJourneyName.getText().toString().trim());
            params.put("journey[tag_line]", mJourneyTagLine.getText().toString().trim());
            params.put("journey[group_relationship]", jGroupType);
            params.put("journey[buddy_ids]", jBuddyList);
            params.put("api_key", TJPreferences.getApiKey(getBaseContext()));

            // get all the journey laps into an array and pass as POST parameters parse the "lapslist" so that
            // it can be properly passed to backend when creating new journey

            int currentPosition = 0;
            for (Map<String, String> lap : ((AppController) getApplicationContext()).lapsList) {
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][source_id]",
                        "1");
                params.put("journey[journey_laps_attributes[" + currentPosition
                        + "]][destination_id]", "2");
                params.put(
                        "journey[journey_laps_attributes[" + currentPosition + "]][travel_mode]",
                        "car");
                params.put(
                        "journey[journey_laps_attributes[" + currentPosition + "]][time_of_day]",
                        "morning");
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][start_date]",
                        lap.get("date"));

                currentPosition++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createNewJourney(View v) {
        jName = mJourneyName.getText().toString();
        if (jName != null && jName != "") {
            if (HelpMe.isNetworkAvailable(this)) {

                createParams();

                // Tag used to cancel the request
                String tag_json_obj = "createNewJourney";
                String url = Constants.URL_CREATE_JOURNEY;
                Log.d(TAG, "creating new journey with url " + url);
                Log.d(TAG, "with params " + params);

                final ProgressDialog pDialog = new ProgressDialog(this);
                pDialog.setMessage("Loading...");
                pDialog.show();

                CustomJsonRequest newJourneyReq = new CustomJsonRequest(Request.Method.POST, url,
                        params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        try {
                            createNewJourneyInDB(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent i = new Intent(getBaseContext(), CurrentJourneyBaseActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        pDialog.hide();
                        Toast.makeText(
                                getApplicationContext(),
                                "There are some issues creating your journey please try again",
                                Toast.LENGTH_LONG).show();
                    }
                });

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(newJourneyReq, tag_json_obj);
            } else {
                Toast.makeText(getBaseContext(), "Please connect to Internet", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Journey Name is required", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void createNewJourneyInDB(JSONObject res) throws JSONException {

        Log.d(TAG, "createNewJourneyInDB");
        JSONObject newJourney = res.getJSONObject("journey");

        String idOnServer = newJourney.getString("id");
        String name = newJourney.getString("name");
        String tag_line = newJourney.getString("tag_line");
        String group_relationship = newJourney.getString("group_relationship");
        String created_by_id = newJourney.getString("created_by_id");

        JSONArray lapsList = newJourney.getJSONArray("journey_lap_ids");
        JSONArray buddyList = newJourney.getJSONArray("buddy_ids");

        ArrayList<String> buddyArrayList = new ArrayList<>();
        if (buddyList != null) {
            int len = buddyList.length();
            for (int i = 0; i < len; i++) {
                buddyArrayList.add(buddyList.get(i).toString());
            }
        }

        // Add it to the Database
        Journey newJ = new Journey(idOnServer, name, tag_line, group_relationship, created_by_id,
                null, buddyArrayList, Constants.JOURNEY_STATUS_ACTIVE);
        JourneyDataSource.createJourney(newJ, getBaseContext());
        TJPreferences.setActiveJourneyId(this, idOnServer);
    }

    /*private void createNewJourneyInDBBypass() throws JSONException {

        Log.d(TAG, "createNewJourneyInDB");
        String id = "45";
        String name = "abhinav";
        String tag_line = "asdaf";

        String group_relationship = "friends";
        String created_by_id = "18";

        ArrayList<String> buddyArrayList = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            buddyArrayList.add("22");
        }

        // Add it to the Database
        Journey newJ = new Journey(id, name, tag_line, group_relationship, created_by_id, null,
                buddyArrayList, Constants.JOURNEY_STATUS_ACTIVE);
        JourneyDataSource.createJourney(newJ, getBaseContext());
        TJPreferences.setActiveJourneyId(this, id);
    }*/

}