package com.example.memories.newjourney;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.memories.R;
import com.example.memories.utility.HelpMe;
import com.example.memories.volley.AppController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddLap extends AppCompatActivity {

    protected static final String TAG = "<AddLap>";
    private TextView fromLocation;
    private TextView toLocation;
    private TextView dateLocation;
    private int conveyanceMode;
    private DatePickerDialog datePickerDialog;
    private ToggleButton toggleCar;
    private ToggleButton toggleFlight;
    private ToggleButton toggleShip;
    private ToggleButton toggleTrain;
    private ToggleButton toggleWalk;
    private ToggleButton toggleBus;
    private ToggleButton toggleBike;
    private ToggleButton toggleCarpet;

    private boolean editMode;
    private int editLapIndex;
    private List<String> fromLocationList;
    private List<String> toLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_lap_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Location");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.next);
        setSupportActionBar(toolbar);

        fromLocation = (TextView) findViewById(R.id.new_journey_location_new_from);
        toLocation = (TextView) findViewById(R.id.new_journey_location_new_to);
        dateLocation = (TextView) findViewById(R.id.new_journey_location_new_date);
        toggleCar = (ToggleButton) findViewById(R.id.carToggle);
        toggleFlight = (ToggleButton) findViewById(R.id.flightToggle);
        toggleTrain = (ToggleButton) findViewById(R.id.trainToggle);
        toggleShip = (ToggleButton) findViewById(R.id.shipToggle);
        toggleWalk = (ToggleButton) findViewById(R.id.walkToggle);
        toggleBus = (ToggleButton) findViewById(R.id.busToggle);
        toggleBike = (ToggleButton) findViewById(R.id.bikeToggle);
        toggleCarpet = (ToggleButton) findViewById(R.id.carpetToggle);

        // Initializing the 2 location arrays
        fromLocationList = new ArrayList<>();
        fromLocationList.add("Bangalore");
        fromLocationList.add("Karnataka");
        fromLocationList.add("India");

        toLocationList = new ArrayList<>();
        toLocationList.add("Delhi");
        toLocationList.add("New Delhi");
        toLocationList.add("India");

        // Set up date picker
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar dateBirth = Calendar.getInstance();
                dateBirth.set(year, monthOfYear, dayOfMonth);
                dateLocation.setText(dateFormatter.format(dateBirth.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (getIntent().hasExtra("EDIT_JOURNEY_POSITION")) {
            editMode = true;
            editLapIndex = getIntent().getIntExtra("EDIT_JOURNEY_POSITION", -1);
            Map<String, String> lap = AppController.lapsList.get(editLapIndex);
            dateLocation.setText(HelpMe.getDate(Long.parseLong(lap.get("date")), 1));
            fromLocation.setText(lap.get("fromCity"));
            toLocation.setText(lap.get("toCity"));

            // parse string into int codes and set appropraite toggle button to true
            setConveyanceMode(Integer.parseInt(lap.get("conveyance")));
        }

    }

    public void listFromPlaces(View v) {
        Intent i = new Intent(getBaseContext(), LocationList.class);
        startActivityForResult(i, 1);
    }

    public void listToPlaces(View v) {
        Intent i = new Intent(getBaseContext(), LocationList.class);
        startActivityForResult(i, 2);
    }

    public void conveyanceToggle(View v) {
        switch (v.getId()) {
            case R.id.flightToggle:
                if (toggleFlight.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_FLIGHT;
                    conveyanceOff();
                    toggleFlight.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.carToggle:
                if (toggleCar.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_CAR;
                    conveyanceOff();
                    toggleCar.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.trainToggle:
                if (toggleTrain.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_TRAIN;
                    conveyanceOff();
                    toggleTrain.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.shipToggle:
                if (toggleShip.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_SHIP;
                    conveyanceOff();
                    toggleShip.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.walkToggle:
                if (toggleWalk.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_WALK;
                    conveyanceOff();
                    toggleWalk.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.busToggle:
                if (toggleBus.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_BUS;
                    conveyanceOff();
                    toggleBus.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.bikeToggle:
                if (toggleBike.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_BIKE;
                    conveyanceOff();
                    toggleBike.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            case R.id.carpetToggle:
                if (toggleCarpet.isChecked()) {
                    conveyanceMode = HelpMe.CONVEYANCE_CARPET;
                    conveyanceOff();
                    toggleCarpet.setChecked(true);
                } else {
                    conveyanceMode = -1;
                }
                break;
            default:
                break;
        }
    }

    // toggles off all the conveyance modes
    private void conveyanceOff() {
        toggleFlight.setChecked(false);
        toggleCar.setChecked(false);
        toggleShip.setChecked(false);
        toggleTrain.setChecked(false);
        toggleBus.setChecked(false);
        toggleWalk.setChecked(false);
        toggleBike.setChecked(false);
        toggleCarpet.setChecked(false);
    }

    // opens the calendar mode to select date
    public void showCalendar(View v) {
        datePickerDialog.show();
    }

    // save the details
    // And send back them to LapsList list screen
    public void updateDone(View v) {
        if (conveyanceMode == -1) {
            Toast.makeText(this, "Please select the conveyence mode", Toast.LENGTH_SHORT).show();
        } else {

            Map<String, String> map;
            if (editMode) {
                map = AppController.lapsList.get(editLapIndex);
            } else {
                map = new HashMap<>();
                AppController.lapsList.add(map);
            }

            // Received locatino has city, state and country
            if (fromLocationList.size() == 3) {
                map.put("fromCity", fromLocationList.get(0));
                map.put("fromState", fromLocationList.get(1));
                map.put("fromCountry", fromLocationList.get(2));

            }// Received locatino has ONLY state and country
            else {
                map.put("fromCity", fromLocationList.get(0));
                map.put("fromState", fromLocationList.get(0));
                map.put("fromCountry", fromLocationList.get(1));
            }

            if (toLocationList.size() == 3) {
                map.put("toCity", toLocationList.get(0));
                map.put("toState", toLocationList.get(1));
                map.put("toCountry", toLocationList.get(2));
            } else {
                map.put("toCity", toLocationList.get(0));
                map.put("toState", toLocationList.get(0));
                map.put("toCountry", toLocationList.get(1));
            }

            map.put("date", String.valueOf(System.currentTimeMillis() / 1000));
            map.put("conveyance", (String.valueOf(conveyanceMode) == "") ? HelpMe.getConveyanceMode(8) : String.valueOf(conveyanceMode));


            Intent i = new Intent(getBaseContext(), LapsList.class);
            startActivity(i);
            finish();
        }
    }

    private void setConveyanceMode(int c) {
        switch (c) {
            case 1:
                toggleFlight.setChecked(true);
                break;
            case 2:
                toggleCar.setChecked(true);
                break;
            case 3:
                toggleTrain.setChecked(true);
                break;
            case 4:
                toggleShip.setChecked(true);
                break;
            case 5:
                toggleWalk.setChecked(true);
                break;
            case 6:
                toggleBus.setChecked(true);
                break;
            case 7:
                toggleBike.setChecked(true);
                break;
            case 8:
                toggleCarpet.setChecked(true);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.d(TAG, "returned from 'from' location");
                String result = data.getStringExtra("result");

                fromLocationList = Arrays.asList(result.split(","));
                fromLocation.setText(fromLocationList.get(0));
            } else if (requestCode == 2) {
                Log.d(TAG, "returned from 'to' location");
                String result = data.getStringExtra("result");

                toLocationList = Arrays.asList(result.split(","));
                toLocation.setText(toLocationList.get(0));
            }
        }
        if (resultCode == RESULT_CANCELED) {
            // Write your code if there's no result
        }

    }

}