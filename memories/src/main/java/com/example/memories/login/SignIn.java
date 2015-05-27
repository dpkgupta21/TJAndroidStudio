package com.example.memories.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.memories.R;
import com.example.memories.SQLitedatabase.JourneyDataSource;
import com.example.memories.services.PullContactsService;
import com.example.memories.services.PullMemoriesService;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

public class SignIn extends Activity implements CustomResultReceiver.Receiver {

    protected static final String TAG = null;
    public CustomResultReceiver mReceiver;
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private SessionManager session;
    private boolean contactsFetched = false;
    private boolean memoriesFetched = false;
    private int REQUEST_FETCH_CONTACTS = 1;
    private int REQUEST_FETCH_MEMORIES = 2;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        //getActionBar().hide();

        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        // Session Manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn(this)) {
            Intent i = new Intent(getBaseContext(), Timeline.class);
            startActivity(i);
            finish();
        }

        // auto populate email field with possible email address
        txtEmailAddress = (EditText) findViewById(R.id.signInEmailTxt);
        txtPassword = (EditText) findViewById(R.id.signInPasswordTxt);
        autoPopulateEmail(txtEmailAddress);

        pDialog = new ProgressDialog(this);

    }

    public void goToSignUp(View v) {
        Intent i = new Intent(getBaseContext(), SignUp.class);
        startActivity(i);
        finish();
    }

    public void fbSignIn(View v) {

    }

    public void signIn(View v) {
        Log.d(TAG, "signIn() method called");

        pDialog.show();
        // Get username, password from EditText
        final String emailAddress = txtEmailAddress.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Add the request to the RequestQueue.
        // But before that check for Internet connection
        if (HelpMe.isNetworkAvailable(this)) {
            // Check if username, password is filled
            if (emailAddress.length() > 0 && password.length() > 0) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                String url = Constants.URL_SIGN_IN + "?email=" + emailAddress + "&password=" + password + "&reg_id=1234";
                Log.d(TAG, url);

                pDialog.show();

                CustomJsonRequest jsonObjReq = new CustomJsonRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());
                                try {
                                    updateUserPref(response);
                                    Log.d(TAG, "calling pullMemoriesService to fetcch all journeys and their memories");
                                    Intent intent = new Intent(getBaseContext(), PullMemoriesService.class);
                                    intent.putExtra("RECEIVER", mReceiver);
                                    intent.putExtra("REQUEST_CODE", REQUEST_FETCH_MEMORIES);
                                    startService(intent);

                                    Log.d(TAG, "calling pullContactsService to fetcch all journeys and their memories");
                                    Intent mServiceIntent = new Intent(getBaseContext(), PullContactsService.class);
                                    mServiceIntent.putExtra("RECEIVER", mReceiver);
                                    mServiceIntent.putExtra("REQUEST_CODE", REQUEST_FETCH_CONTACTS);
                                    startService(mServiceIntent);

                                } catch (JSONException e) {
                                    Log.d(TAG, "everything fine upto here 4");
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                TJPreferences.setActiveJourneyId(SignIn.this, "21");
                                // Staring MainActivity
/*                                Intent i = new Intent(getApplicationContext(), Timeline.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);*/
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(),
                                "Username & password donot match!", Toast.LENGTH_LONG)
                                .show();
                    }
                });

                queue.add(jsonObjReq);

            } else {
                // username / password doesn't match
                Toast.makeText(getApplicationContext(), "Please enter username/password",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void autoPopulateEmail(EditText emailTxt) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accountList = manager.getAccounts();
        for (Account account : accountList) {
            if (emailPattern.matcher(account.name).matches()) {
                String possibleEmail = account.name;
                emailTxt.setText(possibleEmail);
            }
        }
    }

    private void updateUserPref(JSONObject res) throws JSONException {
        JSONObject userItem = res.getJSONObject("user_register");
        String id = userItem.getString("id");
        String name = userItem.getString("name");
        String email = userItem.getString("email");
        String phone = userItem.getString("phone");
        String api_key = userItem.getString("api_key");
        String currentJourney = JourneyDataSource.getCurrentJourney(getApplicationContext());

        TJPreferences.setUserId(this, id);
        TJPreferences.setUserName(this, name);
        TJPreferences.setEmail(this, email);
        TJPreferences.setPhone(this, phone);
        TJPreferences.setApiKey(this, api_key);
        TJPreferences.setLoggedIn(this, true);
        TJPreferences.setActiveJourneyId(this, currentJourney);

        //setting the profile image
        if (!(new File(Constants.GUMNAAM_IMAGE_URL)).exists()) {
            //check whether the dir exists
            File dir = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_profile);
            File file = new File(Constants.GUMNAAM_IMAGE_URL);
            FileOutputStream outStream = null;
            try {
                outStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.flush();
                outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TJPreferences.setProfileImgPath(this, Constants.GUMNAAM_IMAGE_URL);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == REQUEST_FETCH_CONTACTS) {
            Log.d(TAG, "fetch contacts service completed");
            contactsFetched = true;
        } else if (resultCode == REQUEST_FETCH_MEMORIES) {
            Log.d(TAG, "fetch memories service completed");
            memoriesFetched = true;
        }
        if (contactsFetched && memoriesFetched) {
            pDialog.dismiss();
            Intent i = new Intent(getApplicationContext(), Timeline.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        Log.d(TAG, "sign in contacts fetched successfully");
    }
}


