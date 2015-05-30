package com.example.memories.utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.memories.login.NumberVerificationActivity2;

/**
 * Created by abhi on 30/05/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    private String TAG = SMSReceiver.class.getSimpleName();

    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get the data (SMS data) bound to intent
        Log.d(TAG, "on Receive in SMSReceiever");
        Bundle bundle = intent.getExtras();

        SmsMessage[] msgs;

        String msg = "";
        String phoneNumber = "";

        if (bundle != null) {
            // Retrieve the SMS Messages received
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];

            // For every SMS message received
            for (int i = 0; i < msgs.length; i++) {
                // Convert Object array
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                // Sender's phone number
                phoneNumber = msgs[i].getOriginatingAddress();
                // Fetch the text message
                msg = msgs[i].getMessageBody().toString();
                // Newline <img src="http://codetheory.in/wp-includes/images/smilies/simple-smile.png" alt=":-)" class="wp-smiley" style="height: 1em; max-height: 1em;">
            }

            // Display the entire SMS Message
            Log.d(TAG, msg);
            String verifyingText = "Welcome to TravelJar";
            if (msg.equals(verifyingText)) {
                TJPreferences.setPhone(context, phoneNumber);
                Intent i = new Intent(context, NumberVerificationActivity2.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            } else {
                Log.d(TAG, "wrong message receieved");
            }
        }
    }
}
