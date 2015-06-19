package com.traveljar.memories.login;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.HelpMe;

public class NumberVerificationActivity extends Activity {

    private static final String TAG = "<NumberVer1>";
    EditText countryCode;
    EditText phoneNumber;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    private TextView timerCountDownTxt;
    private TextView verifyingtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification_1);

        countryCode = (EditText) findViewById(R.id.number_ver1_country_code);
        phoneNumber = (EditText) findViewById(R.id.number_ver1_phone_number);

    }

    public void verifyPhone(View v) {
        Log.d(TAG, "verifying phone number now with phone number = " + phoneNumber);

        if (!HelpMe.isValidMobile(phoneNumber.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(), "Please enter a valid mobile number of 10 digits", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        sendSMS(phoneNumber.getText().toString().trim(), "Welcome to TravelJar");

        // Start a 60 sec timer on the UI
        verifyingtxt = (TextView) findViewById(R.id.number_ver1_verifying_text);
        timerCountDownTxt = (TextView) findViewById(R.id.number_ver1_countdown_timer);
        Button verifyBtn = (Button) findViewById(R.id.number_ver1_bt_next);

        verifyBtn.setVisibility(View.INVISIBLE);
        verifyingtxt.setVisibility(View.VISIBLE);
        timerCountDownTxt.setVisibility(View.VISIBLE);

        startTimer(60000);
    }

    private void startTimer(int millisec) {
        CountDownTimer counter = new CountDownTimer(millisec, 1000) {

            public void onTick(long millisUntilFinished) {
                timerCountDownTxt.setText((millisUntilFinished / 1000) + " sec");
            }

            public void onFinish() {
                timerCountDownTxt.setText("0 secs");
                verifyingtxt.setText("Sorry, please try again");
            }
        }.start();
    }

    //    --sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        sendBroadcastReceiver = new BroadcastReceiver() {

            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "SMS Sent");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.d(TAG, "Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.d(TAG, "No service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.d(TAG, "Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.d(TAG, "Radio off");
                        break;
                }
            }
        };

        // when message has been delivered
        deliveryBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "SMS Delivered");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.d(TAG, "SMS not delivered");
                        break;
                }
            }
        };
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));
        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveryBroadcastReceiver);
        }catch (IllegalArgumentException ex){
            Log.d(TAG, "exception in unregistering receiver" + ex);
        }
        super.onStop();
    }

}