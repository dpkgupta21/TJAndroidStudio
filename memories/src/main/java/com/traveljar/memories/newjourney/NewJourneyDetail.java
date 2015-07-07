package com.traveljar.memories.newjourney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.common.base.Joiner;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.JourneyDataSource;
import com.traveljar.memories.SQLitedatabase.LapDataSource;
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.SQLitedatabase.PlaceDataSource;
import com.traveljar.memories.currentjourney.CurrentJourneyBaseActivity;
import com.traveljar.memories.models.Journey;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.models.Place;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

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

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Journey Details");
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        setUpToolBar();

        mJourneyName = (EditText) findViewById(R.id.new_journey_detail_name);
        mJourneyTagLine = (EditText) findViewById(R.id.new_journey_detail_tagline);
    }

    private void createParams() {

        jGroupType = "Friends";
        jBuddyList = (AppController.buddyList == null) ? "" : Joiner.on(",").join(AppController.buddyList);
        Log.d(TAG, "buddy list = " + jBuddyList + "====" + AppController.buddyList);

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
            for (Lap lap : AppController.lapList) {
                // Get source info
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][source_city_name]",
                        lap.getSourceCityName());
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][source_state_name]",
                        lap.getSourceStateName());
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][source_country_name]",
                        lap.getSourceCountryName());

                // Get destination info
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][destination_city_name]",
                        lap.getDestinationCityName());
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][destination_state_name]",
                        lap.getDestinationStateName());
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][destination_country_name]",
                        lap.getDestinationCountryName());

                params.put("journey[journey_laps_attributes[" + currentPosition + "]][travel_mode]",
                        HelpMe.getConveyanceMode(lap.getConveyanceMode()));
                params.put("journey[journey_laps_attributes[" + currentPosition + "]][start_date]", String.valueOf(lap.getStartDate()));

                currentPosition++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void createNewJourney(View v) {
        jName = mJourneyName.getText().toString().trim();
        if (!jName.isEmpty()) {
            if (HelpMe.isNetworkAvailable(this)) {

                createParams();

                // Tag used to cancel the request
                String tag_json_obj = "createNewJourney";
                String url = Constants.URL_CREATE_JOURNEY;
                Log.d(TAG, "creating new journey with url " + url);
                Log.d(TAG, "with params " + params);

                final ProgressDialog pDialog = new ProgressDialog(this);
                pDialog.setMessage("Loading...");
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.show();

                CustomJsonRequest newJourneyReq = new CustomJsonRequest(Request.Method.POST, url,
                        params, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.dismiss();
                        try {
                            createNewJourneyInDB(response);
                            AppController.buddyList.clear();
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
                        pDialog.dismiss();
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
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        String tag_line = newJourney.getString("tag_line");
        String group_relationship = newJourney.getString("group_relationship");
        String created_by_id = newJourney.getString("created_by_id");

//        JSONArray lapsList = newJourney.getJSONArray("journey_lap_ids");
        JSONArray lapsArray = newJourney.getJSONArray("journey_laps");
        parseLaps(lapsArray, idOnServer);
        JSONArray buddyList = newJourney.getJSONArray("buddy_ids");

        ArrayList<String> buddyArrayList = null;
        if (buddyList.length() > 0) {
            buddyArrayList = new ArrayList<>();
            int len = buddyList.length();
            for (int i = 0; i < len; i++) {
                buddyArrayList.add(buddyList.get(i).toString());
            }
        }

        // Add it to the Database
        Journey newJ = new Journey(idOnServer, name, tag_line, group_relationship, created_by_id,
                null, buddyArrayList, Constants.JOURNEY_STATUS_ACTIVE, HelpMe.getCurrentTime(), HelpMe.getCurrentTime(), 0, true);
        JourneyDataSource.createJourney(newJ, getBaseContext());
        for(Lap lap : AppController.lapList){
            Log.d(TAG, "setting jouney id " + idOnServer);
            lap.setJourneyId(idOnServer);
        }
        Log.d(TAG, "total laps in the database are " + LapDataSource.getAllLaps(this));
        LapDataSource.updateLapsList(AppController.lapList, this);
        LapDataSource.updateLapsList(AppController.lapList, this);
        AppController.lapList.clear();
        TJPreferences.setActiveJourneyId(this, idOnServer);
    }

    private void parseLaps(JSONArray journeyLaps, String journeyId){
        Laps laps;
        Place source;
        Place destination;
        JSONObject lapObject;
        JSONObject sourceObject;
        JSONObject destinationObject;
        Double latitude;
        Double longitude;
        long sourceId;
        long destinationId;
        int noLaps = journeyLaps.length();
        for(int i = 0; i < noLaps; i++){
            try {
                lapObject = (JSONObject)journeyLaps.get(i);
                sourceObject = lapObject.getJSONObject("source");
                destinationObject = lapObject.getJSONObject("destination");

                //Parsing source place
                latitude = sourceObject.getString("latitude").equals("null") ? 0.0 : Double.parseDouble(sourceObject.getString("latitude"));
                longitude = sourceObject.getString("longitude").equals("null") ? 0.0 : Double.parseDouble(sourceObject.getString("longitude"));

                source = new Place(null, sourceObject.getString("id"), sourceObject.getString("country"), sourceObject.getString("state"),
                        sourceObject.getString("city"), Long.parseLong(sourceObject.getString("created_at")), latitude, longitude);
                sourceId = PlaceDataSource.createPlace(source, this);

                // Parsing destination Place
                latitude = destinationObject.getString("latitude").equals("null") ? 0.0 : Double.parseDouble(destinationObject.getString("latitude"));
                longitude = destinationObject.getString("longitude").equals("null") ? 0.0 : Double.parseDouble(destinationObject.getString("longitude"));

                destination = new Place(null, destinationObject.getString("id"), destinationObject.getString("country"), destinationObject.getString("state"),
                        destinationObject.getString("city"), Long.parseLong(destinationObject.getString("created_at")), latitude, longitude);
                destinationId = PlaceDataSource.createPlace(destination, this);

                laps = new Laps(null, lapObject.getString("id"), journeyId, String.valueOf(sourceId), String.valueOf(destinationId),
                        HelpMe.getConveyanceModeCode(lapObject.getString("travel_mode")), Long.parseLong(lapObject.getString("start_date")));
                LapsDataSource.createLap(laps, this);
            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Journey Detail");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewJourneyDetail.this.finish();
            }
        });
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