package com.example.memories.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.example.memories.R;
import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.services.CustomResultReceiver;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.SessionManager;

import java.io.File;

public class SplashScreen extends Activity implements CustomResultReceiver.Receiver {
    private static final String TAG = "<SplashScreen>";
    public CustomResultReceiver mReceiver;
    private SessionManager session;
    private int REQUEST_FETCH_CONTACTS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);


        // Session class instance
        session = new SessionManager(getApplicationContext());

        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        createTravelJarInitials();

        // check if already logged in
        if (session.isLoggedIn(this)) {
            Log.d(TAG, "since already logged in");
            Log.d(TAG, "SplashScreen ==> TimelineFragment");
            Intent intent = new Intent(getBaseContext(), ActivejourneyList.class);
            startActivity(intent);
            finish();
        }

    }

    private void createTravelJarInitials() {
        // Create traveljar pictures folder
        File file;
        file = new File(Constants.TRAVELJAR_FOLDER_PICTURE);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar VIDEO folder
        file = new File(Constants.TRAVELJAR_FOLDER_VIDEO);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar AUDIO folder
        file = new File(Constants.TRAVELJAR_FOLDER_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar BUDDY PROFILES folder
        file = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
        if (!file.exists()) {
            file.mkdirs();
        }

        //If gumnaam image doesn't exists than create one
        HelpMe.createImageIfNotExist(this);
    }

    public void goToSignUp(View v) {
        Intent i = new Intent(getBaseContext(), SignUp.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void goToSignIn(View v) {
        Intent i = new Intent(getBaseContext(), SignIn.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == REQUEST_FETCH_CONTACTS) {
            Log.d(TAG, "fetch contacts service completed");
        }
        ;
    }
}
