package com.traveljar.memories.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.traveljar.memories.R;
import com.traveljar.memories.activejourney.ActivejourneyList;
import com.traveljar.memories.utility.Constants;
import com.traveljar.memories.utility.HelpMe;
import com.traveljar.memories.utility.SessionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashScreen extends Activity {
    private static final String TAG = "<SplashScreen>";
    private SessionManager session;
    private ViewPager mViewPager;
    private SplashScreenPagerAdapter mAdapter;
    private Map<Integer, String> mPictures;
    private List<Integer> mPictureIdsList;

    private RadioGroup mSwipeIndicator;
    // List of Ids of radio buttons for displaying the dot of currently displayed picture
    private List<Integer> mRadioButtonIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        // Session class instance
        session = new SessionManager(getApplicationContext());
        createTravelJarInitials();

        // check if already logged in

        if (session.isLoggedIn(this)) {
            Log.d(TAG, "since already logged in");
            Log.d(TAG, "SplashScreen ==> TimelineFragment");
            Intent intent = new Intent(getBaseContext(), ActivejourneyList.class);
            startActivity(intent);
            finish();
        }

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        populateRadioButtonIds();
        populateSplashBackgrounds();

        mAdapter = new SplashScreenPagerAdapter(this, mPictures, mPictureIdsList);
        mViewPager.setAdapter(mAdapter);

        mSwipeIndicator = (RadioGroup)findViewById(R.id.swipe_indicator_radio_group);
        mSwipeIndicator.check(mRadioButtonIds.get(0));

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mSwipeIndicator.clearCheck();
                mSwipeIndicator.check(mRadioButtonIds.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void populateRadioButtonIds(){
        mRadioButtonIds = new ArrayList<>();
        mRadioButtonIds.add(R.id.pic1_indicator);
        mRadioButtonIds.add(R.id.pic2_indicator);
        mRadioButtonIds.add(R.id.pic3_indicator);
    }

    private void populateSplashBackgrounds(){
        mPictures = new HashMap<>();

        List<String> mPictureTextList = new ArrayList<>();
        mPictureTextList.add("Capture all type of memories. Not just photos. Travel Jar can take anything");
        mPictureTextList.add("Share those wonderful moments in the form of a small video created AUTOMATICALLY!.");
        mPictureTextList.add("No need to chase people for pictures anymore. Travel Jar automatically syncs everything");

        mPictureIdsList = new ArrayList<>();
        mPictureIdsList.add(R.drawable.img_splash_1);
        mPictureIdsList.add(R.drawable.img_splash_3);
        mPictureIdsList.add(R.drawable.img_splash_2);

        int i = 0;
        for(Integer a : mPictureIdsList){
            mPictures.put(a, mPictureTextList.get(i));
            i++;
        }

    }

    private void createTravelJarInitials() {
        // Create traveljar pictures folder
        File file;
        file = new File(Constants.TRAVELJAR_FOLDER_PICTURE);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar VIDEO folder
        file = new File(Constants.TRAVELJAR_FOLDER_VIDEO);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar AUDIO folder
        file = new File(Constants.TRAVELJAR_FOLDER_AUDIO);
        if (!file.exists()) {
            file.mkdirs();
        }
        // Create traveljar BUDDY PROFILES folder
        file = new File(Constants.TRAVELJAR_FOLDER_BUDDY_PROFILES);
        if (!file.exists()) {
            file.mkdirs();
        }

        //If gumnaam image doesn't exists than create one
        HelpMe.createImageIfNotExist(this);
    }

    public void goToSignUp(View v) {
        Intent i = new Intent(getBaseContext(), SignUp.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void goToSignIn(View v) {
        Intent i = new Intent(getBaseContext(), SignIn.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}

