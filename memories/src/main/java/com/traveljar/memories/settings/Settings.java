package com.traveljar.memories.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.traveljar.memories.R;
import com.traveljar.memories.utility.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ankit on 9/6/15.
 */
public class Settings extends AppCompatActivity{
    protected static final String TAG = "<SettingsActivity>";
    List<String> settingsTitlesList = new ArrayList<>();
    List<String> settingsIconIdsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setUpToolbar();

        createSettings();
    }

    private void createSettings() {
        initializeSettingsTitles();
        initializeSettingsIconIdsList();
        List<HashMap<String, String>> landingSettingsList = new ArrayList<HashMap<String, String>>();

        int i = 0;
        for(String s : settingsTitlesList){
            HashMap<String, String> setting = new HashMap<>();
            setting.put("icon", "" + settingsIconIdsList.get(i));
            setting.put("title", s);
            landingSettingsList.add(setting);
            i++;
        }

        String[] hashMapKeys = { "icon", "title" };
        int[] layoutElementIds = { R.id.landingSettingsIcon, R.id.landingSettingsTitle };
        Log.d(TAG, "hash key map" + hashMapKeys);
        SimpleAdapter adapter = new SimpleAdapter(this, landingSettingsList, R.layout.settings_list_item, hashMapKeys, layoutElementIds);
        ListView settingsList = (ListView) findViewById(R.id.landingSettingsListView);
        settingsList.setAdapter(adapter);
        setClickListenerOnSettingsList(settingsList);
    }

    private void setUpToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView title = (TextView)toolbar.findViewById(R.id.toolbar_title);
        title.setText("Account Settings");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.this.finish();
            }
        });

    }

    private void setClickListenerOnSettingsList(ListView settingsList) {
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "settings item clicked = +" + position);
                Intent i;
                switch (position) {
                    case 0:
                        //Account Settings
                        Intent intent = new Intent(Settings.this, AccountSettings.class);
                        startActivity(intent);
                        break;
                    case 1:
                        //General Settings
                        break;
                    case 2:
                        //About Us
                        AboutUsDialog aboutUsDialog = new AboutUsDialog();
                        aboutUsDialog.show(getSupportFragmentManager(), "ABOUT US");
                        break;
                    case 3:
                        //Logout
                        new AlertDialog.Builder(Settings.this)
                                .setTitle("Logout")
                                .setMessage("Are you sure you want to logout?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new SessionManager(Settings.this).logoutUser(Settings.this);
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }
            }

        });

    }

    private void initializeSettingsTitles() {
        String[] array = getResources().getStringArray(R.array.settings_items);
        for(String s : array){
            settingsTitlesList.add(s);
        }
    }

    private void initializeSettingsIconIdsList() {
        settingsIconIdsList.add(String.valueOf(R.drawable.ic_account_circle_black_24dp));
        settingsIconIdsList.add(String.valueOf(R.drawable.ic_settings_applications_black_24dp));
        settingsIconIdsList.add(String.valueOf(R.drawable.ic_info_black_24dp));
        settingsIconIdsList.add(String.valueOf(R.drawable.ic_power_settings_new_black_24dp));
        settingsIconIdsList.add(String.valueOf(R.drawable.logout));
        settingsIconIdsList.add(String.valueOf(R.drawable.logout));
        settingsIconIdsList.add(String.valueOf(R.drawable.logout));
        settingsIconIdsList.add(String.valueOf(R.drawable.logout));

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
