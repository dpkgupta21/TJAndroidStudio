package com.example.memories;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flotingmenulibrary.FloatingActionsMenu;
import com.example.flotingmenulibrary.FloatingActionsMenu.OnFloatingActionsMenuUpdateListener;
import com.example.memories.audio.CaptureAudio;
import com.example.memories.checkin.CheckInPlacesList;
import com.example.memories.gallery.GalleryBaseActivity;
import com.example.memories.moods.CaptureMoods;
import com.example.memories.newjourney.NewJourney;
import com.example.memories.note.CreateNotes;
import com.example.memories.pastjourney.PastJourneyList;
import com.example.memories.picture.CapturePhotos;
import com.example.memories.profile.ProfileActivity;
import com.example.memories.timeline.Timeline;
import com.example.memories.utility.SessionManager;
import com.example.memories.utility.TJPreferences;
import com.example.memories.video.CaptureVideo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends SlidingActivity {

    private static final String TAG = "<BaseActivity>";
    private static final int REQUEST_CODE_UPDATE_PROFILE = 2;
    private ActionBar actionBar;
    private RelativeLayout fullLayout;
    private FrameLayout contentFrameLayout;
    private SlidingMenu sm;
    private ArrayList<Map<String, String>> featuresList;
    private ListView featuresListView;
    private FloatingActionsMenu mFab;
    private FrameLayout baseActivityContentOverlay;
    private ImageView mProfileImg;
    private TextView mUserName;
    private TextView mUserStatus;

    @Override
    public void setContentView(int layoutResID) {

        Log.d(TAG, "entered base activity");

        fullLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.base_activity, null);
        contentFrameLayout = (FrameLayout) fullLayout.findViewById(R.id.content_activity_frame);

        getLayoutInflater().inflate(layoutResID, contentFrameLayout, true);
        super.setContentView(fullLayout);

        // set the action bar
        actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setIcon(R.drawable.hamburger);
        actionBar.setTitle("Travel Jar");

        // set the Behind View
        setBehindContentView(R.layout.side_menu_drawer);

        // configure the SlidingMenu
        sm = getSlidingMenu();
        sm.setMode(SlidingMenu.LEFT);
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        sm.setShadowWidthRes(R.dimen.shadow_width);
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        sm.setFadeDegree(0.35f);

        // Sliding Side Menu Drawer code
        featuresListView = (ListView) findViewById(R.id.sidemenu_features_list);
        featuresList = new ArrayList<Map<String, String>>();
        Integer[] categoryIconArray = {R.drawable.timeline, R.drawable.map32, R.drawable.stack21,
                R.drawable.cookies, R.drawable.seo47, R.drawable.shopping232};
        String[] hashMapKeys = {"icon", "title"};

        Integer len = getResources().getStringArray(R.array.sidemenu_features_list).length;
        for (int i = 0; i < len; i++) {
            HashMap<String, String> setting = new HashMap<String, String>();
            setting.put("icon", "" + categoryIconArray[i]);
            setting.put("title", getResources().getStringArray(R.array.sidemenu_features_list)[i]);
            featuresList.add(setting);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, featuresList,
                R.layout.side_menu_drawer_item, hashMapKeys, new int[]{R.id.side_menu_cloud_icon,
                R.id.side_menu_cloud_title});
        featuresListView.setAdapter(adapter);

        featuresListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sm.toggle();
                // selected item
                Intent i;
                switch (position) {
                    case 0:
                        i = new Intent(getBaseContext(), Timeline.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getBaseContext(), GalleryBaseActivity.class);
                        startActivity(i);
                        break;
                    case 2:
                        i = new Intent(getBaseContext(), PastJourneyList.class);
                        startActivity(i);
                        break;
                    case 4:
                        i = new Intent(getBaseContext(), ProfileActivity.class);
                        startActivityForResult(i, REQUEST_CODE_UPDATE_PROFILE);
                        break;

                    default:
                        break;
                }
            }
        });

        // update the details in the top half of sliding menu
        // details - name & status & profile picture
        mUserName = (TextView) findViewById(R.id.sidemenu_username);
        mUserStatus = (TextView) findViewById(R.id.sidemenu_status);
        mProfileImg = (ImageView) findViewById(R.id.sidemenu_profile_pic);

        mProfileImg.setImageBitmap(BitmapFactory
                .decodeFile(TJPreferences.getProfileImgPath(this)));
        mUserName.setText(TJPreferences.getUserName(this));
        mUserStatus.setText(TJPreferences.getUserStatus(this));

        // Configure floating action button
        mFab = (FloatingActionsMenu) findViewById(R.id.multiple_actions_down);
        baseActivityContentOverlay = (FrameLayout) findViewById(R.id.content_activity_overlay);

        mFab.setOnFloatingActionsMenuUpdateListener(new OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                Log.d(TAG, "FAB expanded");
                baseActivityContentOverlay.setBackgroundColor(getResources().getColor(
                        R.color.black_semi_transparent));

            }

            @Override
            public void onMenuCollapsed() {
                Log.d(TAG, "FAB collapsed");
                baseActivityContentOverlay.setBackgroundColor(getResources().getColor(
                        R.color.transparent));

            }
        });

        // Remove the overlay if clicked anywhere other than FAB buttons
        baseActivityContentOverlay.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // ignore all touch events
                if (mFab.isExpanded()) {
                    mFab.collapse();
                    return true;
                }
                return false;
            }
        });

    }

    // On clicking on options in FAB button
    // Take them to their respective modules/screens
    public void onFABClick(View v) {
        // TODO Auto-generated method stub
        Intent i;
        if (mFab.isExpanded()) {
            mFab.collapse();
        }
        switch (v.getId()) {
            case R.id.button_mood:
                Log.d(TAG, "set a mood clicked");
                i = new Intent(getApplicationContext(), CaptureMoods.class);
                startActivity(i);
                break;
            case R.id.button_checkin:
                Log.d(TAG, "checkin clicked");
                i = new Intent(getApplicationContext(), CheckInPlacesList.class);
                startActivity(i);
                break;
            case R.id.button_photo:
                Log.d(TAG, "photo clicked");
                i = new Intent(getApplicationContext(), CapturePhotos.class);
                startActivity(i);
                break;
            case R.id.button_note:
                i = new Intent(this, CreateNotes.class);
                startActivity(i);
                Log.d(TAG, "note clicked");
                break;
            case R.id.button_video:
                Log.d(TAG, "video clicked");
                i = new Intent(getApplicationContext(), CaptureVideo.class);
                startActivity(i);
                break;
            case R.id.button_audio:
                Log.d(TAG, "audio clicked");
                i = new Intent(getApplicationContext(), CaptureAudio.class);
                startActivity(i);
                break;
        }
    }

    public void goToNext() {
        Toast.makeText(getApplicationContext(), "Next screen", Toast.LENGTH_LONG).show();
    }

    public void newJourney(View v) {
        Intent i = new Intent(getApplicationContext(), NewJourney.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_items, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                sm.toggle(true);
                return true;
            case R.id.action_next:
                goToNext();
                return true;
            case R.id.action_logout:
                Log.d(TAG, "logging out now");
                SessionManager session = new SessionManager(getApplicationContext());
                session.logoutUser(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // To show icons plus text in overflow menu
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",
                            Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, "onMenuOpened", e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "inside on activity results " + requestCode + " " + resultCode + data + RESULT_OK);
        if (requestCode == REQUEST_CODE_UPDATE_PROFILE && resultCode == RESULT_OK && data != null) {
            Log.d(TAG, "inside on activity results");
            boolean isProfilePicUpdated = data.getBooleanExtra("PROFILE_PICTURE_UPDATED", false);
            boolean isUserNameUpdated = data.getBooleanExtra("USER_NAME_UPDATED", false);
            if (isProfilePicUpdated) {
                Log.d(TAG, "profile picture is updated");
                mProfileImg.setImageBitmap(BitmapFactory
                        .decodeFile(TJPreferences.getProfileImgPath(this)));
            }
            if (isUserNameUpdated) {
                Log.d(TAG, "user name is updated");
                mUserName.setText(TJPreferences.getUserName(this));
            }
            mUserStatus.setText(TJPreferences.getUserStatus(this));
            Timeline.mAdapter.notifyDataSetChanged();
        }
    }

}
