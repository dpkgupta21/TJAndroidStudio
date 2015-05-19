package com.example.memories.services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

public class LastLocation extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    protected static final String TAG = "basic-location-sample";
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private Context mContext;

    public Location getLastKnownLocation(Context mContext) {

        this.mContext = mContext;
        Log.d(TAG, mContext + "!");
        buildGoogleApiClient();
        Log.d(TAG, "7");
        return mLastLocation;

    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "0");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        Log.d(TAG, "1");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "2");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "3");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well
        // suited for
        // applications that do not require a fine-grained location and that do
        // not need location
        // updates. Gets the best and most recent location currently available,
        // which may be null
        // in rare cases when a location is not available.
        Log.d(TAG, "4");
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            // mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            // mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes
        // might be returned in
        // onConnectionFailed.
        Log.d(TAG, "5");
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /*
     * Called by Google Play services if the connection to GoogleApiClient drops
     * because of an error.
     */
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We
        // call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "6");
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

}
