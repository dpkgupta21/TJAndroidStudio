package com.traveljar.memories.checkin;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

public class CheckInPlacesList extends AppCompatActivity {

    private static final String TAG = "<CheckInPlacesList>";

    // These tags will be used to cancel the requests
    private String tag_json_obj = "jobj_req", tag_json_arry = "jarray_req";
    private List<Place> placeList;
    private CheckInPlacesListAdapter placeListViewAdapter;
    private ProgressDialog pDialog;
    private double lat;
    private double longi;
    private long lastCharTypedAt = 0;

    long requestStartTime;
    long requestFinishTime;
    long requestParseTime;

    private EditText filterPlacesEditTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkin_places_list);

        setUpToolBar();

        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog,  final int id) {
                            startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();
        }

        filterPlacesEditTxt = (EditText) findViewById(R.id.checkin_places_search);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(false);
    }

    private void setUpToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Checkin Places");
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
        //showProgressDialog();

        String url = Constants.URL_FS_VENUE_EXPLORE + "?ll=" + lat + "," + longi + "&client_id="
                + Constants.FOURSQUARE_CLIENT_ID + "&client_secret=" + Constants.FOURSQUARE_CLIENT_SECRET
                + "&v=" + Constants.v + "&limit=20";

        url = (query == null) ? url : url + "&query=" + query;

        Log.d(TAG, "FS_URL=" + url);
        requestStartTime = System.currentTimeMillis();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, url, (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG, response.toString());
                        requestFinishTime = System.currentTimeMillis();
                        Log.d(TAG, "total time to fetch request -> " + (requestFinishTime - requestStartTime)/1000);
                        //hideProgressDialog();
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
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
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
        requestParseTime = System.currentTimeMillis();
        Log.d(TAG, "total time to parse request -> " + (requestParseTime - requestFinishTime)/1000);
        ListView checkInPlaceListView = (ListView) findViewById(R.id.checkInPlacesList);
        placeListViewAdapter = new CheckInPlacesListAdapter(this, placeList);
        checkInPlaceListView.setAdapter(placeListViewAdapter);
        if(!pDialog.isShowing())
            pDialog.dismiss();

        checkInPlaceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                Place place = (Place)placeListViewAdapter.getItem(position);
                Intent i = new Intent(CheckInPlacesList.this, CheckInPreview.class);
                i.putExtra("placeName", place.getName());
                i.putExtra("latitude", lat);
                i.putExtra("longitude", longi);
                startActivity(i);
                finish();
            }
        });

        filterPlacesEditTxt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                placeListViewAdapter.getFilter().filter(s);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (System.currentTimeMillis() - lastCharTypedAt >= 500) {
                            //send request
                            getCheckInPlaces(lat, longi, s.toString());
                        }
                    }
                }, 500);
                lastCharTypedAt = System.currentTimeMillis();

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
        placeList = new ArrayList<>();

        String code = jsonObj.getJSONObject("meta").getString("code");
        // Log.d(TAG, "successs ----" + code + jsonObj.toString());

        JSONArray venueList = jsonObj.getJSONObject("response").getJSONArray("venues");
        Integer len = venueList.length();
        Log.d(TAG, "NO of places = " + len);

        placeList = new ArrayList<>();
        Place place;
        for (int i = 0; i < len; i++) {
            JSONObject venueItem = venueList.getJSONObject(i);
            place = new Place();

            place.setName(venueItem.getString("name"));
            place.setAddress(venueItem.getJSONObject("location").getString("distance") + "m");
            // check if address is valid
            if (venueItem.getJSONObject("location").has("address")) {
                place.setAddress(place.getAddress() + " - " + venueItem.getJSONObject("location").getString("address"));
            }
            // check if crossStreet is valid
            if (venueItem.getJSONObject("location").has("crossStreet")) {
                place.setAddress(place.getAddress() + ", " + venueItem.getJSONObject("location").getString("crossStreet"));
            }
            // check for number of user there
            if (venueItem.getJSONObject("stats").has("checkinsCount")) {
                place.setCheckInCount(venueItem.getJSONObject("stats").getString("checkinsCount"));
            }

            // get the icon URL
            if (!venueItem.getJSONArray("categories").isNull(0)) {
                if (venueItem.getJSONArray("categories").getJSONObject(0).has("icon")) {
                    String prefix = venueItem.getJSONArray("categories").getJSONObject(0)
                            .getJSONObject("icon").getString("prefix");
                    String suffix = venueItem.getJSONArray("categories").getJSONObject(0)
                            .getJSONObject("icon").getString("suffix");
                    place.setThumbUrl(prefix + "bg_64" + suffix);
                }
            }
            placeList.add(place);
        }

    }

    @Override
    public void onResume(){
        LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
            if (HelpMe.isNetworkAvailable(getBaseContext())) {
                pDialog.show();
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
        super.onResume();
    }

    public class Place{
        String name;
        String address;
        String checkInCount;
        String thumbUrl;

        public Place(){}

        public Place(String name, String address, String checkInCount, String thumbUrl) {
            this.name = name;
            this.address = address;
            this.checkInCount = checkInCount;
            this.thumbUrl = thumbUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCheckInCount() {
            return checkInCount;
        }

        public void setCheckInCount(String checkInCount) {
            this.checkInCount = checkInCount;
        }

        public String getThumbUrl() {
            return thumbUrl;
        }

        public void setThumbUrl(String thumbUrl) {
            this.thumbUrl = thumbUrl;
        }
    }

}