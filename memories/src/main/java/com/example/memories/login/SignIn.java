package com.example.memories.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.memories.activejourney.ActivejourneyList;
import com.example.memories.services.CustomResultReceiver;
import com.example.memories.services.PullMemoriesService;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.CustomJsonRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

public class SignIn extends Activity implements CustomResultReceiver.Receiver {

    protected static final String TAG = null;
    // GCM -----------------------------------
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public CustomResultReceiver mReceiver;
    String SENDER_ID = Constants.GOOGLE_PROJECT_NUMBER;
    GoogleCloudMessaging gcm;
    Context context;
    String regid = "";
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private int REQUEST_FETCH_MEMORIES = 2;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);


        mReceiver = new CustomResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        // Session Manager
        SessionManager session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn(this)) {
            Intent i = new Intent(getBaseContext(), ActivejourneyList.class);
            startActivity(i);
            finish();
        }

        // auto populate email field with possible email address
        txtEmailAddress = (EditText) findViewById(R.id.signInEmailTxt);
        txtPassword = (EditText) findViewById(R.id.signInPasswordTxt);
        autoPopulateEmail(txtEmailAddress);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
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
        // Get a GCM registration id
        startRegistrationOfGCM(getApplicationContext());
    }


    public void makeRequestToServer() {
        if(HelpMe.isNetworkAvailable(this)) {
            Log.d(TAG, "makeREqusttoServer method called" + regid);
            pDialog = new ProgressDialog(this);
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
                    String url = Constants.URL_SIGN_IN + "?email=" + emailAddress + "&password=" + password + "&reg_id=" + regid;
                    Log.d(TAG, url);

                    pDialog.show();

                    CustomJsonRequest jsonObjReq = new CustomJsonRequest(Request.Method.GET, url, null,
                            new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        updateUserPref(response);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d(TAG, response.toString());
                                    Log.d(TAG, "calling pullMemoriesService to fetch all journeys and their memories");
                                    if(HelpMe.isNetworkAvailable(SignIn.this)) {
                                        Intent mServiceIntent = new Intent(getBaseContext(), PullMemoriesService.class);
                                        mServiceIntent.putExtra("REQUEST_CODE", REQUEST_FETCH_MEMORIES);
                                        mServiceIntent.putExtra("RECEIVER", mReceiver);
                                        startService(mServiceIntent);
                                    }else{
                                        Toast.makeText(SignIn.this, "Network unavailable please turn on your data", Toast.LENGTH_SHORT).show();
                                    }
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
        }else{
            Toast.makeText(this, "Network unavailable please turn on your data", Toast.LENGTH_SHORT).show();
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

        TJPreferences.setUserId(this, id);
        TJPreferences.setUserName(this, name);
        TJPreferences.setEmail(this, email);
        TJPreferences.setPhone(this, phone);
        TJPreferences.setApiKey(this, api_key);
        TJPreferences.setLoggedIn(this, true);

        // download & set the default profile image if does not exist
        HelpMe.createImageIfNotExist(this);
        TJPreferences.setProfileImgPath(this, Constants.GUMNAAM_IMAGE_URL);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        if (resultCode == REQUEST_FETCH_MEMORIES) {
            pDialog.dismiss();
            Intent i = new Intent(getApplicationContext(), ActivejourneyList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        Log.d(TAG, "signed in");
    }

    // GCM methods and declarations
    // -------------------------------------------------------------------
    public void startRegistrationOfGCM(Context mContext) {

        context = mContext;

        // Check device for Play Services APK. If check succeeds, proceed with
        // GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context) {
        String registrationId = TJPreferences.getGcmRegId(context);
        if (registrationId == null) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = TJPreferences.getAppVersion(context);
        int currentVersion = HelpMe.getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over
                    // HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    // sendRegistrationIdToBackend();


                    // For this demo: we don't need to send it because the
                    // device will send
                    // upstream messages to a server that echo back the message
                    // using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    Log.d(TAG, "usre eris id isss === " + regid);
                    TJPreferences.setGcmRegId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.d(TAG, msg + "\n");
                makeRequestToServer();
            }
        }.execute(null, null, null);
    }

}

