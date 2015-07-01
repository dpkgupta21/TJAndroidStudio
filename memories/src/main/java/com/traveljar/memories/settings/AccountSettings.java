package com.traveljar.memories.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.TJPreferences;

public class AccountSettings extends AppCompatActivity implements UpdatePhoneDialog.OnPhoneUpdateListener{

    private Button updatePhoneBtn;
    private Button updatePasswordBtn;
    private TextView emailTxt;
    private TextView phoneTxt;

    private static final String TAG = "AccountSettings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Account Settings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        setUpToolbar();

        phoneTxt = (TextView)findViewById(R.id.user_phone);
        emailTxt = (TextView)findViewById(R.id.user_email);
        updatePhoneBtn = (Button)findViewById(R.id.update_phone_button);
        updatePasswordBtn = (Button)findViewById(R.id.update_password_button);

        phoneTxt.setText(TJPreferences.getPhone(this));
        emailTxt.setText(TJPreferences.getEmail(this));

        updatePhoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!HelpMe.isNetworkAvailable(AccountSettings.this)) {
                    Toast.makeText(AccountSettings.this, "Network unavailable, please connect to the network and try again", Toast.LENGTH_SHORT).show();
                }else {
                    new UpdatePhoneDialog().show(getSupportFragmentManager(), "UPDATE_PHONE");
                }
            }
        });

        updatePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!HelpMe.isNetworkAvailable(AccountSettings.this)) {
                    Toast.makeText(AccountSettings.this, "Network unavailable, please connect to the network and try again", Toast.LENGTH_SHORT).show();
                }else {
                    new UpdatePasswordDialog().show(getSupportFragmentManager(), "UPDATE_PASSWORD");
                }
            }
        });
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Account Settings");

        toolbar.setNavigationIcon(R.drawable.ic_next);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountSettings.this.finish();
            }
        });

    }

    @Override
    public void onPhoneUpdate() {
        Log.d(TAG, "onphoneupdate " + TJPreferences.getPhone(this));
        phoneTxt.setText(TJPreferences.getPhone(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
