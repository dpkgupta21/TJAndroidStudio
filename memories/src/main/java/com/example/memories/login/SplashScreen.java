package com.example.memories.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.memories.R;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.SessionManager;

public class SplashScreen extends Activity {
    private static final String TAG = "<SplashScreen>";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        getActionBar().hide();

        // Session class instance
        session = new SessionManager(getApplicationContext());

        // check if already logged in
        if (session.isLoggedIn(this)) {
            Log.d(TAG, "since already logged in");
            Log.d(TAG, "SplashScreen ==> Timeline");
            Intent i = new Intent(getBaseContext(), Timeline.class);
            startActivity(i);
            finish();
        } else {

			/*
             * Creates a new Intent to start the RSSPullService IntentService.
			 * Passes a URI in the Intent's "data" field.
			 */
            /*Intent mServiceIntent = new Intent(getBaseContext(), PullContactsService.class);
            startService(mServiceIntent);*/
        }

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

}
