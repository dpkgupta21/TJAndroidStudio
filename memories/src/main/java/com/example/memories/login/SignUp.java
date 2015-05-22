package com.example.memories.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.memories.newjourney.LapsList;
import com.example.memories.newjourney.NewJourney;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.Constants;
import com.example.memories.utility.HelpMe;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;
import com.example.memories.volley.AppController;
import com.example.memories.volley.Const;
import com.example.memories.volley.CustomJsonRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUp extends Activity {

    // GCM -----------------------------------
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "<<SIgnUp>>";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = Constants.GOOGLE_PROJECT_NUMBER;
    GoogleCloudMessaging gcm;
    Context context;
    String regid = "";
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private EditText txtName;
    private SessionManager session;
    private Map<String, String> params;
    private String emailAddress;
    private String password;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        getActionBar().hide();

        // Session Manager
        session = new SessionManager(this);

        if (session.isLoggedIn(this)) {
            Intent i = new Intent(getBaseContext(), Timeline.class);
            startActivity(i);
            finish();
        }

        // auto populate email field with possible email address
        txtEmailAddress = (EditText) findViewById(R.id.signupEmailTxt);
        txtPassword = (EditText) findViewById(R.id.signupPasswordTxt);
        txtName = (EditText) findViewById(R.id.signupNameTxt);
        autoPopulateEmail(txtEmailAddress);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    public void goToSignIn(View v) {
        Intent i = new Intent(getBaseContext(), SignIn.class);
        startActivity(i);
        finish();
    }

    public void fbSignUp(View v) {
        Intent i = new Intent(getBaseContext(), LapsList.class);
        startActivity(i);
        finish();
    }

    private void getParams() {
        params = new HashMap<String, String>();
        params.put("user[name]", name);
        params.put("user[email]", emailAddress);
        params.put("user[password]", password);
        params.put("user[device_id]", "android");
        params.put("user[reg_id]", regid);
        Log.d(TAG, "reg id in params = " + regid);
    }

    public void signUp(View v) {
        // Get emailAddress, password from EditText
        emailAddress = txtEmailAddress.getText().toString();
        password = txtPassword.getText().toString();
        name = txtName.getText().toString();

        // Check if emailAddress and password is filled
        if (emailAddress.trim().length() > 0 && password.trim().length() > 0
                && name.trim().length() > 0) {

            // Check for Internet connection otherwise no use in making request
            if (HelpMe.isNetworkAvailable(this)) {

                final ProgressDialog pDialog = new ProgressDialog(this);
                pDialog.setMessage("Loading...");
                pDialog.show();

                // Get a GCM registration id
                startRegistrationOfGCM(getApplicationContext());

                //set the default profile image
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
                TJPreferences.setProfileImgPath(SignUp.this, Constants.GUMNAAM_IMAGE_URL);

            } else {
                Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG)
                        .show();
            }
        } else {
            // user didn't entered emailAddress or password
            // Show alert asking him to enter the details
            Toast.makeText(getApplicationContext(), "Please enter Name, emailAddress & password",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void makeRequestOnServer() {
        // assemble all params
        getParams();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Const.URL_SIGN_UP;

        // Request a string response from the provided URL.
        CustomJsonRequest signUpReg = new CustomJsonRequest(Request.Method.POST, url, params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "=====" + response.toString());
                        try {
                            createNewUserInDB(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), NewJourney.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        //AppController.getInstance().getRequestQueue().add(signUpReg);
        queue.add(signUpReg);

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

    private void createNewUserInDB(JSONObject res) throws JSONException {
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
    }

    // GCM methods and declarations
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
                String msg = "";
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
                    makeRequestOnServer();

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
            }
        }.execute(null, null, null);
    }

}
