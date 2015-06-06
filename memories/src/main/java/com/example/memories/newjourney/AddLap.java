package com.example.memories.newjourney;

import android.app.ActionBar;
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
import android.widget.ToggleButton;

import com.example.memories.R;
import com.example.memories.utility.HelpMe;
import com.example.memories.volley.AppController;

import java.text.SimpleDateFormat;
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
    private String conveyanceMode;
    private DatePickerDialog datePickerDialog;
    private ToggleButton toggleCar;
    private ToggleButton toggleFlight;
    private ToggleButton toggleShip;
    private ToggleButton toggleTrain;
    private ActionBar actionBar;
    private ToggleButton toggleMorning;
    private ToggleButton toggleAfternoon;
    private ToggleButton toggleEvening;
    private ToggleButton toggleNight;
    private String timeOfDayMode;

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
        toggleMorning = (ToggleButton) findViewById(R.id.morningToggle);
        toggleAfternoon = (ToggleButton) findViewById(R.id.afternoonToggle);
        toggleEvening = (ToggleButton) findViewById(R.id.eveningToggle);
        toggleNight = (ToggleButton) findViewById(R.id.nightToggle);

        // Set up date picker
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM, ''yy", Locale.US);
        datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar dateBirth = Calendar.getInstance();
                dateBirth.set(year, monthOfYear, dayOfMonth);
                dateLocation.setText(dateFormatter.format(dateBirth.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
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
        conveyanceOff();
        switch (v.getId()) {
            case R.id.flightToggle:
                toggleFlight.setChecked(true);
                conveyanceMode = HelpMe.CONVEYANCE_FLIGHT;
                break;
            case R.id.carToggle:
                toggleCar.setChecked(true);
                conveyanceMode = HelpMe.CONVEYANCE_CAR;
                break;
            case R.id.trainToggle:
                toggleTrain.setChecked(true);
                conveyanceMode = HelpMe.CONVEYANCE_TRAIN;
                break;
            case R.id.shipToggle:
                toggleShip.setChecked(true);
                conveyanceMode = HelpMe.CONVEYANCE_SHIP;
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
    }

    public void timeOfDayToggle(View v) {
        timeOfDayOff();
        Log.d(TAG, "timeOfDayToggle called");
        switch (v.getId()) {
            case R.id.morningToggle:
                toggleMorning.setChecked(true);
                timeOfDayMode = HelpMe.TIME_OF_DAY_MORNING;
                break;
            case R.id.afternoonToggle:
                toggleAfternoon.setChecked(true);
                timeOfDayMode = HelpMe.TIME_OF_DAY_AFTERNOON;
                break;
            case R.id.eveningToggle:
                toggleEvening.setChecked(true);
                timeOfDayMode = HelpMe.TIME_OF_DAY_EVENING;
                break;
            case R.id.nightToggle:
                toggleNight.setChecked(true);
                timeOfDayMode = HelpMe.TIME_OF_DAY_NIGHT;
                break;
            default:
                break;
        }
    }

    // toggles off all the timeOfDay modes
    private void timeOfDayOff() {
        toggleMorning.setChecked(false);
        toggleAfternoon.setChecked(false);
        toggleEvening.setChecked(false);
        toggleNight.setChecked(false);
    }

    // opens the calendar mode to select date
    public void showCalendar(View v) {
        datePickerDialog.show();
    }

    // save the details
    // And send back them to LapsList list screen
    public void updateDone(View v) {
        Intent i = new Intent(getBaseContext(), LapsList.class);

        Map<String, String> map = new HashMap<String, String>();
        map.put("from", fromLocation.getText().toString());
        map.put("to", toLocation.getText().toString());
        map.put("date", dateLocation.getText().toString());
        map.put("conveyance", conveyanceMode);
        map.put("timeOfTheDay", timeOfDayMode);
        ((AppController) getApplicationContext()).lapsList.add(map);

        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                String result = data.getStringExtra("result");

                List<String> placeDetails = Arrays.asList(result.split(","));
                int len = placeDetails.size();
                fromLocation.setText(placeDetails.get(0));
            } else if (requestCode == 2) {
                String result = data.getStringExtra("result");

                List<String> placeDetails = Arrays.asList(result.split(","));
                int len = placeDetails.size();
                toLocation.setText(placeDetails.get(0));
            }
        }
        if (resultCode == RESULT_CANCELED) {
            // Write your code if there's no result
        }

    }// onActivityResult

}