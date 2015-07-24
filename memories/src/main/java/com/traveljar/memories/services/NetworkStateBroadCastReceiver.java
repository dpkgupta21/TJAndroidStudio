package com.traveljar.memories.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by ankit on 22/6/15.
 */
public class NetworkStateBroadCastReceiver extends BroadcastReceiver {

    private static final String TAG = "NetStateBroadCastRec";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (checkInternet(context)) {
            Log.d(TAG, "Network Available");
            Intent i = new Intent(context, MakeServerRequestsService.class);
            context.startService(i);
        } else {
            Log.d(TAG, "Network gone");
        }

    }

    boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

}

class ServiceManager extends ContextWrapper {

    public ServiceManager(Context base) {
        super(base);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}