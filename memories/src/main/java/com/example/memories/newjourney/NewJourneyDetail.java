package com.example.memories.newjourney;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.memories.models.Journey;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.Const;
import com.example.memories.volley.CustomJsonRequest;
import com.google.common.base.Joiner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewJourneyDetail extends Activity {

    protected static final String TAG = "<NewJourneyDetail>";
    private String jName;
    private String jTagline;
    private String jGroupType;
    private String jBuddyList;
    private Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_detail);

    }

    private void getAllParams() {
        jName = ((EditText) findViewById(R.id.new_journey_detail_name)).getText().toString().trim();
        jTagline = ((EditText) findViewById(R.id.new_journey_detail_tagline)).getText().toString()
                .trim();
        jGroupType = "Friends";
        jBuddyList = Joiner.on(",").join(AppController.buddyList);
        Log.d(TAG, "buddy list = " + jBuddyList);

        // create params to be sent in create new journey api
        params = new HashMap<String, String>();
        try {
            params.put("journey[name]", jName);
            params.put("journey[tag_line]", jTagline);
            params.put("journey[group_relationship]", jGroupType);
            params.put("journey[buddy_ids]", jBuddyList);
            params.put("api_key", TJPreferences.getApiKey(getBaseContext()));

            // get all the journey laps into an array and pass as POST
            // parameters
            // parse the "lapslist"
            // so that
            // it can be properly
            // passed to backend when creating new journey

            int currentPosition = 0;
            for (Map<String, String> lap : ((AppController) getApplicationContext()).lapsList) {
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][source_id]",
                        "1");
                params.put("journey[journey_laps_attributes[" + currentPosition
                        + "]][destination_id]", "1");
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void createNewJourney(View v) {
        if (HelpMe.isNetworkAvailable(this)) {

            getAllParams();

            // Tag used to cancel the request
            String tag_json_obj = "createNewJourney";

            String url = Const.URL_CREATE_JOURNEY;

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
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getBaseContext(), Timeline.class);
                    startActivity(i);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    pDialog.hide();
                    Toast.makeText(
                            getApplicationContext(),
                            "There were some issues, yet we are creating a journey for you :)",
                            Toast.LENGTH_LONG).show();
                    try {
                        createNewJourneyInDBBypass();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Intent i = new Intent(getBaseContext(), Timeline.class);
                    startActivity(i);
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(newJourneyReq, tag_json_obj);
        } else {
            Toast.makeText(getBaseContext(), "Please connect to Internet", Toast.LENGTH_LONG)
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

        ArrayList<String> buddyArrayList = new ArrayList<String>();
        if (buddyList != null) {
            int len = buddyList.length();
            for (int i = 0; i < len; i++) {
                buddyArrayList.add(buddyList.get(i).toString());
            }
        }

        // Add it to the Database
        Journey newJ = new Journey(idOnServer, name, tag_line, group_relationship, created_by_id,
                null, buddyArrayList, 1);
        JourneyDataSource.createJourney(newJ, getBaseContext());
        TJPreferences.setActiveJourneyId(this, idOnServer);
    }

    private void createNewJourneyInDBBypass() throws JSONException {

        Log.d(TAG, "createNewJourneyInDB");

        String id = "45";
        String name = "abhinav";
        String tag_line = "asdaf";

        String group_relationship = "friends";
        String created_by_id = "18";

        ArrayList<String> buddyArrayList = new ArrayList<String>();

        for (int i = 0; i < 1; i++) {
            buddyArrayList.add("22");
        }

        // Add it to the Database
        Journey newJ = new Journey(id, name, tag_line, group_relationship, created_by_id, null,
                buddyArrayList, 1);
        JourneyDataSource.createJourney(newJ, getBaseContext());
        TJPreferences.setActiveJourneyId(this, id);
    }

}