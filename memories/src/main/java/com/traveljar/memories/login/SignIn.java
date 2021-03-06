package com.traveljar.memories.login;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.traveljar.memories.R;
import com.traveljar.memories.SQLitedatabase.ContactDataSource;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.models.Contact;
import com.traveljar.memories.services.PullContactsService;
import com.traveljar.memories.services.PullMemoriesService;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.SessionManager;
import com.traveljar.memories.utility.TJPreferences;
import com.traveljar.memories.volley.AppController;
import com.traveljar.memories.volley.CustomJsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class SignIn extends Activity implements PullMemoriesService.OnTaskFinishListener {

    protected static final String TAG = "SignIn";
    // GCM -----------------------------------
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int REQUEST_FETCH_MEMORIES = 1;
    private static int REQUEST_DOWNLOAD_PROFILE = 2;
    String SENDER_ID = Constants.GOOGLE_PROJECT_NUMBER;
    GoogleCloudMessaging gcm;
    Context context;
    String regid = "";
    private EditText txtEmailAddress;
    private EditText txtPassword;
    private ProgressDialog pDialog;
    private boolean isProfilePicDownloaded;
    private boolean isMemoryFetched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

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

    public void signIn(View v) {
        Log.d(TAG, "signIn() method called");

        if (!HelpMe.isNetworkAvailable(SignIn.this)) {
            Log.d(TAG, "no internet availablae");
            Toast.makeText(SignIn.this, "Network unavailable please turn on your data", Toast.LENGTH_SHORT).show();
        } else {
            pDialog = new ProgressDialog(this);
            pDialog.setTitle("Loading your memories...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
            // Get a GCM registration id
            startRegistrationOfGCM(getApplicationContext());
        }
    }


    public void makeRequestToServer() {
        // Get username, password from EditText
        final String emailAddress = txtEmailAddress.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        // Check if username, password is filled
        if (emailAddress.length() > 0 && password.length() > 0) {
            // Instantiate the RequestQueue.
            String url = Constants.URL_SIGN_IN + "?email=" + emailAddress + "&password=" + password + "&reg_id=" + regid;
            Log.d(TAG, url);

            CustomJsonRequest jsonObjReq = new CustomJsonRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d(TAG, "got some response = " + response.toString());
                                saveUser(response);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d(TAG, "1.3");
                            // Fetch all contacts and memories and contacts
                            Intent intent = new Intent(getBaseContext(), PullContactsService.class);
                            intent.putExtra("ACTIVITY_CODE", 3);
                            Log.d(TAG, "1.4");
                            startService(intent);
                            new PullMemoriesService(SignIn.this, SignIn.this, REQUEST_FETCH_MEMORIES).fetchJourneys();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "Username & password donot match!", Toast.LENGTH_LONG)
                            .show();
                }
            });

            Log.d(TAG, "1.5");
            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            if (HelpMe.isNetworkAvailable(SignIn.this)) {
                AppController.getInstance().getRequestQueue().add(jsonObjReq);
            } else {
                Toast.makeText(SignIn.this, "Network unavailable please turn on your data", Toast.LENGTH_SHORT).show();
            }


        } else {
            // username / password doesn't match
            pDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Please enter username/password",
                    Toast.LENGTH_LONG).show();
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

    private void saveUser(JSONObject res) throws JSONException {
        Log.d(TAG, "1.6");
        JSONObject userItem = res.getJSONObject("user_register");
        final String id = userItem.getString("id");
        String name = userItem.getString("name");
        String email = userItem.getString("email");
        String phone = userItem.getString("phone");
        String api_key = userItem.getString("api_key");
        String status = userItem.getString("status");
        status = (status.equals("null")) ? "Those who travel are not lost" : status;
        String interest = userItem.getString("interests");
        final String picServerUrl = userItem.getJSONObject("profile_picture").getJSONObject("original").getString("url");
        String picLocalUrl;
        String allJourneyIds = userItem.getJSONArray("journey_ids").toString();


        // Fetching the profile image of the user
        if (!picServerUrl.equals("null")) {
            picLocalUrl = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES + id + ".jpeg";
            TJPreferences.setProfileImgPath(this, picLocalUrl);
            ImageRequest request = new ImageRequest(picServerUrl,
                    new Response.Listener<Bitmap>() {
                        @Override
                        public void onResponse(Bitmap bitmap) {
                            FileOutputStream out = null;
                            try {
                                File tjDir = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
                                if (!tjDir.exists()) {
                                    tjDir.mkdirs();
                                }
                                String fileName = Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES
                                        + id + ".jpeg";
                                out = new FileOutputStream(fileName);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                onFinishTask(REQUEST_DOWNLOAD_PROFILE);
                                TJPreferences.setProfileImgPath(SignIn.this, fileName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 0, 0, null, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    //TODO on error response update the picLocalUrl in database as well as preference as null
                    HelpMe.createImageIfNotExist(SignIn.this);
                    onFinishTask(REQUEST_DOWNLOAD_PROFILE);
                }
            });
            AppController.getInstance().addToRequestQueue(request);
        } else {
            // check whether the gumnaam image already exists
            HelpMe.createImageIfNotExist(this);
            onFinishTask(REQUEST_DOWNLOAD_PROFILE);
        }
        Contact contact = new Contact(id, name, name, email, status, picServerUrl, null, phone, allJourneyIds, false, interest);
        ContactDataSource.createContact(contact, this);

        //saving the preferences
        TJPreferences.setUserId(this, id);
        TJPreferences.setUserName(this, name);
        TJPreferences.setEmail(this, email);
        TJPreferences.setPhone(this, phone);
        TJPreferences.setApiKey(this, api_key);
        TJPreferences.setLoggedIn(this, true);
        TJPreferences.setUserStatus(this, status);

        // download & set the default profile image if does not exist
        HelpMe.createImageIfNotExist(this);
        TJPreferences.setProfileImgPath(this, Constants.GUMNAAM_IMAGE_URL);
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

    /*
     * Gets the current registration ID for application on GCM service, if there is one.
     * If result is empty, the app needs to register.
     * @return registration ID, or empty string if there is no existing registration ID. */
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

    @Override
    public void onFinishTask(int requestCode) {
        Log.d(TAG, "on Finish Task called with request code " + requestCode);
        if (requestCode == REQUEST_FETCH_MEMORIES) {
            isMemoryFetched = true;
        }
        if (requestCode == REQUEST_DOWNLOAD_PROFILE) {
            isProfilePicDownloaded = true;
        }
        if (isMemoryFetched && isProfilePicDownloaded) {
            pDialog.dismiss();
            Intent i = new Intent(getApplicationContext(), ActivejourneyList.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }
}