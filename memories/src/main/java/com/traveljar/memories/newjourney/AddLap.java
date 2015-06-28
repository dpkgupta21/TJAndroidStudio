package com.traveljar.memories.newjourney;

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

import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.LapDataSource;
import com.traveljar.memories.models.Lap;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.volley.AppController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddLap extends AppCompatActivity {

    protected static final String TAG = "<AddLap>";
    private TextView fromLocation;
    private TextView toLocation;
    private TextView dateLocation;
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
    private boolean isSourceEdited;
    private boolean isDestinationEdited;
    private boolean isDateEdited;
    private List<String> fromLocationList;
    private List<String> toLocationList;
    private long epochTime;

    private Lap lap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_lap_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Lap");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(100);

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
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("d MMM yyyy", Locale.US);
        //Set the current date initially
        dateLocation.setText(dateFormatter.format(new Date()));

        epochTime = HelpMe.getCurrentTime();
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                isDateEdited = true;
                Calendar dateBirth = Calendar.getInstance();
                dateBirth.set(year, monthOfYear, dayOfMonth);
                epochTime = (dateBirth.getTimeInMillis() / 1000);
                Log.d(TAG, "epochTIme = " + epochTime + "!");
                dateLocation.setText(dateFormatter.format(dateBirth.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        if (getIntent().hasExtra("EDIT_LAP_ID")) {
            editMode = true;
//            lap = LapDataSource.getLapById(getIntent().getStringExtra("EDIT_LAP_ID"), this);
            lap = Lap.getLapFromLapsList(AppController.lapList, getIntent().getStringExtra("EDIT_LAP_ID"));
            dateLocation.setText(HelpMe.getDate(lap.getStartDate(), 1));
            fromLocation.setText(lap.getSourceCityName());
            toLocation.setText(lap.getDestinationCityName());
            Log.d(TAG, "editing lap with source " + lap.getSourceCityName() + lap.getDestinationCityName());
            setConveyanceMode(lap.getConveyanceMode());
        } else {
            //Set default conveyence mode to magical carpet
            lap = new Lap();
            lap.setConveyanceMode(HelpMe.CONVEYANCE_CARPET);
            toggleCarpet.setChecked(true);
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
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_FLIGHT);
                    conveyanceOff();
                    toggleFlight.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.carToggle:
                if (toggleCar.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_CAR);
                    conveyanceOff();
                    toggleCar.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.trainToggle:
                if (toggleTrain.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_TRAIN);
                    conveyanceOff();
                    toggleTrain.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.shipToggle:
                if (toggleShip.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_SHIP);
                    conveyanceOff();
                    toggleShip.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.walkToggle:
                if (toggleWalk.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_SHIP);
                    conveyanceOff();
                    toggleWalk.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.busToggle:
                if (toggleBus.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_BUS);
                    conveyanceOff();
                    toggleBus.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.bikeToggle:
                if (toggleBike.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_BIKE);
                    conveyanceOff();
                    toggleBike.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
                }
                break;
            case R.id.carpetToggle:
                if (toggleCarpet.isChecked()) {
                    lap.setConveyanceMode(HelpMe.CONVEYANCE_CARPET);
                    conveyanceOff();
                    toggleCarpet.setChecked(true);
                } else {
                    lap.setConveyanceMode(-1);
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
        if (lap.getConveyanceMode() == -1) {
            Toast.makeText(this, "Please select the conveyance mode", Toast.LENGTH_SHORT).show();
        } else {
            if(editMode){
                if(isSourceEdited){
                    setLapSourceInfo();
                }
                if(isDestinationEdited){
                    setLapDestinationInfo();
                }
                if(isDateEdited){
                    lap.setStartDate(epochTime);
                }
                LapDataSource.updateLap(lap, this);
            } else {
                setLapSourceInfo();
                setLapDestinationInfo();
                lap.setStartDate(epochTime);
                long id = LapDataSource.createLap(lap, this);
                lap.setId(String.valueOf(id));
                Log.d(TAG, "total laps in the database are " + LapDataSource.getAllLaps(this));
                AppController.lapList.add(lap);
            }

            Intent i = new Intent(getBaseContext(), LapsList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    private void setLapSourceInfo(){
        // Received location has city, state and country
        if (fromLocationList.size() == 3) {
            lap.setSourceCityName(fromLocationList.get(0));
            lap.setSourceStateName(fromLocationList.get(1));
            lap.setSourceCountryName(fromLocationList.get(2));
        }// Received locatino has ONLY state and country
        else {
            lap.setSourceCityName(fromLocationList.get(0));
            lap.setSourceStateName(fromLocationList.get(0));
            lap.setSourceCountryName(fromLocationList.get(1));
        }
    }

    private void setLapDestinationInfo(){
        if (toLocationList.size() == 3) {
            lap.setDestinationCityName(toLocationList.get(0));
            lap.setDestinationStateName(toLocationList.get(1));
            lap.setDestinationCountryName(toLocationList.get(2));
        } else {
            lap.setDestinationCityName(toLocationList.get(0));
            lap.setDestinationStateName(toLocationList.get(0));
            lap.setDestinationCountryName(toLocationList.get(1));
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
                isSourceEdited = true;
            } else if (requestCode == 2) {
                Log.d(TAG, "returned from 'to' location");
                String result = data.getStringExtra("result");

                toLocationList = Arrays.asList(result.split(","));
                toLocation.setText(toLocationList.get(0));
                isSourceEdited = true;
                isDestinationEdited = true;
            }
        }
        if (resultCode == RESULT_CANCELED) {
            // Write your code if there's no result
        }

    }

}