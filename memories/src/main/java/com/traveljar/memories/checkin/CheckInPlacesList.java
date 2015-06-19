package com.traveljar.memories.checkin;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.traveljar.memories.R;
import com.traveljar.memories.checkin.adapter.CheckInPlacesListAdapter;
import com.traveljar.memories.services.GPSTracker;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.volley.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CheckInPlacesList extends AppCompatActivity {

    private static final String TAG = "<CheckInPlacesList>";

    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private ArrayList<Map<String, String>> placeList;
    private CheckInPlacesListAdapter placeListViewAdapter;
    private ProgressDialog pDialog;
    private double lat;
    private double longi;

    private EditText filterPlacesEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_places_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Checkin Places");
        setSupportActionBar(toolbar);

        filterPlacesEditTxt = (EditText) findViewById(R.id.checkin_places_search);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        if (HelpMe.isNetworkAvailable(getBaseContext())) {
            Log.d(TAG, "network available");
            GPSTracker gps = new GPSTracker(this);
            // gps enabled return boolean true/false
            if (gps.canGetLocation()) {
                lat = gps.getLatitude(); // returns latitude
                longi = gps.getLongitude(); // returns longitude
                getCheckInPlaces(lat, longi, null);
            } else {
                Toast.makeText(getApplicationContext(), "Network issues. Try later.",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "Please connect to internet", Toast.LENGTH_LONG)
                    .show();
        }

    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Making json object request
     */
    private void getCheckInPlaces(double lat, double longi, String query) {
        showProgressDialog();

        String url = Constants.URL_FS_VENUE_EXPLORE + "?ll=" + lat + "," + longi + "&client_id="
                + Constants.FOURSQUARE_CLIENT_ID + "&client_secret=" + Constants.FOURSQUARE_CLIENT_SECRET
                + "&v=" + Constants.v;

        url = (query == null) ? url : url + "&query=" + query;

        Log.d(TAG, "FS_URL=" + url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG, response.toString());
                        hideProgressDialog();
                        try {
                            Log.d(TAG, response.toString());
                            updateCheckInPlacesList(response);
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
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

    private void updateCheckInPlacesList(JSONObject res) throws JSONException {

        formatJSON(res);

        ListView checkInPlaceListView = (ListView) findViewById(R.id.checkInPlacesList);
        placeListViewAdapter = new CheckInPlacesListAdapter(this, placeList);
        checkInPlaceListView.setAdapter(placeListViewAdapter);

        checkInPlaceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String placeName = placeList.get(position).get("name");
                Intent i = new Intent(CheckInPlacesList.this, CheckInPreview.class);
                i.putExtra("placeName", placeName);
                i.putExtra("latitude", lat);
                i.putExtra("longitude", longi);
                startActivity(i);
                finish();
            }
        });

        filterPlacesEditTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");
                getCheckInPlaces(lat, longi, s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        Log.d(TAG, "list population done.");
    }

    private void formatJSON(JSONObject jsonObj) throws JSONException {
        placeList = new ArrayList<Map<String, String>>();

        String code = jsonObj.getJSONObject("meta").getString("code");
        // Log.d(TAG, "successs ----" + code + jsonObj.toString());

        JSONArray venueList = jsonObj.getJSONObject("response").getJSONArray("venues");
        Integer len = venueList.length();
        Log.d(TAG, "NO of places = " + len);

        for (int i = 0; i < len; i++) {
            JSONObject venueItem = venueList.getJSONObject(i);
            Map<String, String> map = new HashMap<String, String>();
            String address = null;
            String count = null;
            String imgURL = null;

            // check for name
            String name = venueItem.getString("name");

            // get distance from current location
            address = venueItem.getJSONObject("location").getString("distance") + "m";

            // check if address is valid
            if (venueItem.getJSONObject("location").has("address")) {
                address += " - " + venueItem.getJSONObject("location").getString("address");
            }

            // check if crossStreet is valid
            if (venueItem.getJSONObject("location").has("crossStreet")) {
                address += ", " + venueItem.getJSONObject("location").getString("crossStreet");
            }

            // check for number of user there
            if (venueItem.getJSONObject("stats").has("checkinsCount")) {
                count = venueItem.getJSONObject("stats").getString("checkinsCount");
            }

            // get the icon URL
            // Log.d(TAG, "categories no of itme" +
            // venueItem.getJSONArray("categories").toString());
            if (!venueItem.getJSONArray("categories").isNull(0)) {
                if (venueItem.getJSONArray("categories").getJSONObject(0).has("icon")) {
                    String prefix = venueItem.getJSONArray("categories").getJSONObject(0)
                            .getJSONObject("icon").getString("prefix");
                    String suffix = venueItem.getJSONArray("categories").getJSONObject(0)
                            .getJSONObject("icon").getString("suffix");
                    imgURL = prefix + "bg_64" + suffix;
                }
            }

            map.put("name", name);
            map.put("address", address);
            map.put("count", count + " check-ins already");
            map.put("thumbnail", imgURL);
            placeList.add(map);
        }

    }

}