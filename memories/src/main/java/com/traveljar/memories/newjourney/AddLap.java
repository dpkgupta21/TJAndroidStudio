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
import com.traveljar.memories.SQLitedatabase.LapsDataSource;
import com.traveljar.memories.SQLitedatabase.PlaceDataSource;
import com.traveljar.memories.models.Laps;
import com.traveljar.memories.models.Place;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.JourneyUtil;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

    private Laps laps;

    private boolean isJourneyCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_lap_new);

        isJourneyCreated = getIntent().getBooleanExtra("isJourneyCreated", false);

        setUpToolBar();

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
            if (isJourneyCreated) {
                laps = LapsDataSource.getLapsByIdWithPlace(this, getIntent().getStringExtra("EDIT_LAP_ID"));
            } else {
                laps = Laps.getLapFromLapsList(AppController.lapsList, getIntent().getStringExtra("EDIT_LAP_ID"));
            }
            dateLocation.setText(HelpMe.getDate(laps.getStartDate(), 1));
            fromLocation.setText(laps.getSourceCityName());
            toLocation.setText(laps.getDestinationCityName());
            Log.d(TAG, "editing     lap with source " + laps.getSourceCityName() + laps.getDestinationCityName());
            setConveyanceMode(laps.getConveyanceMode());
        } else {
            //Set default conveyence mode to magical carpet
            laps = new Laps();
            laps.setConveyanceMode(HelpMe.CONVEYANCE_CARPET);
            toggleCarpet.setChecked(true);
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
        title.setText("New Lap");
    }

    public void listFromPlaces(View v) {
        if (HelpMe.isNetworkAvailable(this)) {
            Intent i = new Intent(getBaseContext(), LocationList.class);
            startActivityForResult(i, 1);
        } else {
            Toast.makeText(this, "please switch ON packet data to continue", Toast.LENGTH_LONG).show();
        }
    }

    public void listToPlaces(View v) {
        if (HelpMe.isNetworkAvailable(this)) {
            Intent i = new Intent(getBaseContext(), LocationList.class);
            startActivityForResult(i, 2);
        } else {
            Toast.makeText(this, "please switch ON packet data to continue", Toast.LENGTH_LONG).show();
        }
    }

    public void conveyanceToggle(View v) {
        switch (v.getId()) {
            case R.id.flightToggle:
                if (toggleFlight.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_FLIGHT);
                    conveyanceOff();
                    toggleFlight.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.carToggle:
                if (toggleCar.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_CAR);
                    conveyanceOff();
                    toggleCar.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.trainToggle:
                if (toggleTrain.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_TRAIN);
                    conveyanceOff();
                    toggleTrain.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.shipToggle:
                if (toggleShip.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_SHIP);
                    conveyanceOff();
                    toggleShip.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.walkToggle:
                if (toggleWalk.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_WALK);
                    conveyanceOff();
                    toggleWalk.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.busToggle:
                if (toggleBus.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_BUS);
                    conveyanceOff();
                    toggleBus.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.bikeToggle:
                if (toggleBike.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_BIKE);
                    conveyanceOff();
                    toggleBike.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
                }
                break;
            case R.id.carpetToggle:
                if (toggleCarpet.isChecked()) {
                    laps.setConveyanceMode(HelpMe.CONVEYANCE_CARPET);
                    conveyanceOff();
                    toggleCarpet.setChecked(true);
                } else {
                    laps.setConveyanceMode(-1);
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
        if (laps.getConveyanceMode() == -1) {
            Toast.makeText(this, "Please select the conveyance mode", Toast.LENGTH_SHORT).show();
        } else {
            if (editMode) {
                if (isDateEdited) {
                    laps.setStartDate(epochTime);
                }
                if (isSourceEdited) {
                    setLapSourceInfo();
                }
                if (isDestinationEdited) {
                    setLapDestinationInfo();
                }

                if (isJourneyCreated) {
                    LapsDataSource.updateLaps(laps, this);
                } else {
                    LapsDataSource.updateLaps(laps, this);
                }
            } else {
                laps.setStartDate(epochTime);
                setLapSourceInfo();
                setLapDestinationInfo();


                long id = LapsDataSource.createLap(laps, this);
                laps.setId(String.valueOf(id));
                //Log.d(TAG, "total laps in the database are " + LapDataSource.getAllLaps(this));
                if (isJourneyCreated) {
                    JourneyUtil.updateJourneyLap(this, createParams(laps));
                } else {
                    AppController.lapsList.add(laps);
                }
            }

           /* Intent i = new Intent(getBaseContext(), LapsList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);*/

            finish();
        }
    }

    private void setLapSourceInfo() {
        // Received location has city, state and country
        if (fromLocationList.size() == 3) {
            laps.setSourceCityName(fromLocationList.get(0));
            laps.setSourceStateName(fromLocationList.get(1));
            laps.setSourceCountryName(fromLocationList.get(2));
        }// Received locatino has ONLY state and country
        else {
            laps.setSourceCityName(fromLocationList.get(0));
            laps.setSourceStateName(fromLocationList.get(0));
            laps.setSourceCountryName(fromLocationList.get(1));


        }

        Place sourcePlace = new Place(null, null, laps.getSourceCountryName(), laps.getSourceStateName(),
                laps.getSourceCityName(), laps.getStartDate(), 0, 0);
        long sourcePlaceId = PlaceDataSource.createPlace(sourcePlace, AddLap.this);

        laps.setSourcePlaceId(String.valueOf(sourcePlaceId));
    }

    private void setLapDestinationInfo() {
        // Received location has city, state and country
        if (toLocationList.size() == 3) {
            laps.setDestinationCityName(toLocationList.get(0));
            laps.setDestinationStateName(toLocationList.get(1));
            laps.setDestinationCountryName(toLocationList.get(2));
        }// Received locatino has ONLY state and country
        else {
            laps.setDestinationCityName(toLocationList.get(0));
            laps.setDestinationStateName(toLocationList.get(0));
            laps.setDestinationCountryName(toLocationList.get(1));
        }

        Place destinationPlace = new Place(null, null, laps.getDestinationCountryName(),
                laps.getDestinationStateName(),
                laps.getDestinationCityName(),
                laps.getStartDate(), 0, 0);
        long destinationPlaceId = PlaceDataSource.createPlace(destinationPlace, AddLap.this);

        laps.setDestinationPlaceId(String.valueOf(destinationPlaceId));
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

    private HashMap createParams(Laps laps) {

        // create params to be sent in update  lap in  journey api
        HashMap<String, String> params = new HashMap<>();
        try {
            params.put("api_key", TJPreferences.getApiKey(getBaseContext()));
            params.put("journey_lap[travel_mode]", String.valueOf(laps.getConveyanceMode()));
            params.put("journey_lap[time_of_day]", String.valueOf(laps.getStartDate()));
            params.put("journey_lap[start_date]", String.valueOf(laps.getStartDate()));
            // params.put("journey_lap[end_date]", String.valueOf(laps.getStartDate()));

            // Get Lap local id
            params.put("journey_lap[journey_laps_attributes[0]][journey_lap_local_id]",
                    laps.getId());

            // Get source info
            params.put("journey_lap[journey_laps_attributes[0]][source_city_name]",
                    laps.getSourceCityName());
            params.put("journey_lap[journey_laps_attributes[0]][source_state_name]",
                    laps.getSourceStateName());
            params.put("journey_lap[journey_laps_attributes[0]][source_country_name]",
                    laps.getSourceCountryName());

            // Get destination info
            params.put("journey_lap[journey_laps_attributes[0]][destination_city_name]",
                    laps.getDestinationCityName());
            params.put("journey_lap[journey_laps_attributes[0]][destination_state_name]",
                    laps.getDestinationStateName());
            params.put("journey_lap[journey_laps_attributes[0]][destination_country_name]",
                    laps.getDestinationCountryName());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

}