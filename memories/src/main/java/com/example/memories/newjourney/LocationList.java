package com.example.memories.newjourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.memories.R;
import com.example.memories.newjourney.adapters.LocationListAdapter;

import java.util.ArrayList;

public class LocationList extends Activity {

    private LocationListAdapter locationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey_location_list);

        final ArrayList<String> locationList = new ArrayList<String>();
        int len = getResources().getStringArray(R.array.places_list).length;
        for (int i = 0; i < len; i++) {
            locationList.add(getResources().getStringArray(R.array.places_list)[i]);
        }

        ListView locationListView = (ListView) findViewById(R.id.new_journey_location_list);
        locationListAdapter = new LocationListAdapter(this, locationList);
        locationListView.setAdapter(locationListAdapter);

        locationListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String placeName = locationList.get(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", placeName);
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        });

    }

}