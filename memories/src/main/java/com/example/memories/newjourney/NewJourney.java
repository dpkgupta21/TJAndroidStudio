package com.example.memories.newjourney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.memories.R;

public class NewJourney extends Activity {

    private static final String TAG = "<NewJourney>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_journey);

    }

    public void embarkNewJourney(View v) {
        Intent i = new Intent(getBaseContext(), AddLap.class);
        startActivity(i);
    }
}