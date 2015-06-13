package com.example.memories.newjourney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import com.example.memories.R;
import com.example.memories.SQLitedatabase.PlaceDataSource;
import com.example.memories.models.Place;
import com.example.memories.newjourney.adapters.LocationListAdapter;
import com.example.memories.newjourney.adapters.PlacesAutoCompleteAdapter;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.TJPreferences;

import java.util.Arrays;
import java.util.List;

public class LocationList extends AppCompatActivity {

    private LocationListAdapter locationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_location_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Select Location");
        setSupportActionBar(toolbar);

        AutoCompleteTextView autocompleteView = (AutoCompleteTextView) findViewById(R.id.new_journey_location_list_autocomplete);
        autocompleteView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.new_journey_location_list_autocomplete_item));

        autocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get data associated with the specified position
                // in the list (AdapterView)
                String description = (String) parent.getItemAtPosition(position);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", description);

                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

//        final ArrayList<String> locationList = new ArrayList<>();
//        int len = getResources().getStringArray(R.array.places_list).length;
//        for (int i = 0; i < len; i++) {
//            locationList.add(getResources().getStringArray(R.array.places_list)[i]);
//        }
//
//        ListView locationListView = (ListView) findViewById(R.id.new_journey_location_list);
//        locationListAdapter = new LocationListAdapter(this, locationList);
//        locationListView.setAdapter(locationListAdapter);
//
//        locationListView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // selected item
//                String placeName = locationList.get(position);
//
//                Intent returnIntent = new Intent();
//                returnIntent.putExtra("result", placeName);
//                setResult(RESULT_OK, returnIntent);
//                finish();
//            }
//        });

    }

    private long createNewPlaceInDB(String desc) {
        List<String> placeDetails;
        placeDetails = Arrays.asList(desc.split(","));

        int len = placeDetails.size();
        String city = null;
        if (len == 3) {
            city = placeDetails.get(0);
        }
        Place newPlace = new Place(null, null, null, placeDetails.get(len - 1), placeDetails.get(len - 2), city, TJPreferences.getUserId(getBaseContext()), HelpMe.getCurrentTime());
        return PlaceDataSource.createPlace(newPlace, getBaseContext());
    }

}